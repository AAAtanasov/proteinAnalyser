package de.ovgu.msdatastream.brukerraw.sqllite;

import java.util.ArrayList;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

public class BrukerPrecusor {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
	private Double monoisotopicMz;
	private Double intensity;
	private Integer precursorParent;
	// PasefItems
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// Spectrum
	private Spectrum spectrum;
	
	public BrukerPrecusor(BrukerPasefFrameMSMSInfo pasefItem) {
		// File
		bkFile = pasefItem.bkFile;
		// Precursor
		precursorId = pasefItem.precursorId;
		monoisotopicMz = pasefItem.monoisotopicMz;
		intensity = pasefItem.intensity;
		precursorParent = pasefItem.precursorParent;
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public Spectrum getSpectrum() {
		// init empty spectrum
		Spectrum spectrum = new Spectrum();
		// retrieve data from each frame and append
		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			Spectrum newSpectrum = bkFile.readRawdata(bkFile.getFrame(pasefItem.frameId), pasefItem.scanNumBegin, pasefItem.scanNumEnd);
			spectrum.appendData(newSpectrum);
		}
		return spectrum; 
	}
	
	
}

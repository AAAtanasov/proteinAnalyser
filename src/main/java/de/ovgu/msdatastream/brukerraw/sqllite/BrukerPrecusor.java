package de.ovgu.msdatastream.brukerraw.sqllite;

import java.util.ArrayList;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

public class BrukerPrecusor {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	private Integer precursorId;
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
		// TODO
		return null;
	}
	
	
}

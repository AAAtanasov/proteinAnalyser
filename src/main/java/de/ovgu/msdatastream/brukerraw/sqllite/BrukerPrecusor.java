package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BrukerPrecusor implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
	private Double monoisotopicMz;
	private Double intensity;
	private Integer precursorParent;
	private Integer charge;
	// PasefItems
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// Spectrum
	private Spectrum spectrum;
	
	public BrukerPrecusor(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Precursor
		precursorId = rs.getInt("Precursor");
//		monoisotopicMz = pasefItem.monoisotopicMz;
//		intensity = pasefItem.intensity;
//		precursorParent = pasefItem.precursorParent;
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
		charge = rs.getInt("Charge");
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
			spectrum.scanBegin = newSpectrum.scanBegin;
			spectrum.scanEnd = newSpectrum.scanEnd;
			spectrum.frameId = newSpectrum.frameId;
			spectrum.rtinseconds = newSpectrum.rtinseconds;

		}
		spectrum.precursorMZ = this.monoisotopicMz;
		spectrum.precursorINT = this.intensity;
		spectrum.charge = this.charge;
		return spectrum; 
	}
	
	
}

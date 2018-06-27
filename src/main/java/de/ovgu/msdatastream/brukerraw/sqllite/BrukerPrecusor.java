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
		monoisotopicMz = rs.getDouble("MonoisotopicMz");
		intensity = rs.getDouble("Intensity");;
//		precursorParent = pasefItem.precursorParent;
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
		charge = rs.getInt("Charge");
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public ArrayList<Spectrum> getSpectrum() {
		ArrayList<Spectrum> result = new ArrayList<Spectrum>();
		// init empty spectrum
		// retrieve data from each frame and append

		// multiple precursors -> should be batch ?
		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			Spectrum spectrum = new Spectrum();
			BrukerFrame frame = bkFile.getFrame(pasefItem.frameId);

			Spectrum newSpectrum = bkFile.readRawdata(frame, pasefItem.scanNumBegin, pasefItem.scanNumEnd);
			spectrum.appendData(newSpectrum);
			spectrum.scanBegin = pasefItem.scanNumBegin;
			spectrum.scanEnd = pasefItem.scanNumEnd;
			spectrum.frameId = pasefItem.frameId;
			spectrum.precursorId = pasefItem.precursorId;
			spectrum.rtinseconds = frame.time;

			spectrum.precursorMZ = this.monoisotopicMz;
			spectrum.precursorINT = this.intensity;
			spectrum.charge = this.charge;
			spectrum.polarity = frame.polarity;
			result.add(spectrum);

		}

		return result;
	}
	
	
}

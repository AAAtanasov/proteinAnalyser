package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrukerPasefFrameMSMSInfo implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	public Integer pasefFrameMSMSInfoId;
	// Frame
	public Integer frameId;

	// PasefMSMSInfo
	public Integer scanNumBegin;
	public Integer scanNumEnd;
	// Precursor
	public Integer precursorId;
	public Double isolationMz;
	public Double intensity;
	public Integer precursorParent;
	// Spectrum
	private Spectrum spectrum;

	public BrukerPasefFrameMSMSInfo(BrukerRawFormatWrapper brkFile, ResultSet rs) throws SQLException {
		// File
		bkFile = brkFile;
		pasefFrameMSMSInfoId = rs.getInt("PasefFrameMSMSInfo");
		// Frame
		frameId = rs.getInt("Frame");
		//polarity = rs.getString("Polarity");

		// PasefMSMSInfo
		scanNumBegin = rs.getInt("ScanNumBegin");
		scanNumEnd = rs.getInt("ScanNumEnd");
		// Precursor
		precursorId = rs.getInt("Precursor");

	}
	
	public Spectrum getSpectrum() {
		// read specific range from specific frame
		return  bkFile.readRawdata(bkFile.getFrame(this.frameId), this.scanNumBegin, this.scanNumEnd);
	}

}

package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrukerPasefFrameMSMSInfo {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer msmsType;
	public Integer timsId;
	public Integer numScans;
	public Integer numPeaks;
	// PasefMSMSInfo
	public Integer scanNumBegin;
	public Integer scanNumEnd;
	// Precursor
	public Integer precursorId;
	public Double monoisotopicMz;
	public Double intensity;
	public Integer precursorParent;
	// Spectrum
	private Spectrum spectrum;

	public BrukerPasefFrameMSMSInfo(BrukerRawFormatWrapper brkFile, ResultSet rs, SQLWrapper sql) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
		frameId = rs.getInt("Frame");
		polarity = rs.getString("Polarity");
		msmsType = rs.getInt("MsMsType");
		timsId = rs.getInt("TimsId");
		numScans = rs.getInt("NumScans");
		numPeaks = rs.getInt("NumPeaks");
		// PasefMSMSInfo
		scanNumBegin = rs.getInt("ScanNumBegin");
		scanNumEnd = rs.getInt("ScanNumEnd");
		// Precursor
		precursorId = rs.getInt("Precursor");
		monoisotopicMz = (double) rs.getDouble("MonoisotopicMz");
		intensity = (double) rs.getDouble("Intensity");
		precursorParent = rs.getInt("Parent");
	}
	
	public Spectrum getSpectrum() {
		// read specific range from specific frame
		return  bkFile.readRawdata(bkFile.getFrame(this.frameId), this.scanNumBegin, this.scanNumEnd);
	}

}

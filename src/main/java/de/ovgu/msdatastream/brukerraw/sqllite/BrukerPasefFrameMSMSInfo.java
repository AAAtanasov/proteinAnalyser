package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.ovgu.msdatastream.model.Spectrum;


import java.sql.ResultSet;
import java.sql.SQLException;

public class BrukerPasefFrameMSMSInfo implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer msmsType;
	public Integer timsId;
	public Integer numScans;
	public Integer numPeaks;
	public Float retentionTime; //Time in Frames table

	// PasefMSMSInfo
	public Integer scanNumBegin;
	public Integer scanNumEnd;
	// Precursor
	public Integer precursorId;

	public Double monoisotopicMz;
	public Double averageMz;
	public Double intensity;
	public Integer precursorParent;
	public Integer precursorCharge;
	// PeakListContainer
	private PeakListContainer peakListContainer;


	public BrukerPasefFrameMSMSInfo(BrukerRawFormatWrapper brkFile, ResultSet rs) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
		frameId = rs.getInt("Frame");

		polarity = rs.getString("Polarity");
		msmsType = rs.getInt("MsMsType");
		timsId = rs.getInt("TimsId");
		numScans = rs.getInt("NumScans");
		numPeaks = rs.getInt("NumPeaks");
		retentionTime = rs.getFloat("Time");

		// PasefMSMSInfo
		scanNumBegin = rs.getInt("ScanNumBegin");
		scanNumEnd = rs.getInt("ScanNumEnd");
		// Precursor
		precursorId = rs.getInt("Precursor");

		monoisotopicMz = (double) rs.getDouble("MonoisotopicMz");
		averageMz = (double) rs.getDouble("AverageMz");
		intensity = (double) rs.getDouble("Intensity");
		precursorParent = rs.getInt("Parent");
		precursorCharge = rs.getInt("Charge");

	}
	

	public PeakListContainer getPeakListContainer() {
		return peakListContainer;
		// TODO: dead method?
	}

	public Spectrum getSpecificSpectrum() {
		// read specific range from specific frame
		BrukerFrame frame = bkFile.getFrame(this.frameId);
		BrukerPrecusor precusor = bkFile.getPrecursor(this.precursorId);
		Spectrum spectrum = bkFile.readRawdata(frame, this.scanNumBegin, this.scanNumEnd);

		spectrum.scanBegin = this.scanNumBegin;
		spectrum.scanEnd = this.scanNumEnd;
		spectrum.frameId = this.frameId;
		spectrum.precursorId = this.precursorId;
		spectrum.rtinseconds = frame.time;
		spectrum.precursorMZ = precusor.getMonoisotopicMz();
		spectrum.precursorINT = precusor.getIntensity();
		spectrum.charge = precusor.getCharge();
		spectrum.polarity = frame.polarity;

		return spectrum;
	}

	public Spectrum[] getSpectrum(){
		return new Spectrum[]{ getSpecificSpectrum() };
	}

}

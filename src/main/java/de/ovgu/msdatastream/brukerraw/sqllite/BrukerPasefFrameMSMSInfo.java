package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrukerPasefFrameMSMSInfo implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;

	// PasefMSMSInfo
	public Integer scanNumBegin;
	public Integer scanNumEnd;
	// Precursor
	public Integer precursorId;

	public BrukerPasefFrameMSMSInfo(BrukerRawFormatWrapper brkFile, ResultSet rs) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
		frameId = rs.getInt("Frame");
		// PasefMSMSInfo
		scanNumBegin = rs.getInt("ScanNumBegin");
		scanNumEnd = rs.getInt("ScanNumEnd");
		// Precursor
		precursorId = rs.getInt("Precursor");
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

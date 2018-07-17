package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BrukerFrame implements ISpectrum {

	// File
	private BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer msmsType;
	public Integer timsId;
	public Integer numScans;
	public Integer numPeaks;
	public Double time;
	// PasefItems
	private Queue<BrukerPasefFrameMSMSInfo> pasefItems;
	// Spectrum
	private Spectrum spectrum;
	
	// TODO: Implementation for remaining data

	public BrukerFrame(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
		frameId = rs.getInt("Frame");
		polarity = rs.getString("Polarity");
		time = rs.getDouble("Time");
		numScans = rs.getInt("NumScans");
		numPeaks = rs.getInt("NumPeaks");
		// PasefItems
		pasefItems = new LinkedList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public Spectrum[] getSpectrum() {
		Spectrum[] result = new Spectrum[pasefItems.size()];
		int index = 0;

		for (BrukerPasefFrameMSMSInfo bs: this.pasefItems) {
			result[index] = bs.getSpecificSpectrum();
			index++;
		}

		return result;
	}
	
}

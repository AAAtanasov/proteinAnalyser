package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
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
//		msmsType = pasefItem.msmsType;
//		timsId = pasefItem.timsId;
		numScans = rs.getInt("NumScans");
		numPeaks = rs.getInt("NumPeaks");
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public ArrayList<Spectrum> getSpectrum() {
		ArrayList<Spectrum> result = new ArrayList<Spectrum>();

		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			ArrayList<Spectrum> spectrums = pasefItem.getSpectrum();
			result.addAll(spectrums);
		}

		return result;
	}
	
}

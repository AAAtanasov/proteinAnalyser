package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class BrukerFrame implements ISpectrum {

	// File
	private BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer numScans;
	public Integer numPeaks;
	public Float retentionTime;
	public Double time;
	
	// PasefItems
	private LinkedList<BrukerPasefFrameMSMSInfo> pasefItems;
	// PeakListContainer
	private PeakListContainer peakListContainer;
	
	// TODO: are these correct types?
	private Integer msmsType;
	private Integer timsId;
	
	// TODO: Implementation for remaining data

//	private Queue<BrukerPasefFrameMSMSInfo> pasefItems;


	public BrukerFrame(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
		frameId = pasefItem.frameId;
		polarity = pasefItem.polarity;
		msmsType = pasefItem.msmsType;
		timsId = pasefItem.timsId;
		numScans = pasefItem.numScans;
		numPeaks = pasefItem.numPeaks;
		retentionTime = pasefItem.retentionTime;

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
	

	public PeakListContainer getPeakListContainer() {
		// read from 0 to numScans (= entire frame)
		return bkFile.readRawdata(this, 0, this.numScans);
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

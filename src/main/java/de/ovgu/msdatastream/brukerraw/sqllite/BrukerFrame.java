package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;

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
<<<<<<< HEAD
	public Float retentionTime;
=======
	public Double time;
>>>>>>> refs/heads/merge_branch_streaming_processing
	// PasefItems
<<<<<<< HEAD
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// PeakListContainer
	private PeakListContainer peakListContainer;
	
	// TODO: Implementation for remaining data
=======
	private Queue<BrukerPasefFrameMSMSInfo> pasefItems;
>>>>>>> refs/heads/merge_branch_streaming_processing

	public BrukerFrame(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Frame
<<<<<<< HEAD
		frameId = pasefItem.frameId;
		polarity = pasefItem.polarity;
		msmsType = pasefItem.msmsType;
		timsId = pasefItem.timsId;
		numScans = pasefItem.numScans;
		numPeaks = pasefItem.numPeaks;
		retentionTime = pasefItem.retentionTime;
=======
		frameId = rs.getInt("Frame");
		polarity = rs.getString("Polarity");
		time = rs.getDouble("Time");
		numScans = rs.getInt("NumScans");
		numPeaks = rs.getInt("NumPeaks");
>>>>>>> refs/heads/merge_branch_streaming_processing
		// PasefItems
		pasefItems = new LinkedList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
<<<<<<< HEAD
	public PeakListContainer getPeakListContainer() {
		// read from 0 to numScans (= entire frame)
		return bkFile.readRawdata(this, 0, this.numScans);
=======
	public Spectrum[] getSpectrum() {
		Spectrum[] result = new Spectrum[pasefItems.size()];
		int index = 0;

		for (BrukerPasefFrameMSMSInfo bs: this.pasefItems) {
			result[index] = bs.getSpecificSpectrum();
			index++;
		}

		return result;
>>>>>>> refs/heads/merge_branch_streaming_processing
	}
	
}

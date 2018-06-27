package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.util.ArrayList;

public class BrukerFrame implements SpectrumInterface {

	// File
	private BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer msmsType;
	public Integer timsId;
	public Integer numScans;
	public Integer numPeaks;
	public String time;
	// PasefItems
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// Spectrum
	private Spectrum spectrum;
	
	// TODO: Implementation for remaining data

	public BrukerFrame(BrukerPasefFrameMSMSInfo pasefItem) {
		// File
		bkFile = pasefItem.bkFile;
		// Frame
		frameId = pasefItem.frameId;
		polarity = pasefItem.polarity;
		msmsType = pasefItem.msmsType;
		timsId = pasefItem.timsId;
		numScans = pasefItem.numScans;
		numPeaks = pasefItem.numPeaks;
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public Spectrum getSpectrum() {
		// read from 0 to numScans (= entire frame)
		return bkFile.readRawdata(this, 0, this.numScans);
	}
	
}

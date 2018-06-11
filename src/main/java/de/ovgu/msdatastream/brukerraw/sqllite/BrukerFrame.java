package de.ovgu.msdatastream.brukerraw.sqllite;

import java.util.ArrayList;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

public class BrukerFrame {

	// File
	private BrukerRawFormatWrapper bkFile;
	// Frame
	public Integer frameId;
	public String polarity;
	public Integer msmsType;
	public Integer timsId;
	public Integer numScans;
	public Integer numPeaks;
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
		// TODO
		bkFile.readRawdata2(this);
		return null;
	}
	
}

package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;

import java.util.ArrayList;

public class BrukerPrecusor {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
	public Double monoisotopicMz;
	public Double intensity;
	private Integer precursorParent;
	public Integer precursorCharge;

	public ArrayList<BrukerPasefFrameMSMSInfo> getPasefItems() {
		return pasefItems;
	}

	// PasefItems
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// PeakListContainer
	private PeakListContainer peakListContainer;
	
	public BrukerPrecusor(BrukerPasefFrameMSMSInfo pasefItem) {
		// File
		bkFile = pasefItem.bkFile;
		// Precursor
		precursorId = pasefItem.precursorId;
		monoisotopicMz = pasefItem.monoisotopicMz;
		if (monoisotopicMz == 0.0) {
			monoisotopicMz = pasefItem.averageMz;
		}
		intensity = pasefItem.intensity;
		precursorParent = pasefItem.precursorParent;
		precursorCharge = pasefItem.precursorCharge;
		if (precursorCharge == 0)  {
			precursorCharge = 1;
		}
		
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
	public PeakListContainer getPeakListContainer() {
		// init empty peakListContainer
		 peakListContainer = new PeakListContainer();
		// retrieve data from each frame and append
		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			PeakListContainer newPeakListContainer = bkFile.readRawdata(bkFile.getFrame(pasefItem.frameId), pasefItem.scanNumBegin, pasefItem.scanNumEnd);
			peakListContainer.appendData(newPeakListContainer);
		}
		return peakListContainer;
	}
	
	
}

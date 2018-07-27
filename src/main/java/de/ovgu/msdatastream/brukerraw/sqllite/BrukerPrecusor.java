package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;

import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class BrukerPrecusor implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
<<<<<<< HEAD
	public Double monoisotopicMz;
	public Double intensity;
	private Integer precursorParent;
	public Integer precursorCharge;

	public ArrayList<BrukerPasefFrameMSMSInfo> getPasefItems() {
		return pasefItems;
	}

=======
	private Double monoisotopicMz;
	private Double intensity;
	private Integer charge;
>>>>>>> refs/heads/merge_branch_streaming_processing
	// PasefItems
<<<<<<< HEAD
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// PeakListContainer
	private PeakListContainer peakListContainer;
	
	public BrukerPrecusor(BrukerPasefFrameMSMSInfo pasefItem) {
=======
	private Queue<BrukerPasefFrameMSMSInfo> pasefItems;

	public BrukerPrecusor(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
>>>>>>> refs/heads/merge_branch_streaming_processing
		// File
		bkFile = brkFile;
		// Precursor
<<<<<<< HEAD
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
		
=======
		precursorId = rs.getInt("Precursor");
		monoisotopicMz = rs.getDouble("MonoisotopicMz");
		intensity = rs.getDouble("Intensity");;
>>>>>>> refs/heads/merge_branch_streaming_processing
		// PasefItems
		pasefItems = new LinkedList<BrukerPasefFrameMSMSInfo>();
		pasefItems.add(pasefItem);
		charge = rs.getInt("Charge");
	}

	public double getMonoisotopicMz(){
		return  this.monoisotopicMz;
	}

	public double getIntensity() {
		return  this.intensity;
	}

	public Integer getCharge() {
		return this.charge;
	}

	public void addPasefItem(BrukerPasefFrameMSMSInfo pasefItem) {
		pasefItems.add(pasefItem);
	}
	
<<<<<<< HEAD
	public PeakListContainer getPeakListContainer() {
		// init empty peakListContainer
		 peakListContainer = new PeakListContainer();
		// retrieve data from each frame and append
		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			PeakListContainer newPeakListContainer = bkFile.readRawdata(bkFile.getFrame(pasefItem.frameId), pasefItem.scanNumBegin, pasefItem.scanNumEnd);
			peakListContainer.appendData(newPeakListContainer);
=======
	public Spectrum[] getSpectrum() {
		Spectrum[] result = new Spectrum[pasefItems.size()];
		int index = 0;

		for (BrukerPasefFrameMSMSInfo bs: this.pasefItems) {
			result[index] = bs.getSpecificSpectrum();
			index++;
>>>>>>> refs/heads/merge_branch_streaming_processing
		}
<<<<<<< HEAD
		return peakListContainer;
=======

		return result;
>>>>>>> refs/heads/merge_branch_streaming_processing
	}
	
	
}

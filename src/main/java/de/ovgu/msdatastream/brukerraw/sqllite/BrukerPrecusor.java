package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BrukerPrecusor implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
	private Double monoisotopicMz;
	private Double intensity;
	private Integer precursorParent;
	private Integer charge;
	// PasefItems
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	// Spectrum
	private Spectrum spectrum;
	
	public BrukerPrecusor(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Precursor
		precursorId = rs.getInt("Precursor");
		monoisotopicMz = rs.getDouble("MonoisotopicMz");
		intensity = rs.getDouble("Intensity");;
//		precursorParent = pasefItem.precursorParent;
		// PasefItems
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
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
	
	public ArrayList<Spectrum> getSpectrum() {
		ArrayList<Spectrum> result = new ArrayList<Spectrum>();
		// init empty spectrum
		// retrieve data from each frame and append

		for (BrukerPasefFrameMSMSInfo pasefItem : this.pasefItems) {
			ArrayList<Spectrum> spectrums = pasefItem.getSpectrum();
			result.addAll(spectrums);

		}

		return result;
	}
	
	
}

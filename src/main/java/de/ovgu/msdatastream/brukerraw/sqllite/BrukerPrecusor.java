package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class BrukerPrecusor implements ISpectrum {

	// File
	public BrukerRawFormatWrapper bkFile;
	// Precursor
	public Integer precursorId;
	private Double monoisotopicMz;
	private Double intensity;
	private Integer charge;
	// PasefItems
	private Queue<BrukerPasefFrameMSMSInfo> pasefItems;

	public BrukerPrecusor(BrukerRawFormatWrapper brkFile, ResultSet rs, BrukerPasefFrameMSMSInfo pasefItem) throws SQLException {
		// File
		bkFile = brkFile;
		// Precursor
		precursorId = rs.getInt("Precursor");
		monoisotopicMz = rs.getDouble("MonoisotopicMz");
		intensity = rs.getDouble("Intensity");;
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

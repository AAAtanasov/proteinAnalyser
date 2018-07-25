package de.ovgu.msdatastream.model;

import java.util.Arrays;

public class Spectrum {

	public Double precursorMZ;
	public Double precursorINT;

	public double[] mzArray;
	public int[] intensitiesArray;

	public Double rtinseconds;
	public int scanBegin;
	public int scanEnd;
	public Integer frameId;
	public String polarity;
	public int charge;
	public Integer precursorId;


	public Spectrum() {
		// intialize empty spectrum
		mzArray = new double[0];
		intensitiesArray = new int[0];
	}

	public Spectrum(double[] mz, int[] intensities) {
		mzArray = mz;
		intensitiesArray = intensities;
	}

	public void appendData(Spectrum newSpectrum) {
		try {
			int oldlength = this.mzArray.length;
			int newArraySize = oldlength + newSpectrum.mzArray.length;
			mzArray = Arrays.copyOf(mzArray, newArraySize);
			intensitiesArray = Arrays.copyOf(intensitiesArray, newArraySize);
			for (int i = oldlength; i < newArraySize; i++) {
				mzArray[i] = newSpectrum.mzArray[i - oldlength];
				intensitiesArray[i] = newSpectrum.intensitiesArray[i - oldlength];
			}
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getSpectrumInformationAsString() {
		StringBuilder tempString = new StringBuilder();
		tempString.append("\n");
		tempString.append("BEGIN IONS");
		tempString.append("\n");
		tempString.append("TITLE: FrameId=" + this.frameId + "_PrecursorId=" + this.precursorId); //FRAME ID + PRECURSOR ID
		tempString.append("\n");
		tempString.append("PEPMASS=" + this.precursorMZ + "\t" + this.precursorINT);
		tempString.append("\n");
		tempString.append("RTINSECONDS=" + this.rtinseconds + "\n");
		tempString.append("SCANS=" + this.scanBegin+ ", " + this.scanEnd);
		tempString.append("\n");
		tempString.append("CHARGE=" + this.charge + this.polarity); //todo : FRAME CHARGE
		tempString.append("\n");
		for (int i = 0; i < this.intensitiesArray.length; i++) {
			if (this.intensitiesArray[i] != 0) {
				tempString.append(this.mzArray[i] + "\t" + this.intensitiesArray[i]);
				tempString.append("\n");
			}
		}
		tempString.append("END IONS");
		tempString.append("\n");
		tempString.append("\n");

		return tempString.toString();
	}
	
}

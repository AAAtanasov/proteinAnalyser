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
	public int frameId;
	public String polarity;
	public int charge;
	public int precursorId;


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
			//FIXME: shouldn't we throw an exception here?
			e.printStackTrace();

		}
	}
	
}

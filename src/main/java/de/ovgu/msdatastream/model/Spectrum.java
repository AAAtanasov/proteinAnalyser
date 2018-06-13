package de.ovgu.msdatastream.model;

import java.util.Arrays;

public class Spectrum {
	
	public Double precursorMZ;
	public Double precursorINT;

	public Double[] mzArray;
	public int[] intensitiesArray;

	public Spectrum() {
		// intialize empty spectrum
		this(new Double[0], new int[0]);
	}
	
	public Spectrum(Double[] mz, int[] intensities) {
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
			e.printStackTrace();
		}
	}
	
}

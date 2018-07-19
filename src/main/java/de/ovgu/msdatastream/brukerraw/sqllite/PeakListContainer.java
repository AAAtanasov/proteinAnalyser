package de.ovgu.msdatastream.brukerraw.sqllite;
import java.util.Arrays;



public class PeakListContainer {

	public double[] mzArray;
	public int[] intensitiesArray;

	public PeakListContainer() {
		// initialize empty spectrum
		this(new double[0], new int[0]);
	}
	
	public PeakListContainer(double[] mz, int[] intensities) {
		mzArray = mz;
		intensitiesArray = intensities;
	}

	public void appendData(PeakListContainer newPeakListContainer) {
		try {

			int oldLength = this.mzArray.length;
			int newArraySize = oldLength + newPeakListContainer.mzArray.length;
			//noinspection deprecation
			mzArray = Arrays.copyOf(mzArray, newArraySize);
            //noinspection deprecation
            intensitiesArray = Arrays.copyOf(intensitiesArray, newArraySize);
			for (int i = oldLength; i < newArraySize; i++) {
				mzArray[i] = newPeakListContainer.mzArray[i - oldLength];
				intensitiesArray[i] = newPeakListContainer.intensitiesArray[i - oldLength];


			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
}

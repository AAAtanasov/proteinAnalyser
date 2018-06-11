package de.ovgu.msdatastream.model;

public class Spectrum {

	public double[] mzArray;
    public int[] intensitiesArray;
    
    @Deprecated
	public int[] indicies;
    @Deprecated
	public int[] intensities;
    
    
    public Spectrum(double[] mz, int[] intensities) {
    	mzArray = mz;
    	intensitiesArray = intensities;
	}

    @Deprecated
	public Spectrum() {
		// TODO Auto-generated constructor stub
	}
}

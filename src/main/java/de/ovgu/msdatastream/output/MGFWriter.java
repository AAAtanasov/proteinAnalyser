package de.ovgu.msdatastream.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.model.Spectrum;

public class MGFWriter {

	private BufferedWriter bw;
		
	// constructor
	public MGFWriter(String filename) {
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeSpectrum(Spectrum spec) {
		// pass
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
	
}

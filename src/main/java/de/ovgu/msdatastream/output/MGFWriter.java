package de.ovgu.msdatastream.output;

import de.ovgu.msdatastream.model.Spectrum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MGFWriter {

	private BufferedWriter bw;
		
	// constructor
	public MGFWriter(String filename) {
		try {
			File file = new File(filename);
			bw = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeSpectrum(String spectrumInformation) throws IOException {
		bw.write(spectrumInformation);
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();
	}
	
}

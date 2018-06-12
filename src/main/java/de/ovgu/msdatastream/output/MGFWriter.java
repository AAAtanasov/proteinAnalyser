package de.ovgu.msdatastream.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	
	public void writeSpectrum(Spectrum spec) throws IOException {
		bw.write("BEGIN IONS");
		bw.write("\n");
		bw.write("PEPMASS=" + spec.precursorMZ + "\t" + spec.precursorINT);
		bw.write("\n");
		for (int i = 0; i < spec.intensitiesArray.length; i++) {
			if (spec.intensitiesArray[i] != 0) {
				bw.write(spec.mzArray[i] + "\t" + spec.intensitiesArray[i]);
				bw.write("\n");
			}
		}
		bw.write("END IONS");
		bw.write("\n");
		bw.write("\n");
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();
	}
	
}

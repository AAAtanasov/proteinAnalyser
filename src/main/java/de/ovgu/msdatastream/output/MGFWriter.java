package de.ovgu.msdatastream.output;

<<<<<<< HEAD
import de.ovgu.msdatastream.brukerraw.sqllite.PeakListContainer;
=======
import de.ovgu.msdatastream.model.Spectrum;
>>>>>>> refs/heads/merge_branch_streaming_processing

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
	
<<<<<<< HEAD
	public void writeSpectrum(PeakListContainer spec) throws IOException {
		bw.write("BEGIN IONS");
		bw.write("\n");
		bw.write("TITLE= ");
		bw.write("\n");
		bw.write("CHARGE=" + "+");
		bw.write("\n");
		bw.write("PEPMASS=" );
		bw.write("\n");
		for (int i = 0; i < spec.intensitiesArray.length; i++) {
			if (spec.intensitiesArray[i] != 0) {
				bw.write(spec.mzArray[i] + " " + spec.intensitiesArray[i]);
				bw.write("\n");
			}
		}
		bw.write("END IONS");
		bw.write("\n");
		bw.write("\n");
=======
	public void writeSpectrum(String spectrumInformation) throws IOException {
		bw.write(spectrumInformation);
>>>>>>> refs/heads/merge_branch_streaming_processing
	}

	public void close() throws IOException {
		bw.flush();
		bw.close();
	}
	
}

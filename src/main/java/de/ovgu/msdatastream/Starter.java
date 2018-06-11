package de.ovgu.msdatastream;

import java.io.IOException;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerFrame;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;

public class Starter {

	public static void main(String[] args) {
		try {
			// inits
			BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(Properties.analysisDir);
			MGFWriter writer = new MGFWriter(Properties.targetFile);
			// loop precursors
			for (BrukerFrame fr : bruker.getFrames()) {
				// write spectrum
				Spectrum spec = fr.getSpectrum();
				if (spec != null) {
					writer.writeSpectrum(spec);
				}
			}
			// close everything
			writer.close();
			bruker.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

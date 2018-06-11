package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerFrame;
import de.ovgu.msdatastream.brukerraw.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;

public class Starter {

	public static void main(String[] args) {
		// inits
		BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(Properties.analysisDir);
		MGFWriter writer = new MGFWriter(Properties.targetFile);
		
		// loop precursors
		for (BrukerFrame fr : bruker.getFrames()) {
			// write spectrum
			Spectrum spec = fr.getSpectrum();
			writer.writeSpectrum(spec);
		}
		// close everything
		writer.close();
		bruker.close();
	}

}

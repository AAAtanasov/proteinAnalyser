package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;

import java.io.IOException;
import java.util.ArrayList;

public class Starter {

	public static void main(String[] args) {
		try {
			// inits
			BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(Properties.analysisDir);
			MGFWriter writer = new MGFWriter(Properties.targetFile);

			// loop precursors
			int precursorcount = 0;

			for (ISpectrum pc : bruker.getPrecursors()) {
				precursorcount++;
				if ((precursorcount % 1000) == 0) {
					System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
					// for testing purpouses
					break;
				}
				// write spectrum
				Spectrum spec = pc.getSpectrum();
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

package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Starter {

	public static void main(String[] args) {
		try {
			// inits
			// will accept directory file from args
			Properties properties = new Properties("F:\\\\proteinProjData\\\\Roh\\\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d");
			BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(properties);
			MGFWriter writer = new MGFWriter(properties.getTargetFile());

			// loop precursors
			int precursorcount = 0;

			for (ISpectrum pc : bruker.getPrecursors()) {
				precursorcount++;
				if ((precursorcount % 1000) == 0) {
					System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
					break;
				}
				// write spectrum
				ArrayList<Spectrum> spectrums = pc.getSpectrum();
				for (Spectrum spec : spectrums){
					if (spec != null) {
						writer.writeSpectrum(spec);
					}
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

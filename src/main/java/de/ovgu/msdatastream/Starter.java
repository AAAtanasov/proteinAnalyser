package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;

import java.io.IOException;

public class Starter {

	public static void main(String[] args) {
		try {
			// inits
			// will accept directory file from args
			ApplicationProperties applicationProperties = new ApplicationProperties();
			BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);
			MGFWriter writer = new MGFWriter(applicationProperties.getTargetFile());

			// loop precursors
			int precursorcount = 0;

			for (ISpectrum pc : bruker.getPrecursors()) {
				precursorcount++;
				if ((precursorcount % 1000) == 0) {
					System.out.println("Writing Precursor " + precursorcount + " of " + bruker.getPrecursors().size());
//					break;
				}
				// write spectrum
				Spectrum[] spectrums = pc.getSpectrum();
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}

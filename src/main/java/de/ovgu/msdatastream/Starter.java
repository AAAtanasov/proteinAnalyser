package de.ovgu.msdatastream;

import de.ovgu.msdatastream.brukerraw.BrukerRawFormatWrapper;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.PeakListContainer;
import de.ovgu.msdatastream.model.PeakListUtilities;
import org.expasy.mzjava.core.io.ms.spectrum.MgfWriter;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTime;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeDiscrete;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.expasy.mzjava.core.ms.spectrum.TimeUnit.SECOND;
import de.ovgu.msdatastream.brukerraw.sqllite.ISpectrum;
import de.ovgu.msdatastream.kafka.KafkaProducerSingleton;
import de.ovgu.msdatastream.model.Spectrum;
import de.ovgu.msdatastream.output.MGFWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Starter {

    
    // this is antons main method, here we find the loop and kafka implementation
    public static void main(String[] args) {
//		String tdfDicrectory = args[0];
//		String targetFile = args[1];
		ApplicationProperties applicationProperties = new ApplicationProperties(tdfDicrectory, targetFile);
		boolean isKafkaProducer = applicationProperties.getIsKafkaProducer();
		Integer maxEmptyIterations = applicationProperties.getMaxEmptyIterations();
		System.out.println("Max empty iterations are : " + maxEmptyIterations);
        boolean hasProcessedSomething;
        Integer threadSleepTime = applicationProperties.getThreadSleepTime();

        BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(applicationProperties);
        File file = new File(Properties.targetFile);
        
		//TODO: calculate elapsed time: use System.nano
		int emptyIterationIndex = 0;
		while (emptyIterationIndex < maxEmptyIterations) {
			try {
				
				if (bruker.checkForNewFrames()) {

					bruker.readMetaData();

					hasProcessedSomething = processData(bruker, isKafkaProducer);

					System.out.println("Processed iteration...");

					if (hasProcessedSomething) {
						emptyIterationIndex = 0;
					} else {
						emptyIterationIndex++;
					}
				}

				Thread.sleep(threadSleepTime);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				break;

			}
		}
    }
    
	// static methods should be replaced
	private static boolean processData(BrukerRawFormatWrapper bruker, boolean isKafkaProducer) throws SQLException, IOException {
		String kafkaTopic = isKafkaProducer ? bruker.getApplicationProperties().getKafkaTopic() : "";
		MGFWriter writer = isKafkaProducer ? null : new MGFWriter(bruker.getApplicationProperties().getTargetFile());
		KafkaProducer<String, String> kafkaProducer = isKafkaProducer ? KafkaProducerSingleton.getSingletonInstance(bruker.getApplicationProperties()) : null;

		int iterationCount = 0;
		Connection connection = bruker.getCurrentConnection();
		PreparedStatement pstmt = connection.prepareStatement(bruker.getApplicationProperties().getInsertProcessedIdsSqlString());

		// Set auto commit to false so we can perform batch commits.
		connection.setAutoCommit(false);

		try {
			for (ISpectrum spectrumContainer : bruker.getPrecursors()) {
				iterationCount++;
				Spectrum[] spectrums = spectrumContainer.getSpectrum();
				
	            MgfWriter mgfWriter = new MgfWriter(file, PeakList.Precision.FLOAT_CONSTANT);

				for (Spectrum spec : spectrums){
					if (spec != null){
						//TODO: time measure, solution as jar, possible optimisation, total time, function time
						
						// XXX ATINS CODE
		                // read peakList for this precursor
		                PeakListContainer spec = pc.getPeakListContainer();

		                /*
		                 * Three tasks to be performed on PeakList
		                 * 1. Binning
		                 * 2. Deisotoping
		                 * 3. Noise filtering
		                 */

		                /* Binning Task**/
		                /*PeakListUtilities.binPeaklist(PeakListContainer peakListContainer) - uses default binTolerance =0.02
		                  PeakListUtilities.binPeaklist(double binTolerance, PeakListContainer peakListContainer)
		                  Returns - HashMap<Double,Double>*/

		                mzPairFinal = PeakListUtilities.binPeaklist(spec);

		                /* Deisotoping Task**/
		                /*PeakListUtilities.removeIsotopePeaklist(HashMap <Double, Double> binnedPeakListMap, Boolean monotonicShape) - without mzTolerance,ppmTolerance
		                  PeakListUtilities.removeIsotopePeaklist(HashMap <Double, Double> binnedPeakListMap, Boolean monotonicShape,double mzTolerance, double ppmTolerance) */

		                //monotonicShape - If true, then monotonically decreasing height of isotope pattern is required
		                //mzTolerance    - absolute tolerance (in m/z)
		                //ppmTolerance   - relative tolerance (in ppm)
		                mzPairFinal = PeakListUtilities.removeIsotopePeaklist(mzPairFinal,false);

		                /* Noise filtering**/
		                double noiseIntLimit = 8.0;
		                //noinspection Convert2MethodRef (As Map.Entry::getKey & Map.Entry::getValue static method references cannot be called within a non-static method)
		                mzPairFinal = (HashMap<Double, Double>) mzPairFinal.entrySet().stream().filter(x -> x.getValue() >= noiseIntLimit).collect(Collectors.toMap(x->x.getKey(), x->x.getValue()));
		   /*             HashMap <Double, Double> copyOfMzPairFinal = new HashMap <>(mzPairFinal);
		                for (Map.Entry<Double,Double> tempPeak:copyOfMzPairFinal.entrySet()
		                     ) {
		                    if(tempPeak.getValue()<noiseIntLimit)
		                    {
		                        mzPairFinal.remove(tempPeak.getKey());
		                    }
		                }*/




		                /*------Creating Mzjava Spectrum Object-----------*/

		                MsnSpectrum spectrum = new MsnSpectrum(spec.mzArray.length, PeakList.Precision.DOUBLE);

		                //Precursor = Peak(double mz, double intensity, int... charge)
		                Peak precursor = new Peak(pc.monoisotopicMz, pc.intensity, pc.precursorCharge);

		                spectrum.setPrecursor(precursor);

		                //Setting Retention Time
		                RetentionTime rt = new RetentionTimeDiscrete(pc.getPasefItems().get(0).retentionTime, SECOND);
		                spectrum.addRetentionTime(rt);

		                //Id

		                spectrum.setSpectrumIndex(pc.precursorId);

		                //MsLevel
		                spectrum.setMsLevel(2);

		                //Title
		                spectrum.setComment("Precursor" + pc.precursorId);

		                //Setting Mz Pairs in mzjava spectrum object
		                for (Map.Entry<Double, Double> entry : mzPairFinal.entrySet()) {
		                    spectrum.add(new BigDecimal(String.valueOf(entry.getKey())).setScale(3, RoundingMode.CEILING).doubleValue(), new BigDecimal(String.valueOf(entry.getValue())).setScale(1, RoundingMode.CEILING).doubleValue());
		                }

		                //MgfWriter writing part
//		                mgfWriter.write(spectrum);
						// XXX ATINS CODE
						
						if (isKafkaProducer) {
							// Producer sends messaged
							sendMessage(spec.getSpectrumInformationAsString(),
										kafkaProducer,
										kafkaTopic);
						} else {
							mgfWriter.write(spectrum);
//							writer.writeSpectrum(spec.getSpectrumInformationAsString());
						}

						//We prepare a statement to insert the already processed Frame-Precursor Pair
						pstmt.setString(1, spec.frameId.toString());
						pstmt.setString(2, spec.precursorId.toString());
						pstmt.addBatch();
					}
				}

				if ((iterationCount % bruker.getApplicationProperties().getBatchSize()) == 0) {
					System.out.println("Writing Precursor " + iterationCount + " of " + bruker.getPrecursors().size());
					//execute batch
					pstmt.executeBatch();
					connection.commit();
				}
			}

			// ensure no trailing changes are left
			pstmt.executeBatch();
			connection.commit();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			// If error occurs we wish to rollback any changes.
			connection.rollback();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();

		} finally {
			bruker.close();
			if (writer != null) {
				writer.close();
			}
		}

        return iterationCount > 0;

    }

	
	// static methods should be replaced
	private static void sendMessage(String content, KafkaProducer kafkaProducer, String kafkaTopic) {
		String message = content;
		ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, message);
		kafkaProducer.send(record);
	}
	
//  public static void main_ATIN(String[] args) {
//  try {
//      // Read Bruker Raw File
//      BrukerRawFormatWrapper bruker = new BrukerRawFormatWrapper(Properties.analysisDir);
//
//      File file = new File(Properties.targetFile);
//      /*
//       * Precision.DOUBLE -> 23.3330000 9.000000
//       * Precision.DOUBLE_CONSTANT -> 23.3330000 9
//       * Precision.DOUBLE_FLOAT -> 23.3330000 9.000
//       *
//       */
//      MgfWriter mgfWriter = new MgfWriter(file, PeakList.Precision.FLOAT_CONSTANT);
//
//      
//      // i think this old stuff can be discarded entirely
//      /*----- MGF Writer code by Anton & Kay -----**/
//      /* MGFWriter writer = new MGFWriter(Properties.targetFile);
//		// loop Frames
//		int framecount = 0;
//		for (BrukerFrame fr : bruker.getFrames()) {
//			framecount++;
//			if ((framecount % 1000) == 0) {
//				System.out.println("Writing Frame " + framecount + " of " + bruker.getFrames().size());
//			}
//			// write spectrum
//			PeakListContainer spec = fr.getPeakListContainer();
//			if (spec != null) {
//				writer.writeSpectrum(spec);
//      // loop precursors 
//       */
//      int precursorCount = 0;
//
//      HashMap<Double, Double> mzPairFinal;
//
//      for (BrukerPrecusor pc : bruker.getPrecursors()) {
//          precursorCount++;
//          if ((precursorCount % 1000) == 0) {
//              System.out.println("Writing Precursor " + precursorCount + " of " + bruker.getPrecursors().size());
//          }
//          // read peakList for this precursor
//          PeakListContainer spec = pc.getPeakListContainer();
//
//          /*
//           * Three tasks to be performed on PeakList
//           * 1. Binning
//           * 2. Deisotoping
//           * 3. Noise filtering
//           */
//
//          /* Binning Task**/
//          /*PeakListUtilities.binPeaklist(PeakListContainer peakListContainer) - uses default binTolerance =0.02
//            PeakListUtilities.binPeaklist(double binTolerance, PeakListContainer peakListContainer)
//            Returns - HashMap<Double,Double>*/
//
//          mzPairFinal = PeakListUtilities.binPeaklist(spec);
//
//          /* Deisotoping Task**/
//          /*PeakListUtilities.removeIsotopePeaklist(HashMap <Double, Double> binnedPeakListMap, Boolean monotonicShape) - without mzTolerance,ppmTolerance
//            PeakListUtilities.removeIsotopePeaklist(HashMap <Double, Double> binnedPeakListMap, Boolean monotonicShape,double mzTolerance, double ppmTolerance) */
//
//          //monotonicShape - If true, then monotonically decreasing height of isotope pattern is required
//          //mzTolerance    - absolute tolerance (in m/z)
//          //ppmTolerance   - relative tolerance (in ppm)
//          mzPairFinal = PeakListUtilities.removeIsotopePeaklist(mzPairFinal,false);
//
//          /* Noise filtering**/
//          double noiseIntLimit = 8.0;
//          //noinspection Convert2MethodRef (As Map.Entry::getKey & Map.Entry::getValue static method references cannot be called within a non-static method)
//          mzPairFinal = (HashMap<Double, Double>) mzPairFinal.entrySet().stream().filter(x -> x.getValue() >= noiseIntLimit).collect(Collectors.toMap(x->x.getKey(), x->x.getValue()));
///*             HashMap <Double, Double> copyOfMzPairFinal = new HashMap <>(mzPairFinal);
//          for (Map.Entry<Double,Double> tempPeak:copyOfMzPairFinal.entrySet()
//               ) {
//              if(tempPeak.getValue()<noiseIntLimit)
//              {
//                  mzPairFinal.remove(tempPeak.getKey());
//              }
//          }*/
//
//
//
//
//          /*------Creating Mzjava Spectrum Object-----------*/
//
//          MsnSpectrum spectrum = new MsnSpectrum(spec.mzArray.length, PeakList.Precision.DOUBLE);
//
//          //Precursor = Peak(double mz, double intensity, int... charge)
//          Peak precursor = new Peak(pc.monoisotopicMz, pc.intensity, pc.precursorCharge);
//
//          spectrum.setPrecursor(precursor);
//
//          //Setting Retention Time
//          RetentionTime rt = new RetentionTimeDiscrete(pc.getPasefItems().get(0).retentionTime, SECOND);
//          spectrum.addRetentionTime(rt);
//
//          //Id
//
//          spectrum.setSpectrumIndex(pc.precursorId);
//
//          //MsLevel
//          spectrum.setMsLevel(2);
//
//          //Title
//          spectrum.setComment("Precursor" + pc.precursorId);
//
//          //Setting Mz Pairs in mzjava spectrum object
//          for (Map.Entry<Double, Double> entry : mzPairFinal.entrySet()) {
//              spectrum.add(new BigDecimal(String.valueOf(entry.getKey())).setScale(3, RoundingMode.CEILING).doubleValue(), new BigDecimal(String.valueOf(entry.getValue())).setScale(1, RoundingMode.CEILING).doubleValue());
//          }
//
//          //MgfWriter writing part
//          mgfWriter.write(spectrum);
//
//      }
//      // close everything
//      mgfWriter.close();
//      bruker.close();
//
//
//  } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//  }
//}

}

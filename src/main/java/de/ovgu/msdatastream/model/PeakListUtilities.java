package de.ovgu.msdatastream.model;

import com.google.common.collect.Range;
import de.ovgu.msdatastream.brukerraw.sqllite.PeakListContainer;

import java.util.*;
import java.util.stream.Collectors;

public class PeakListUtilities {

    /**
     * The isotopeDistance constant defines expected distance between isotopes.
     * Actual weight of 1 neutron is 1.008665 Da, but part of this mass is
     * consumed as binding energy to other protons/neutrons. Actual mass
     * increase of isotopes depends on chemical formula of the molecule. Since
     * we don't know the formula, we can assume the distance to be ~1.0033 Da,
     * with user-defined tolerance.
     */
    private static final double isotopeDistance = 1.0033;

    private static double mzTolerance;
    private static double ppmTolerance;

    // PPM conversion factor.
    private static final double MILLION = 1000000.0;

    /*------------------------------------Binning----------------------------------------*/

    /**
     *
     * @param binTolerance
     *              m/z tolerance for binning
     * @param spec
     *              PeakListContainer object containing all peaks
     * @return binnedPeaklistMap
     *              Contains all peaks after binning
     */

    public static HashMap <Double, Double> binPeaklist(double binTolerance, PeakListContainer spec) {

        /**
         * Binning for PeakListContainer spec
         */

        //double Tolerance = binTolerance;
        HashMap <Double, Double> mzPair = new HashMap <>();
        HashMap <Double, Double> binnedPeaklistMap = new HashMap <>();

        //Resetting binnedPeaklistMap variable used to write each spectrum
        //binnedPeaklistMap.clear();

        /*Moving mz pairs to a map for simplicity*/
        //mzPair.clear();
        for (int i = 0; i < spec.intensitiesArray.length; i++) {
            mzPair.put(spec.mzArray[i], (double) spec.intensitiesArray[i]);
            //Note : Here intensities are integers and explicitly converted to double
            // to adjust with Spectrum's structure
        }

        //Local variable for binning loop
        double start;
        double next;
        double sumOfMz = 0.0;
        double sumOfInt = 0;
        double countMz = 0;
        //noinspection MismatchedQueryAndUpdateOfCollection
        List <Double> tempMzList = new ArrayList <>();

        //mz array
        double[] tempMz = spec.mzArray;
        //Sorting mz array (Ascending)
        //noinspection deprecation
        Arrays.sort(tempMz);

        //selecting first element from mz array
        start = tempMz[0];

        //Binning loop counter starts from 1 as 0th element is already selected
        for (int i = 1; i <= tempMz.length; i++) {

            //start already initialised
            try {
                //starting to collect peaks for binning
                if (countMz == 0) {
                    sumOfMz += start;
                    tempMzList.add(start);
                    countMz++;

                    sumOfInt += mzPair.get(start);
                }

                //fetching the next peak to compare for binning
                next = tempMz[i];

                if (next - start <= binTolerance) {

                    sumOfMz += next;
                    tempMzList.add(next);
                    countMz++;

                    sumOfInt += mzPair.get(next);
                } else {
                    /* Updating in mzPair Map if
                     * there are more than one peak to be
                     * combined or binned
                     */
                    if (countMz > 1) {
                        //Adding averaged peaks
                        binnedPeaklistMap.put((sumOfMz / countMz), sumOfInt);
                    } else if (countMz == 1) {
                        binnedPeaklistMap.put(sumOfMz, sumOfInt);
                    }


                    //Resetting all counters
                    countMz = 0;
                    sumOfMz = 0;
                    sumOfInt = 0;
                    tempMzList.clear();
                }
                //Moving on to next (element) peak
                start = next;
            } catch (ArrayIndexOutOfBoundsException arrOutBond) {
                //That means array has reached the last element
                /* Updating in mzPair Map if
                 * there are more than one peak to be
                 * combined or binned
                 */
                if (countMz > 1) {
                    //Adding averaged peaks
                    binnedPeaklistMap.put((sumOfMz / countMz), sumOfInt);
                } else if (countMz == 1) {
                    binnedPeaklistMap.put(sumOfMz, sumOfInt);
                }
            }

        }
        /* ---- End of binning part ----* */

        return binnedPeaklistMap;
    }

    /**
     *
     * @param spec
     *              PeakListContainer object containing all peaks
     * @return binPeaklist(defaultBinningTolerance, spec)
     *
     */
    public static HashMap <Double, Double> binPeaklist(PeakListContainer spec) {
        // use the tolerance of 0.02
        double defaultBinningTolerance = 0.02;

        return binPeaklist(defaultBinningTolerance, spec);
    }

    /*------------------------------------End of Binning----------------------------------------*/





    /*------------------------------------Deisotoping----------------------------------------*/


    /**
     * Method for deisotoping (isotope-removal) a Peaklist
     * inspired from 'net.sf.mzmine.modules.peaklistmethods.isotopes.deisotoper.IsotopeGrouperTask'
     * Github link - 'https://github.com/mzmine/mzmine2'
     *
     * @param peakListMap    Contains all peaks for a spectrum
     * @param monotonicShape If true, then monotonically decreasing height of isotope pattern is required
     * @param mzTolerance    absolute tolerance (in m/z)
     *                       The tolerance range is calculated using maximum of the absolute and relative tolerances
     * @param ppmTolerance   relative tolerance (in ppm)
     *                       The tolerance range is calculated using maximum of the absolute and relative tolerances
     * @return deisotopedPeakListMap
     * Contains all the peaks after removing isotopes
     */

    public static HashMap <Double, Double> removeIsotopePeaklist(HashMap <Double, Double> peakListMap, Boolean monotonicShape, double mzTolerance, double ppmTolerance) {
//        Logger logger = Logger.getLogger(PeakListUtilities.class.getName());
//        logger.info("Original Peaklist Length: " + peakListMap.size());


        setMzTolerance(mzTolerance);
        setPpmTolerance(ppmTolerance);
        //Sort Map based on Intensity (height) in descending Order
        HashMap <Double, Double> sortedPeakMap = peakListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        //Alternative way to sort
        /*Map<Double, Double> result2 = new LinkedHashMap<>();
        PeakListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));*/

        int maximumCharge = 3;
        // Collect all selected charge states
        int charges[] = new int[maximumCharge];
        for (int i = 0; i < maximumCharge; i++)
            charges[i] = i + 1;


        HashMap <Double, Double> deisotopedPeakListMap = new HashMap <>();

        /* ---- Start Deisotoping Logic -----**/

        //Counter for number of peaks processed
        //int processedPeaks = 0;

        // Loop through all peaks
        while (!sortedPeakMap.isEmpty()) {

            Iterator <Map.Entry <Double, Double>> sortedPeaks = sortedPeakMap.entrySet().iterator();

            HashMap.Entry <Double, Double> aPeak = sortedPeaks.next();

            // Check if peak was already deleted
            if (aPeak == null) {
               // processedPeaks++;
                continue;
            }

            // Check which charge state fits best around this peak
            int bestFitCharge = 0;
            int bestFitScore = -1;

            HashMap <Double, Double> bestFitPeaksMap = null;

            for (int charge : charges) {

                HashMap <Double, Double> fittedPeaksMap = new HashMap <>();
                fittedPeaksMap.put(aPeak.getKey(), aPeak.getValue());
                fitPattern(fittedPeaksMap, aPeak, charge, sortedPeakMap, monotonicShape);

                int score = fittedPeaksMap.size();
                if ((score > bestFitScore)
                        || ((score == bestFitScore) && (bestFitCharge > charge))) {
                    bestFitScore = score;
                    bestFitCharge = charge;
                    bestFitPeaksMap = fittedPeaksMap;
                }

            }

            /* Verify the number of detected isotopes. If there is only one
             isotope, we add this to deisotopedPeakListMap and remove from original PeakListMap (sortedPeakMap)
            and continue*/
            if (bestFitPeaksMap.size() == 1) {
                deisotopedPeakListMap.put(aPeak.getKey(), aPeak.getValue());

                //remove from original PeakListMap
                sortedPeakMap.remove(aPeak.getKey(), aPeak.getValue());
                //processedPeaks++;
                continue;
            }


            //Sort by lowest m/z peak (Ascending)
            bestFitPeaksMap = bestFitPeaksMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            //Feature newPeak = new SimpleFeature(originalPeaks[0]); - mzmine reference
            //Peak to be retained from isotope group
            HashMap.Entry <Double, Double> newPeak = bestFitPeaksMap.entrySet().iterator().next();

            //Update Intensity as sum of the intensities of all isotopes
            //noinspection unused(variable used for logging)
            double oldIntensity = newPeak.getValue();
            double newIntensity = bestFitPeaksMap.values().stream().mapToDouble(Double::doubleValue).sum();
            newPeak.setValue(newIntensity);

            deisotopedPeakListMap.put(newPeak.getKey(), newPeak.getValue());

            //TODO (in future): To store bestFitCharge in the Peak (retained peak from isotope group)
//            logger.info("Peak : " + newPeak.getKey() + " " + newPeak.getValue() + " BestFitCharge : " + bestFitCharge);


//            if (bestFitPeaksMap.size() > 0) {
//                logger.info("Deleting Isotopes...");
//            }


            // Remove all peaks already assigned to isotope pattern
            for (HashMap.Entry <Double, Double> loopVariable : bestFitPeaksMap.entrySet()) {
                if (sortedPeakMap.containsKey(loopVariable.getKey()))//Checking only key,
                // as Map won't have duplicate keys
                {
                    sortedPeakMap.remove(loopVariable.getKey(), loopVariable.getValue());
                    //Logging
//                    if(loopVariable.getKey()==newPeak.getKey() && loopVariable.getValue()==newPeak.getValue())
////                    {
////                        //This is the retained peak
////                        //So logging its original intensity
////                        logger.info("Isotope - " + loopVariable.getKey() + " " + oldIntensity);
////                    }
////                    else
////                    {
////                        logger.info("Isotope - " + loopVariable.getKey() + " " + loopVariable.getValue());
////                    }

                }
            }

            //processedPeaks++;

        }



//        logger.info("Deisotoped Peaklist Length: " + deisotopedPeakListMap.size());
        return deisotopedPeakListMap;


    }


    /**
     *
     * @param peakListMap       Contains all peaks for a spectrum
     * @param monotonicShape    If true, then monotonically decreasing height of isotope pattern is required
     * @return removeIsotopePeaklist(peakListMap,monotonicShape,defaultMzTolerance,defaultPpmTolerance)
     *
     */
    public static HashMap <Double, Double> removeIsotopePeaklist(HashMap <Double, Double> peakListMap, Boolean monotonicShape) {

        // Tolerance has absolute (in m/z) and relative (in ppm) values
        double defaultMzTolerance = 0.1;
        double defaultPpmTolerance = 1.0;
        return removeIsotopePeaklist(peakListMap,monotonicShape, defaultMzTolerance, defaultPpmTolerance);
    }


    /**
     * Fits isotope pattern around one peak.
     *
     *
     * @param fittedPeaks
     *          Probable isotope peaks to be stored
     * @param p
     *          Pattern is fitted around this peak
     * @param charge
     *          Charge state of the fitted pattern
     * @param sortedPeakMap
     *          Contains all peaks from peakListMap sorted by intensity
     * @param monotonicShape
     *          If true, then monotonically decreasing height of isotope pattern is required
     * @return
     *          Void , as fittedPeaks object is set with desired result
     *
     */

    private static void fitPattern(HashMap <Double, Double> fittedPeaks, Map.Entry <Double, Double> p, int charge,
                                   HashMap <Double, Double> sortedPeakMap, Boolean monotonicShape) {

        if (charge == 0) {
            return;
        }

        // Search for peaks before the start peak
        if (!monotonicShape) {
            fitHalfPattern(p, charge, -1, fittedPeaks, sortedPeakMap);
        }

        // Search for peaks after the start peak
        fitHalfPattern(p, charge, 1, fittedPeaks, sortedPeakMap);

    }

    /**
     * Helper method for fitPattern. Fits only one half of the pattern.
     *
     * @param p           Pattern is fitted around this peak
     * @param charge      Charge state of the fitted pattern
     * @param direction   Defines which half to fit: -1=fit to peaks before start M/Z,
     *                    +1=fit to peaks after start M/Z
     * @param fittedPeaks All matching peaks will be added to this set
     * @param sortedPeakMap Contains all peaks from peakListMap sorted by intensity
     * @return Void , as fittedPeaks object is set with desired result
     */
    private static void fitHalfPattern(Map.Entry <Double, Double> p, int charge, int direction,
                                       HashMap <Double, Double> fittedPeaks, HashMap <Double, Double> sortedPeakMap) {

        // Use M/Z and RT of the strongest peak of the pattern (peak 'p')
        double mainMZ = p.getKey(); //mZ
        double mainINT = p.getValue(); //Intensity

        //TODO (in future) : RT for each peak
        //double mainRT = 15.87;//p.getRT();

        // Variable n is the number of peak we are currently searching. 1=first
        // peak before/after start peak, 2=peak before/after previous, 3=...
        boolean followingPeakFound;
        int n = 1;
        do {

            // Assume we don't find match for n:th peak in the pattern (which
            // will end the loop)
            followingPeakFound = false;

            // Loop through all peaks, and collect candidates for the n:th peak
            // in the pattern
            HashMap <Double, Double> goodCandidates = new HashMap <>();
            Iterator <Map.Entry <Double, Double>> iteratorSortedPeaks = sortedPeakMap.entrySet().iterator();
            //for (int ind = 0; ind < sortedPeakMap.length; ind++)
            do {

                //Feature candidatePeak = sortedPeaks[ind];

                HashMap.Entry <Double, Double> candidatePeak = iteratorSortedPeaks.next();

                if (candidatePeak == null)
                    continue;

                // Get properties of the candidate peak
                double candidatePeakMZ = candidatePeak.getKey();//candidatePeak.getMZ();
//              double candidatePeakRT = 15.87;//candidatePeak.getRT();

                // Does this peak fill all requirements of a candidate?
                // - within tolerances from the expected location (M/Z and RT)
                // - not already a fitted peak (only necessary to avoid
                // conflicts when parameters are set too wide)
                double isotopeMZ = candidatePeakMZ - ((isotopeDistance
                        * direction * n) / (double) charge);

                if (checkWithinTolerance(isotopeMZ, mainMZ)
                        && checkWithinIntensityTolerance(candidatePeak.getValue(), mainINT) //candidatePeak.getValue() = candidatePeak Intensity
                       /* && rtTolerance.checkWithinTolerance(candidatePeakRT,
                        mainRT)*/ // TODO: (Future) As RT remains same for all peaks in our case
                        && (!fittedPeaks.entrySet().contains(candidatePeak))) {
                    goodCandidates.put(candidatePeak.getKey(), candidatePeak.getValue());

                }

            } while (iteratorSortedPeaks.hasNext());

            // Add all good candidates to the isotope pattern (note: in MZmine
            // 2.3 and older, only the highest candidate was added)
            //TODO: Note the comment
            if (!goodCandidates.isEmpty()) {

                fittedPeaks.putAll(goodCandidates);

                // n:th peak was found, so let's move on to n+1
                n++;
                followingPeakFound = true;
            }

        } while (followingPeakFound);

    }

    private static boolean checkWithinTolerance(final double mz1, final double mz2) {
        return getToleranceRange(mz1).contains(mz2);
    }

    private static Range <Double> getToleranceRange(final double mzValue) {
        final double absoluteTolerance = getMzToleranceForMass(mzValue);
        return Range.closed(mzValue - absoluteTolerance, mzValue
                + absoluteTolerance);
    }

    private static double getMzToleranceForMass(final double mzValue) {
        return Math.max(mzTolerance, mzValue / MILLION * ppmTolerance);
    }


    private static boolean checkWithinIntensityTolerance(final double ints1, final double ints2) {
        return getToleranceIntRange(ints1).contains(ints2);
    }

    private static Range <Double> getToleranceIntRange(final double intensityValue) {
        final double absoluteTolerance = 0.05;
        return Range.closed(intensityValue * absoluteTolerance, intensityValue
                / absoluteTolerance);
    }


    private static void setMzTolerance(double mzTolerance) {
        PeakListUtilities.mzTolerance = mzTolerance;
    }

    private static void setPpmTolerance(double ppmTolerance) {
        PeakListUtilities.ppmTolerance = ppmTolerance;
    }

    /*------------------------------------End of Deisotoping----------------------------------------*/
}


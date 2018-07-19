package de.ovgu.msdatastream;

import com.google.common.collect.Range;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTime;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeDiscrete;

import java.util.*;
import java.util.stream.Collectors;

import static org.expasy.mzjava.core.ms.spectrum.TimeUnit.SECOND;

public class TestDeisotoping {
    static Boolean monotonicShape = false;
    private static final double isotopeDistance = 1.0033;

    // PPM conversion factor.
    private static final double MILLION = 1000000.0;

    public static void main(String[] args) {
        double[] tempMz = new double[] {
                212.00,
                212.50,
                213.00,
                213.50,
                214.00,
                269.03,
                270.919,
                272.754,
                274.931,
                287.945,
                290.916,
                292.919,
                319.887,
                331.944,
                333.951,
                351.95,
                352.02,
                353.949,
                354.939,
                355.939,
                371.968,
                373.957,
                381.17,
                393.965,
                398.96,
                407.281,
                411.986,
                413.975,
                414.97,
                415.412,
                415.968,
                416.95,
                432.968,
                433.982,
                434.967,
                455.987,
                456.136,
                456.999,
                457.983,
                458.004,
                458.923,
                475.993,
                476.215,
                477,
                477.987,
                478.98,
                482.465,
                511.084,
                513.929,
                520.005,
                521.798,
                526.371,
                533.929,
                539.833,
                540.004,
                540.442,
                541.011,
                542.998,
                558.015,
                559.018,
                560.962,
                574.049,
                602.026,
                603.02,
                618.772,
                620.851,
                620.875,
                621.663,
                621.711,
                621.787,
                621.893,
                621.971,
                622.036,
                622.185,
                622.213,
                622.266,
                622.487,
                622.759,
                622.928,
                623.031,
                623.153,
                623.286,
                623.324,
                623.468,
                623.653,
                623.698,
                623.789,
                623.878,
                623.958,
                624.021,
                624.1,
                624.134,
                624.18,
                624.272,
                624.393,
                624.459,
                624.565,
                624.596,
                624.716,
                624.76,
                624.824,
                625.247,
                625.354,
                625.562,
                625.642,
                625.692,
                626.317,
                626.429,
                626.569,
                626.753,
                626.915,
                626.941,
                627.789,
                628.233,
                628.269,
                628.817,
                628.961,
                629.186,
                629.899,
                629.967,
                631.19,
                653.326,
                759.28,
                927.187,
                979.271,
                1125.32,
                1376.13,
                1520.808,
                1622.113,
                1658.011};
        double[] tempInt = new double[]{
                200,
                1000,
                200,
                100,
                100,
                9,
                84,
                9,
                87,
                26,
                63,
                61,
                9,
                209,
                86,
                118,
                93,
                115,
                224,
                47,
                74,
                254,
                9,
                310,
                58,
                9,
                10,
                246,
                82,
                57,
                493,
                31,
                67,
                155,
                109,
                744,
                10,
                43,
                47,
                63,
                9,
                481,
                23,
                50,
                1388,
                258,
                18,
                9,
                11,
                78,
                106,
                22,
                9,
                20,
                2697,
                29,
                583,
                75,
                1262,
                81,
                9,
                56,
                160,
                66,
                11,
                24,
                32,
                13,
                43,
                21,
                263,
                389,
                14569,
                78,
                33,
                84,
                52,
                490,
                27,
                3743,
                28,
                57,
                9,
                20,
                73,
                115,
                71,
                84,
                130,
                109,
                38,
                27,
                37,
                100,
                10,
                69,
                9,
                37,
                37,
                57,
                9,
                38,
                40,
                22,
                22,
                64,
                42,
                24,
                24,
                25,
                64,
                10,
                14,
                37,
                26,
                26,
                27,
                13,
                20,
                24,
                26,
                9,
                10,
                9,
                9,
                9,
                9,
                9,
                9,
                9};


        MsnSpectrum spectrum = new MsnSpectrum(tempMz.length, PeakList.Precision.DOUBLE);
        spectrum.addSorted(tempMz,tempInt,tempMz.length);

        //Precursor = Peak(double mz, double intensity, int... charge)
        Peak precursor = new Peak(622.029, 203644, 1);

        spectrum.setPrecursor(precursor);

        //Setting Retention Time
        //Done: Correct RT to be set
        RetentionTime rt = new RetentionTimeDiscrete(15.87, SECOND);
        spectrum.addRetentionTime(rt);



        HashMap<Double, Double> PeakListMap = new HashMap<>();

        //Set-up Spectrum's Peaklist into a Map
        for (int i = 0; i < tempMz.length; i++) {
            PeakListMap.put(tempMz[i], tempInt[i]);
        }

        System.out.println("Original Peaklist Length: "+PeakListMap.size());
        //Sort Map based on height; we don't have height so I would use Intensity
        //Descending Order
        HashMap<Double,Double> sortedPeakMap = PeakListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        //Alternative way
        /*Map<Double, Double> result2 = new LinkedHashMap<>();
        PeakListMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));*/

        /*for (double ints: result.values()) {
            System.out.println(ints);
        }*/

        int maximumCharge =3;
        // Collect all selected charge states
        int charges[] = new int[maximumCharge];
        for (int i = 0; i < maximumCharge; i++)
            charges[i] = i + 1;


        HashMap<Double,Double> deisotopedPeakListMap = new HashMap<>();

        /*Start Deisotoping Logic**/

        // Loop through all peaks
        int totalPeaks = sortedPeakMap.size();
        int processedPeaks =0;

        while (!sortedPeakMap.isEmpty()) {

            Iterator<Map.Entry<Double,Double>> sortedPeaks = sortedPeakMap.entrySet().iterator();

            HashMap.Entry<Double,Double> aPeak = sortedPeaks.next();

            // Check if peak was already deleted
            if (aPeak == null) {
                processedPeaks++;
                continue;
            }

            // Check which charge state fits best around this peak
            int bestFitCharge = 0;
            int bestFitScore = -1;

            HashMap<Double,Double> bestFitPeaksMap = null;

            for (int charge : charges) {

                HashMap<Double,Double> fittedPeaksMap = new HashMap<>();
                fittedPeaksMap.put(aPeak.getKey(),aPeak.getValue());
                fitPattern(fittedPeaksMap, aPeak, charge, sortedPeakMap);

                int score = fittedPeaksMap.size();
                if ((score > bestFitScore)
                        || ((score == bestFitScore) && (bestFitCharge > charge))) {
                    bestFitScore = score;
                    bestFitCharge = charge;
                    bestFitPeaksMap = fittedPeaksMap;
                }

            }

            //PeakListRow oldRow = peakList.getPeakRow(aPeak);

           // assert bestFitPeaks != null;

            // Verify the number of detected isotopes. If there is only one
            // isotope, we skip this left the original peak in the peak list.
            if (bestFitPeaksMap.size() == 1) {
                //deisotopedPeakList.addRow(oldRow);
                deisotopedPeakListMap.put(aPeak.getKey(),aPeak.getValue());

                //remove from original PeakList
                sortedPeakMap.remove(aPeak.getKey(),aPeak.getValue());
                processedPeaks++;
                continue;
            }

           /* // Convert the peak pattern to array
            Feature originalPeaks[] = bestFitPeaks.toArray(new Feature[0]);

            // Create a new SimpleIsotopePattern
            DataPoint isotopes[] = new DataPoint[bestFitPeaks.size()];
            for (int i = 0; i < isotopes.length; i++) {
                Feature p = originalPeaks[i];
                isotopes[i] = new SimpleDataPoint(p.getMZ(), p.getHeight());

            }
            SimpleIsotopePattern newPattern = new SimpleIsotopePattern(
                    isotopes, IsotopePatternStatus.DETECTED, aPeak.toString());*/

            // Depending on user's choice, we leave either the most intenst, or
            // the lowest m/z peak
           /* if (chooseMostIntense) {
                Arrays.sort(originalPeaks, new PeakSorter(
                        SortingProperty.Height, SortingDirection.Descending));
            } else {
                Arrays.sort(originalPeaks, new PeakSorter(SortingProperty.MZ,
                        SortingDirection.Ascending));
            }*/

           //Sort by lowest m/z peak -Ascending
            bestFitPeaksMap =  bestFitPeaksMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            //Feature newPeak = new SimpleFeature(originalPeaks[0]);
            HashMap.Entry<Double,Double> newPeak = bestFitPeaksMap.entrySet().iterator().next();

            //newPeak.setIsotopePattern(newPattern);

            //TODO: Update Intensity as sum of the intensities of all isotopes
            double oldIntensity = newPeak.getValue();
            double newIntenity = bestFitPeaksMap.values().stream().mapToDouble(Double::doubleValue).sum();
            newPeak.setValue(newIntenity);
            // Keep old ID
            //int oldID = oldRow.getID();
            //SimplePeakListRow newRow = new SimplePeakListRow(oldID);
            //PeakUtils.copyPeakListRowProperties(oldRow, newRow);
            //newRow.addPeak(dataFile, newPeak);

            //deisotopedPeakList.addRow(newRow);
            deisotopedPeakListMap.put(newPeak.getKey(),newPeak.getValue());

            //TODO: To add bestFitCharge in the Peak in future//newPeak.setCharge(bestFitCharge);
            System.out.println("Peak : " +newPeak.getKey()+" "+newPeak.getValue()+" BestFitCharge : "+bestFitCharge);

            // Remove all peaks already assigned to isotope pattern
            //TODO:Caution
            if(bestFitPeaksMap.size()>0)
            {
                System.out.println("Deleting Isotopes...");
            }
            for (HashMap.Entry<Double,Double> loopVariable:bestFitPeaksMap.entrySet()) {
                if(sortedPeakMap.containsKey(loopVariable.getKey()) )//Checking only key,
                    // as Map won't have duplicate keys
                {
                    sortedPeakMap.remove(loopVariable.getKey(),loopVariable.getValue());
                    System.out.println("Isotope - "+loopVariable.getKey()+" "+loopVariable.getValue());
                }
            }
            /*for (int i = 0; i < sortedPeaks.length; i++) {
                if (bestFitPeaks.contains(sortedPeaks[i]))
                    sortedPeaks[i] = null;
            }*/

            // Update completion rate
            processedPeaks++;

        }

        // Add new peakList to the project
        //project.addPeakList(deisotopedPeakList);

        // Load previous applied methods
        /*for (PeakListAppliedMethod proc : peakList.getAppliedMethods()) {
            deisotopedPeakList.addDescriptionOfAppliedTask(proc);
        }*/

        // Add task description to peakList
        /*deisotopedPeakList
                .addDescriptionOfAppliedTask(new SimplePeakListAppliedMethod(
                        "Isotopic peaks grouper", parameters));*/

        // Remove the original peakList if requested
        /*if (removeOriginal)
            project.removePeakList(peakList);*/

        /*logger.info("Finished isotopic peak grouper on " + peakList);
        setStatus(TaskStatus.FINISHED);*/

        System.out.println("Deisotope Peaklist Length: "+deisotopedPeakListMap.size());
    }



    /**
     * Fits isotope pattern around one peak.
     *
     * @param p
     *            Pattern is fitted around this peak
     * @param charge
     *            Charge state of the fitted pattern
     */
    private static void fitPattern(HashMap<Double,Double> fittedPeaks, Map.Entry<Double,Double> p, int charge,
                            HashMap<Double,Double> sortedPeakMap) {

        if (charge == 0) {
            return;
        }

        // Search for peaks before the start peak
        //"Monotonic shape",
        //	    "If true, then monotonically decreasing height of isotope pattern is required"
        if (!monotonicShape) {
            fitHalfPattern(p, charge, -1, fittedPeaks, sortedPeakMap);
        }

        // Search for peaks after the start peak
        fitHalfPattern(p, charge, 1, fittedPeaks, sortedPeakMap);

    }

    /**
     * Helper method for fitPattern. Fits only one half of the pattern.
     *
     * @param p
     *            Pattern is fitted around this peak
     * @param charge
     *            Charge state of the fitted pattern
     * @param direction
     *            Defines which half to fit: -1=fit to peaks before start M/Z,
     *            +1=fit to peaks after start M/Z
     * @param fittedPeaks
     *            All matching peaks will be added to this set
     */
    private static void fitHalfPattern(Map.Entry<Double,Double> p, int charge, int direction,
                                       HashMap<Double,Double> fittedPeaks,HashMap<Double,Double> sortedPeakMap) {

        // Use M/Z and RT of the strongest peak of the pattern (peak 'p')
        double mainMZ =  p.getKey(); //p.getMZ();
        double mainINT = p.getValue(); //Intensity

        double mainRT = 15.87;//p.getRT();

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
            HashMap<Double,Double> goodCandidates = new HashMap<Double,Double>();
            Iterator<Map.Entry<Double,Double>> iteratorSortedPeaks = sortedPeakMap.entrySet().iterator();
            //for (int ind = 0; ind < sortedPeakMap.length; ind++)
            do {

                //Feature candidatePeak = sortedPeaks[ind];

                HashMap.Entry<Double,Double> candidatePeak = iteratorSortedPeaks.next();

                if (candidatePeak == null)
                    continue;

                // Get properties of the candidate peak
                double candidatePeakMZ = candidatePeak.getKey();//candidatePeak.getMZ();
                double candidatePeakRT = 15.87;//candidatePeak.getRT();

                // Does this peak fill all requirements of a candidate?
                // - within tolerances from the expected location (M/Z and RT)
                // - not already a fitted peak (only necessary to avoid
                // conflicts when parameters are set too wide)
                double isotopeMZ = candidatePeakMZ - ((isotopeDistance
                        * direction * n) / (double) charge);

                if (checkWithinTolerance(isotopeMZ, mainMZ)
                        && checkWithinIntensitzTolerance(candidatePeak.getValue(), mainINT) //candidatePeak.getValue() = candidatePeak Intensity
                       /* && rtTolerance.checkWithinTolerance(candidatePeakRT,
                        mainRT)*/ // TODO: (Future) As RT remains same for all peaks in our case
                        && (!fittedPeaks.entrySet().contains(candidatePeak))) {
                    goodCandidates.put(candidatePeak.getKey(),candidatePeak.getValue());

                }

            }while (iteratorSortedPeaks.hasNext());

            // Add all good candidates to the isotope pattern (note: in MZmine
            // 2.3 and older, only the highest candidate was added)
            //TODO: Note the comment
            if (!goodCandidates.isEmpty()) {

//                for (Map.Entry<Double, Double> peak : goodCandidates.entrySet())  {
//                   if (peak.getValue() >= 100)  {
//                       fittedPeaks.put(peak.getKey(), peak.getValue());
//                   }
//                }
                 fittedPeaks.putAll(goodCandidates);

                // n:th peak was found, so let's move on to n+1
                n++;
                followingPeakFound = true;
            }

        } while (followingPeakFound);

    }
//checkWithinIntensityTolerance

    public static boolean checkWithinIntensitzTolerance(final double ints1, final double ints2) {
        return getToleranceIntRange(ints1).contains(ints2);
    }
    public static boolean checkWithinTolerance(final double mz1, final double mz2) {
        return getToleranceRange(mz1).contains(mz2);
    }

    public static Range<Double> getToleranceRange(final double mzValue) {
        final double absoluteTolerance = getMzToleranceForMass(mzValue);
        return Range.closed(mzValue - absoluteTolerance, mzValue
                + absoluteTolerance);
    }

    public static Range<Double> getToleranceIntRange(final double intvalue) {
        final double absoluteTolerance = 0.05;
        return Range.closed(intvalue * absoluteTolerance, intvalue
                / absoluteTolerance);
    }

    private static double getMzToleranceForMass(final double mzValue) {
        // Tolerance has absolute (in m/z) and relative (in ppm) values
        double mzTolerance = 0.1;
        double ppmTolerance = 1.0;
        return Math.max(mzTolerance, mzValue / MILLION * ppmTolerance);
    }
}

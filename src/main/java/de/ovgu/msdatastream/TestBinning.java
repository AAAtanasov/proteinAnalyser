package de.ovgu.msdatastream;

import java.util.*;

public class TestBinning {
    public static void main(String[] args) {

        double tolerance = 0.02; // use the tolerance of 0.02
        HashMap<Double,Double> mzPair = new HashMap<>();
        HashMap<Double,Double> mzPairFinal = new HashMap<>();
        //Resetting mzPairFinal variable used to write each spectrum
        mzPairFinal.clear();

        /*Moving mz pairs to a map for simplicity*/
        mzPair.clear();

        //mz array
        double[] tempMz = new double[]{212.00,
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
        double[] tempInt = new double[]{200,
                1000,
                200,
                40,
                8,
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

        for(int i=0;i<tempInt.length;i++)
        {
            mzPair.put(tempMz[i],tempInt[i]);
        }

        //Local variable for binning loop
        double start;
        double next;
        double sumOfMz = 0.0;
        double sumOfInt = 0;
        double countMz = 0;
        List<Double> tempMzList = new ArrayList<>();


        //noinspection deprecation
        Arrays.sort(tempMz);
        start = tempMz[0];

        System.out.println(" Size before Binning: "+tempMz.length);
        //Binning Loop
        for(int i =1;i<=tempMz.length;i++)
        {
            //start already initialised
            try{
                //starting to collect peaks for binning
                if (countMz==0)
                {
                    sumOfMz+= start;
                    tempMzList.add(start);
                    countMz++;

                    sumOfInt += mzPair.get(start);
                }

                //fetching the next peak to compare for binning
                next = tempMz[i];

                if(next-start<=tolerance)
                {

                    sumOfMz+=next;
                    tempMzList.add(next);
                    countMz++;

                    sumOfInt += mzPair.get(next);
                }
                else
                {
                    /* Updating in mzPair Map if
                     * there are more than one peak to be
                     * combined or binned
                     */
                    if (countMz>1)
                    {
                        //Adding averaged peaks
                        mzPairFinal.put((sumOfMz/countMz),sumOfInt);
                    }
                    else if (countMz==1)
                    {
                        mzPairFinal.put(sumOfMz,sumOfInt);
                    }


                    //Resetting all counters
                    countMz =0;
                    sumOfMz =0;
                    sumOfInt =0;
                    tempMzList.clear();
                }
                //Moving on to next (element) peak
                start = next;
            }
            catch (ArrayIndexOutOfBoundsException arrOutBond)
            {
                //That means array has reached the last element
                /* Updating in mzPair Map if
                 * there are more than one peak to be
                 * combined or binned
                 */
                if (countMz>1)
                {
                    //Adding averaged peaks
                    mzPairFinal.put((sumOfMz/countMz),sumOfInt);
                }
                else if (countMz==1)
                {
                    mzPairFinal.put(sumOfMz,sumOfInt);
                }
            }

        }

        System.out.println(" Size after Binning: "+mzPairFinal.size());
        for (Map.Entry<Double,Double> entry:mzPairFinal.entrySet()
             ) {
            System.out.println( entry.getKey()+", "+ entry.getValue() + ",");
        }
    }
}

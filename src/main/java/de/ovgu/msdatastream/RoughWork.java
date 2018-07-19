package de.ovgu.msdatastream;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class RoughWork {
    public static void main(String[] args) {
        try {
            Map<Double,Double> mzPairFinal = new HashMap <>();

            mzPairFinal.put(10.0,200.0);
            mzPairFinal.put(15.0,300.0);
            mzPairFinal.put(20.0,90.0);
            mzPairFinal.put(30.0,87.0);
            mzPairFinal.put(23.0,89.0);
            mzPairFinal.put(34.0,900.0);

            Double noiseIntLimit = 100.0;
            mzPairFinal = mzPairFinal.entrySet().stream().filter(x -> x.getValue() >= noiseIntLimit).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
            System.out.println("hi");
        }  catch(Exception e) {
            e.printStackTrace();
        }
    }
}

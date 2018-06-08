package de.ovgu.msdatastream;

// Helper class used to call timsdata.dll methods
public class ConversionFunctionHelper {
    static void indexToMz(PayloadContainer inputData, TimsdataService dll) {
        long result = dll.tims_index_to_mz(inputData.handle, inputData.frameId, inputData.inArrayOfPointers,
                inputData.outArrayOfPointers, inputData.count);

       if (result == 0) {
           throw  new RuntimeException("Failed to convert index to mz");
        }

    }

    static void scanNumToOneOverK0(PayloadContainer inputData, TimsdataService dll) {
        long result = dll.tims_scannum_to_oneoverk0(inputData.handle, inputData.frameId, inputData.inArrayOfPointers,
                inputData.outArrayOfPointers, inputData.count);

        if (result == 0) {
            throw  new RuntimeException("Failed to convert scannum to one over K0");
        }

    }

    static void oneOverK0ToScanNum(PayloadContainer inputData, TimsdataService dll) {
        long result = dll.tims_oneoverk0_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers,
                inputData.outArrayOfPointers, inputData.count);

        if (result == 0) {
            throw  new RuntimeException("Failed to convert one over K0 to scannum");
        }

    }

    static void voltageToScanNum(PayloadContainer inputData, TimsdataService dll) {
        long result = dll.tims_voltage_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers,
                inputData.outArrayOfPointers, inputData.count);

        if (result == 0) {
            throw  new RuntimeException("Failed to convert voltage to scan number");
        }

    }

    static void scanNumToVoltage (PayloadContainer inputData, TimsdataService dll) {
        long result = dll.tims_scannum_to_voltage(inputData.handle, inputData.frameId, inputData.inArrayOfPointers,
                inputData.outArrayOfPointers, inputData.count);

        if (result == 0) {
            throw  new RuntimeException("Failed to convert scan number to voltage");
        }
    }


    //TODO: implement better search if this functionality will be used
    static int[] npDigitizeImplementation(double[] searchedData, double[] mzBins){
        int[] result = new int[searchedData.length];
        for (int i = 0; i < searchedData.length; i++) {
            for (int j = 0; j < mzBins.length - 1 ; j++) {
                if (searchedData[i] > mzBins[j] && searchedData[i] < mzBins[j + 1]){
                    result[i] = j + 1;
                    break;
                }
            }
        }

        return result;
    }


}

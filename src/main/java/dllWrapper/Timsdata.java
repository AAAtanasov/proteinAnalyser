package dllWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Timsdata {
    private  int initialFrameBUfferSize = 128;
    private static final int maxBufferSize = 16777216;

    public List<ResultWrapper> readScans(int frameId, int scanBegin, int scanEnd, long handle) {
        long[] pivotArr;
        while (true) {
            int cnt = initialFrameBUfferSize;
            pivotArr = new long[cnt];
            TimsdataService sdll = TimsdataService.INSTANCE;
            int len = 4 * cnt;

            long requiredLength = sdll.tims_read_scans_v2(handle, frameId, scanBegin, scanEnd, pivotArr, len);
            // wtb exception handling

            if (requiredLength == 0) {
                throw  new RuntimeException("Timsdata error");
                //TODO: implement proper error handling by calling also the dll
            }

            if (requiredLength > len) {
                if (requiredLength > maxBufferSize) {
                    throw  new RuntimeException("Maximum expected frame size exceeded");
                }
                initialFrameBUfferSize = ((int)requiredLength / 4 )+ 1; // grow buffer size
            } else {
                break;
            }
        }


        int startIndex = scanEnd - scanBegin;
        int endOfScan = startIndex;
//        long asdasdasd = 4294967297L;
//        int testus = (int)asdasdasd;
        int currentLength = pivotArr.length;
        int tempInt = startIndex;


        List<ResultWrapper> resultWrappers = new ArrayList<ResultWrapper>();

        for (int i = scanBegin; i < endOfScan; i++) {
            try {
                int npeaks = (int)pivotArr[i - scanBegin];
                long[] intensities;
                long[] indicies;

                tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);

                indicies = Arrays.copyOfRange(pivotArr, startIndex, tempInt);
                startIndex += npeaks;

                if (startIndex > currentLength) {
                    break;
                } else {

                }
                tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
                intensities = Arrays.copyOfRange(pivotArr, startIndex, tempInt);
                startIndex += npeaks;
                if (startIndex > currentLength) {
                    break;
                }

                ResultWrapper wrapper = new ResultWrapper();
                wrapper.indicies = indicies;
                wrapper.intensities = intensities;
                resultWrappers.add(wrapper);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex);
            }

        }

        return resultWrappers;
    }
}



//        while (true) {
//            int cnt = initialFrameBUfferSize;
//            int[] pivotArr = new int[cnt];
//            INDArray buf = Nd4j.zeros(pivotArr, DataBuffer.Type.LONG);
//            TimsdataService sdll = TimsdataService.INSTANCE;
//            int len = 4 * cnt;
//
//            long hailCall = sdll.tims_read_scans_v2(handle, frameId, scanBegin, scanEnd, buf, len);
//            System.out.println(hailCall);
//
//        }
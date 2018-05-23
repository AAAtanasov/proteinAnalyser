package dllWrapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Timsdata {
    private  int initialFrameBUfferSize = 128;
    private static final int maxBufferSize = 16777216;

    public List<ResultWrapper> readScans(int frameId, int scanBegin, int scanEnd, long handle) {
        int[] pivotArr;
        while (true) {
            int cnt = initialFrameBUfferSize;
            pivotArr = new int[cnt];
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
        int currentLength = pivotArr.length;
        int tempInt = startIndex;


        List<ResultWrapper> resultWrappers = new ArrayList<ResultWrapper>();

        for (int i = scanBegin; i < endOfScan; i++) {
            int npeaks = pivotArr[i - scanBegin];
            if (npeaks > endOfScan) {
                System.out.println("Npeaks bigger than end of scan!");
            }
            int[] intensities = new int[0];
            int[] indicies = new int[0];

            //TODO: remove if-s after certain that the behaviour is stable
            if (startIndex > currentLength) {
                System.out.println("Skip this i: " + i);
                throw new RuntimeException("Start index is bigger than current length");

            } else {
                tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
                indicies = Arrays.copyOfRange(pivotArr, startIndex, tempInt);
            }


            startIndex += npeaks;

            if (startIndex > currentLength) {
                System.out.println("Skip this i: " + i);
                throw new RuntimeException("Start index is bigger than current length indicating problem with conversion" +
                        " and using plain int values instead of uint.");

            } else {
                tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
                intensities = Arrays.copyOfRange(pivotArr, startIndex, tempInt);

            }
            startIndex += npeaks;

            ResultWrapper wrapper = new ResultWrapper();
            wrapper.indicies = indicies;
            wrapper.intensities = intensities;
            resultWrappers.add(wrapper);
        }

        return resultWrappers;
    }

}


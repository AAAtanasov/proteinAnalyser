package dllWrapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Timsdata {
    //Initially set data amount seen from the example implementation
    private  int initialFrameBUfferSize = 128;
    private static final int maxBufferSize = 16777216;

    public List<ResultWrapper> readScans(int frameId, int scanBegin, int scanEnd, long handle) {
        // Pivot array contains all the scan information regarding a Frame. It is constructed from 3 separate data arrays
        // where the first half of the array with size = scanEnd - scanBegin is used to describe the peaks of each scan.
        // Second part of the array is build by the concatenation of indexes-intensities pairs. The second part of the
        // array is like a stack for scans with peaks bigger than 0, each result is added separately, the next peak
        // information comes after that
        int[] pivotArr;
        while (true) {
            int cnt = initialFrameBUfferSize;
            pivotArr = new int[cnt];
            TimsdataService sdll = TimsdataService.INSTANCE;
            int len = 4 * cnt;

            long requiredLength = sdll.tims_read_scans_v2(handle, frameId, scanBegin, scanEnd, pivotArr, len);

            if (requiredLength == 0) {
                throw  new RuntimeException("Timsdata error");
                //TODO: check if calling the dll errors is needed
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

        int[] intensities;
        int[] indicies;

        for (int i = scanBegin; i < endOfScan; i++) {
            int npeaks = pivotArr[i - scanBegin];

            //Ternary operator used in order to avoid Array out of bounds exception.
            tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
            indicies = Arrays.copyOfRange(pivotArr, startIndex, tempInt);

            startIndex += npeaks;

            tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
            intensities = Arrays.copyOfRange(pivotArr, startIndex, tempInt);

            startIndex += npeaks;

            ResultWrapper wrapper = new ResultWrapper();
            wrapper.indicies = indicies;
            wrapper.intensities = intensities;
            resultWrappers.add(wrapper);
        }

        return resultWrappers;
    }

}


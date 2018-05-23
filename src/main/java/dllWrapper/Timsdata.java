package dllWrapper;

import java.nio.ByteBuffer;
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

        // TODO: map pivotArr values to converted
        long[] convertedFromUint = new long[pivotArr.length];

        for (int i = 0; i < pivotArr.length; i++) {
            long initialValue = pivotArr[i];
            long converedUint = transformLongToUint(initialValue);
            convertedFromUint[i] = converedUint;
        }


        int startIndex = scanEnd - scanBegin;
        int endOfScan = startIndex;
        int currentLength = pivotArr.length;
        int tempInt = startIndex;


        List<ResultWrapper> resultWrappers = new ArrayList<ResultWrapper>();

        for (int i = scanBegin; i < endOfScan; i++) {
            try {
                int npeaks = (int)pivotArr[i - scanBegin];
                if (npeaks > endOfScan) {
                    System.out.println("Npeaks bigger than end of scan!");
                }
                long[] intensities = new long[0];
                long[] indicies = new long[0];

                if (startIndex > currentLength) {
                    System.out.println("Skip this i: " + i);
                } else {
                    tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
                    indicies = Arrays.copyOfRange(pivotArr, startIndex, tempInt);
                }


                startIndex += npeaks;

                if (startIndex > currentLength) {
                    System.out.println("Skip this i: " + i);

                } else {
                    tempInt = (startIndex + npeaks) > currentLength ? currentLength : (startIndex + npeaks);
                    intensities = Arrays.copyOfRange(pivotArr, startIndex, tempInt);

                }
                startIndex += npeaks;

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

    public long transformLongToUint(long value)
    {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);
        byte[] newOnes = Arrays.copyOfRange(bytes, 4, 8);

        int sizeMissing = Long.BYTES - newOnes.length;
        byte[] zeroArray = new byte[sizeMissing];
        byte[] resultingArray = new byte[newOnes.length + zeroArray.length];

        // Copy sliced array and artificial zeroes array into a new target array.
        System.arraycopy(zeroArray, 0, resultingArray, 0, zeroArray.length);
        System.arraycopy(newOnes, 0, resultingArray, zeroArray.length, newOnes.length);

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(resultingArray);
        buffer.flip();//need flip

        return buffer.getLong();

    }


}


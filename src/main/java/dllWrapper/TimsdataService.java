package dllWrapper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface TimsdataService extends Library {
    long tims_open(String analysisDirectory);
    long tims_read_scans_v2(long handle, int frameId, int scanBegin, int scanEnd, long[] buffer, int length);

    TimsdataService INSTANCE =
            (TimsdataService) Native.loadLibrary("C:\\Users\\Anton\\Documents\\sqliteReader\\lib\\timsdata\\timsdata.dll",
                    TimsdataService.class);
}

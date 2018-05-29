package dllWrapper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface TimsdataService extends Library {
    long tims_open(String analysisDirectory);
    long tims_read_scans_v2(long handle, int frameId, int scanBegin, int scanEnd, int[] buffer, int length);
    long tims_index_to_mz(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_scannum_to_oneoverk0(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_oneoverk0_to_scannum(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_scannum_to_voltage(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_voltage_to_scannum(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);


    TimsdataService INSTANCE =
            (TimsdataService) Native.loadLibrary("C:\\Users\\Anton\\Documents\\sqliteReader\\lib\\timsdata\\timsdata.dll",
                    TimsdataService.class);
}

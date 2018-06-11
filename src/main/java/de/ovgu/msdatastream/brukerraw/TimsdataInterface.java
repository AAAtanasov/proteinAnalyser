package de.ovgu.msdatastream.brukerraw;

import com.sun.jna.Library;
import com.sun.jna.Native;

import de.ovgu.msdatastream.Properties;

public interface TimsdataInterface extends Library {
    long tims_open(String analysisDirectory);
    long tims_read_scans_v2(long handle, int frameId, int scanBegin, int scanEnd, int[] buffer, int length);
    long tims_index_to_mz(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_scannum_to_oneoverk0(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_oneoverk0_to_scannum(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_scannum_to_voltage(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    long tims_voltage_to_scannum(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    TimsdataInterface INSTANCE = (TimsdataInterface) Native.loadLibrary(Properties.timsdatadllLocation, TimsdataInterface.class);
}

package de.ovgu.msdatastream.brukerraw.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
<<<<<<< HEAD
import de.ovgu.msdatastream.Properties;
=======
import de.ovgu.msdatastream.ApplicationProperties;
>>>>>>> refs/heads/merge_branch_streaming_processing

public interface TimsdataInterface extends Library {
    long tims_open(String analysisDirectory);
    long tims_read_scans_v2(long handle, int frameId, int scanBegin, int scanEnd, int[] buffer, int length);
    long tims_index_to_mz(long handle, int frameId, double[] inArrayOfPointers, double[] ourArrayOfPointers, int count);
    TimsdataInterface INSTANCE = Native.loadLibrary(ApplicationProperties.timsdatadllLocation, TimsdataInterface.class);
}

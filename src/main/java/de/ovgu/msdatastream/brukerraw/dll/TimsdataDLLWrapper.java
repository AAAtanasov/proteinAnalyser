package de.ovgu.msdatastream.brukerraw.dll;

// Helper class used to call timsdata.dll methods
public class TimsdataDLLWrapper {
	private TimsdataInterface tdInterface = TimsdataInterface.INSTANCE;
	private long handle = 0;
	
	public TimsdataDLLWrapper(String analysisDir) {
		this.timsOpen(analysisDir);
	}
	
	public void timsOpen(String analysisDir) {
		long result = tdInterface.tims_open(analysisDir);
		handle = result;
		if (result == 0) {
			throw new RuntimeException("Failed to timsOpen");
		}
	}

	public long getHandle(){
		return this.handle;
	}
	
	public long timsReadScansV2(long handle, int frameId, int scanBegin, int scanEnd, int[] buffer, int length) {
		long result = tdInterface.tims_read_scans_v2(handle, frameId, scanBegin, scanEnd, buffer, length);
		if (result == 0) {
			throw new RuntimeException("Failed to tims_read_scans_v2");
		}
		return result;
	}
	
	public void indexToMz(TimsdataPayloadContainer inputData) {
		long result = tdInterface.tims_index_to_mz(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert index to mz");
		}
	}

}

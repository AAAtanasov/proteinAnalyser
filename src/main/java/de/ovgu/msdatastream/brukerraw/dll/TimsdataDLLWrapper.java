package de.ovgu.msdatastream.brukerraw.dll;

// Helper class used to call timsdata.dll methods
public class TimsdataDLLWrapper {
	
	private TimsdataInterface tdInterface = TimsdataInterface.INSTANCE;
	// TODO get rid of this hack
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

	public void scanNumToOneOverK0(TimsdataPayloadContainer inputData) {
		long result = tdInterface.tims_scannum_to_oneoverk0(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert scannum to one over K0");
		}
	}

	public void oneOverK0ToScanNum(TimsdataPayloadContainer inputData) {
		long result = tdInterface.tims_oneoverk0_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert one over K0 to scannum");
		}
	}

	public void voltageToScanNum(TimsdataPayloadContainer inputData, TimsdataInterface dll) {
		long result = tdInterface.tims_voltage_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert voltage to scan number");
		}
	}

	public void scanNumToVoltage (TimsdataPayloadContainer inputData, TimsdataInterface dll) {
		long result = tdInterface.tims_scannum_to_voltage(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert scan number to voltage");
		}
	}

}

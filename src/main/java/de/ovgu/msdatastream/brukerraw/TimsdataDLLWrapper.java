package de.ovgu.msdatastream.brukerraw;

// Helper class used to call timsdata.dll methods
public class TimsdataDLLWrapper {
	
	private TimsdataInterface tdInterface = TimsdataInterface.INSTANCE;
	
	public TimsdataDLLWrapper(String analysisDir) {
		this.timsOpen(analysisDir);
	}
	
	public void timsOpen(String analysisDir) {
		long result = tdInterface.tims_open(analysisDir);
		if (result == 0) {
			throw new RuntimeException("Failed to timsOpen");
		}
	}
	
	public void indexToMz(PayloadContainer inputData) {
		long result = tdInterface.tims_index_to_mz(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert index to mz");
		}
	}

	public void scanNumToOneOverK0(PayloadContainer inputData) {
		long result = tdInterface.tims_scannum_to_oneoverk0(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert scannum to one over K0");
		}
	}

	public void oneOverK0ToScanNum(PayloadContainer inputData) {
		long result = tdInterface.tims_oneoverk0_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert one over K0 to scannum");
		}
	}

	public void voltageToScanNum(PayloadContainer inputData, TimsdataInterface dll) {
		long result = tdInterface.tims_voltage_to_scannum(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert voltage to scan number");
		}
	}

	public void scanNumToVoltage (PayloadContainer inputData, TimsdataInterface dll) {
		long result = tdInterface.tims_scannum_to_voltage(inputData.handle, inputData.frameId, inputData.inArrayOfPointers, inputData.outArrayOfPointers, inputData.count);
		if (result == 0) {
			throw new RuntimeException("Failed to convert scan number to voltage");
		}
	}

	// TODO: this does not belong here?
//	//TODO: implement better search if this functionality will be used
//	public int[] npDigitizeImplementation(double[] searchedData, double[] mzBins){
//		int[] result = new int[searchedData.length];
//		for (int i = 0; i < searchedData.length; i++) {
//			for (int j = 0; j < mzBins.length - 1 ; j++) {
//				if (searchedData[i] > mzBins[j] && searchedData[i] < mzBins[j + 1]){
//					result[i] = j + 1;
//					break;
//				}
//			}
//		}
//		return result;
//	}

}

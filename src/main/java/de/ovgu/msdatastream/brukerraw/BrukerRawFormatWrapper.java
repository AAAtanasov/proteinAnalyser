package de.ovgu.msdatastream.brukerraw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import de.ovgu.msdatastream.brukerraw.dll.TimsdataDLLWrapper;
import de.ovgu.msdatastream.brukerraw.dll.TimsdataInterface;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerFrame;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPasefFrameMSMSInfo;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.SQLWrapper;
import de.ovgu.msdatastream.model.Spectrum;

public class BrukerRawFormatWrapper {

	// file access
	private SQLWrapper sql;
	private TimsdataDLLWrapper dll;
	
	// data
	private HashMap<Long, BrukerFrame> frames;
	private HashMap<Long, BrukerPrecusor> precursors;
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	
	// convenience mappings
	private HashMap<Long, HashSet<BrukerPrecusor>> frameToPrecursorMapping;
	private HashMap<Long, HashSet<BrukerFrame>> precursorToFrameMapping;
	
	public BrukerRawFormatWrapper(String analysisDir) {
		dll = new TimsdataDLLWrapper(analysisDir);
		sql = new SQLWrapper();
		frames = new HashMap<Long, BrukerFrame>();
		precursors = new HashMap<Long, BrukerPrecusor>();
		pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		frameToPrecursorMapping = new HashMap<Long, HashSet<BrukerPrecusor>>();
		precursorToFrameMapping = new HashMap<Long, HashSet<BrukerFrame>>();
		readMetaData();
	}
	
	public void readMetaData() {
		try {
			ResultSet rs;
			// get all metadata and save it as frames and precursors
			rs = sql.executeSQL("SELECT * FROM Frames f INNER JOIN PasefFrameMSMsInfo ms2 ON f.Id = ms2.Frame INNER JOIN Precursors p ON p.Id = ms2.Precursor");
			HashSet<Long> frameIdSet = new HashSet<Long>();
			HashSet<Long> precIdsSet = new HashSet<Long>();
			while (rs.next()) {
				Long frameID = (long) rs.getInt("Frame");
				Long precursorID = (long) rs.getInt("Precursor");
				// read data
				BrukerPasefFrameMSMSInfo bkFr = new BrukerPasefFrameMSMSInfo(this, rs, sql);
				this.pasefItems.add(bkFr);
				// create frames
				BrukerFrame frame;
				if (frameIdSet.contains(frameID)) {
					frame = frames.get(frameID);
				} else {
					frame = new BrukerFrame(bkFr);
					frames.put(frameID, frame);
					frameToPrecursorMapping.put(frameID, new HashSet<BrukerPrecusor>());
				}
				// create precursors
				BrukerPrecusor precursor;
				if (precIdsSet.contains(precursorID)) {
					precursor = precursors.get(precursorID);
				} else {
					precursor = new BrukerPrecusor(bkFr);
					precursors.put(precursorID, precursor);
					precursorToFrameMapping.put(precursorID, new HashSet<BrukerFrame>());
				}
				// update mappings
				frameToPrecursorMapping.get(frameID).add(precursor);
				precursorToFrameMapping.get(precursorID).add(frame);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Frames: " + frames.size());
		System.out.println("Precursors: " + precursors.size());
		System.out.println("Pasefs: " + pasefItems.size());
	}
	
	public ArrayList<BrukerFrame> getFrames() {
		ArrayList<BrukerFrame> returnList = new ArrayList<BrukerFrame>();
		returnList.addAll(frames.values());
		return returnList;
	}
	
	public ArrayList<BrukerPrecusor> getPrecursors() {
		ArrayList<BrukerPrecusor> returnList = new ArrayList<BrukerPrecusor>();
		returnList.addAll(precursors.values());
		return returnList;
	}
	public Spectrum readRawdata2(BrukerFrame f) {
		Spectrum spectrum = null;
		int[] pivotArr = new int[512];
		long requiredLength = dll.timsReadScansV2(dll.handle, f.frameId, 0, f.numScans, pivotArr, 512);
		int correctSize = ((int) requiredLength / 4 ) + 1;
		if (correctSize > 16777216) {
			throw  new RuntimeException("Maximum expected frame size exceeded");
		}
		pivotArr = new int[correctSize];
		dll.timsReadScansV2(dll.handle, f.frameId, 0, f.numScans, pivotArr, correctSize);
		
		// TODO: why is the first entry so big (64k) ??? MS1 ??
//		System.out.println("Test: " + correctSize);
		
		int[] indicies = new int[f.numScans];
        int[] intensities = new int[f.numScans];
        indicies = Arrays.copyOfRange(pivotArr, 0, f.numScans);
        
        for (int i = 0; i > pivotArr.length; i++) {
        	if (pivotArr[i] > 0) {
        		System.out.println("TEST " + pivotArr[i]);
        	}
        }
        
        int index = f.numScans + 1;
        for (int i = 0; i >  f.numScans; i++) {
        	if (indicies[i] != 0) {
//        		if (pivotArr[index] != 0) {
//        			System.out.println(f.frameId);
//        		}
        		intensities[i] = pivotArr[index];
        		index++;
        	}
        }
        
//        System.out.println("correct?");
		
		return spectrum;
	}

	public Spectrum readRawdata(BrukerFrame f) {
		
		// TODO: main logic of the dll, make this work again
		
//        int minMz = 0;
//        int maxMz = 3000;
//        int numPlotBins = 500;
        ArrayList<Spectrum> frameScanInformationList = new ArrayList<Spectrum>();
//        INDArray mzbinLinspace =  Nd4j.linspace(minMz, maxMz, numPlotBins);
//        double[] mzBins = new double[mzbinLinspace.length()];
//        for (int i = 0; i < mzbinLinspace.length(); i++) {
//            mzBins[i] = mzbinLinspace.getDouble(i);
//        }
//        double[] summedIntensities = new double[(numPlotBins + 1)];
        // TODO: fix this hack
        long handle = dll.handle;
        
//		ArrayList<ResultWrapper> wrappers = this.readScans(f.frameId, 0, f.numScans, handle);
		// old readscans method
        
//        public ArrayList<ResultWrapper> readScans(int frameId, int scanBegin, int scanEnd, long handle) {
		
        Integer frameId = f.frameId;
        int scanBegin = 0; 
        int scanEnd = f.numScans;
        
        //Initially set data amount seen from the example implementation
        int initialFrameBUfferSize = 128;
        int maxBufferSize = 16777216;
        // Pivot array contains all the scan information regarding a Frame. It is constructed from 3 separate data arrays
        // where the first half of the array with size = scanEnd - scanBegin is used to describe the peaks of each scan.
        // Second part of the array is build by the concatenation of indexes-intensities pairs. The second part of the
        // array is like a stack for scans with peaks bigger than 0, each result is added separately, the next peak
        // information comes after that
        int[] pivotArr;
        int countDebug = 0;
        while (true) {
        	countDebug++;
//        	System.out.println(countDebug);
            pivotArr = new int[initialFrameBUfferSize];

            // this method is problematic
            long requiredLength = dll.timsReadScansV2(handle, frameId, scanBegin, scanEnd, pivotArr, 4 * initialFrameBUfferSize);
//            System.out.println("requiredLength " + requiredLength);

            if (requiredLength == 0) {
                throw  new RuntimeException("Timsdata error");
                //TODO: check if calling the dll errors is needed
            }
            
            // what is the point of this??
            if (requiredLength > 4 * initialFrameBUfferSize) {
                if (requiredLength > maxBufferSize) {
                    throw  new RuntimeException("Maximum expected frame size exceeded");
                }
                initialFrameBUfferSize = ((int)requiredLength / 4 ) + 1; // grow buffer size
            } else {
                break;
            }
        }

        int startIndex = scanEnd - scanBegin;
        int endOfScan = startIndex;
        int currentLength = pivotArr.length;
        int tempInt = startIndex;

        for (int i = 0; i > pivotArr.length; i++) {
        	if (pivotArr[i] > 0) {
        		System.out.println("TEST " + pivotArr[i]);
        	}
        }

        ArrayList<ResultWrapper> resultWrappers = new ArrayList<ResultWrapper>();

        int[] intensities;
        int[] indicies;
        
        // why is there a loop here???
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
		
        ArrayList<ResultWrapper> wrappers = resultWrappers;
        
		PayloadContainer container;
//		System.out.println("Index2MZLoop Starts");
		int count = 0;
		for (ResultWrapper wrapper: wrappers) {
			count++;
//			System.out.println("Wrapper " + count + " Of " + wrappers.size());
			double[] indexArray = new double[wrapper.indicies.length];
			// shouldnt those indexes be an int
			for (int i = 0; i < wrapper.indicies.length; i++) {
				indexArray[i] = (double)wrapper.indicies[i];
			}

			container = new PayloadContainer();
			container.handle = handle;
			container.frameId = f.frameId;
			container.inArrayOfPointers = indexArray;
			container.outArrayOfPointers = new double[wrapper.indicies.length];
			container.count = wrapper.indicies.length;
			// Load mzindex into container.outArrayOfPointers
			dll.indexToMz(container);
			if (container.outArrayOfPointers.length > 0) {
				//Needed mzIndex array and intensities array
				Spectrum mzWrapper = new Spectrum();
				mzWrapper.mzArray = container.outArrayOfPointers;
				mzWrapper.intensitiesArray = wrapper.intensities;
				frameScanInformationList.add(mzWrapper);
			}
		}
//		System.out.println("Finished: " + frameScanInformationList.size());
//		for (Spectrum spec : frameScanInformationList) {
//			for (int i = 0; i > 10; i++) {
//				System.out.println("MZ: " + spec.mzArray[i] + " INT: " +spec.intensitiesArray[i]);
//			}
//		}
		
		return new Spectrum();
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}
	
}

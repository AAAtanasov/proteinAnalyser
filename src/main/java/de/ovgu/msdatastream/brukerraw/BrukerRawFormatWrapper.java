package de.ovgu.msdatastream.brukerraw;

import de.ovgu.msdatastream.brukerraw.dll.TimsdataDLLWrapper;
import de.ovgu.msdatastream.brukerraw.dll.TimsdataPayloadContainer;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerFrame;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPasefFrameMSMSInfo;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.SQLWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BrukerRawFormatWrapper {

	// file access
	private SQLWrapper sql;
	private TimsdataDLLWrapper dll;
	
	// data
	private HashMap<Integer, BrukerFrame> frames;
	private HashMap<Integer, BrukerPrecusor> precursors;
	private ArrayList<BrukerPasefFrameMSMSInfo> pasefItems;
	
	// convenience mappings
	private HashMap<Integer, HashSet<BrukerPrecusor>> frameToPrecursorMapping;
	private HashMap<Integer, HashSet<BrukerFrame>> precursorToFrameMapping;

	//constants
	private int initialFrameBufferSize = 128;
	private int maxBufferSize = 16777216;
	
	public BrukerRawFormatWrapper(String analysisDir) {
		dll = new TimsdataDLLWrapper(analysisDir);
		sql = new SQLWrapper();
		frames = new HashMap<Integer, BrukerFrame>();
		precursors = new HashMap<Integer, BrukerPrecusor>();
		//Not needed anywhere - will delete after confirm fixme
		//pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
		frameToPrecursorMapping = new HashMap<Integer, HashSet<BrukerPrecusor>>();
		precursorToFrameMapping = new HashMap<Integer, HashSet<BrukerFrame>>();
		readMetaData();
	}
	
	public void readMetaData() {
		try {
			// get all metadata and save it as frames and precursors
			PreparedStatement ps = sql.conn.prepareStatement("SELECT * FROM Frames f INNER JOIN PasefFrameMSMsInfo ms2 ON f.Id = ms2.Frame INNER JOIN Precursors p ON p.Id = ms2.Precursor");
			ResultSet rs = ps.executeQuery();
			// do we need a second set of these?
			HashSet<Long> frameIdSet = new HashSet<Long>();
			HashSet<Long> precIdsSet = new HashSet<Long>();

			while (rs.next()) {
				// edit here

				Integer frameID = rs.getInt("Frame");
				Integer precursorID = rs.getInt("Precursor");

				// read data
				BrukerPasefFrameMSMSInfo bkFr = new BrukerPasefFrameMSMSInfo(this, rs);

				//this.pasefItems.add(bkFr); - removed because was not used and frames-precursors already contain
				// references to

				// create frames
				BrukerFrame frame;
				if (frameIdSet.contains(frameID)) {
					frame = frames.get(frameID);
				} else {
					frame = new BrukerFrame(this, rs, bkFr);
					frame.time = rs.getTime("Time").toString();
					frames.put(frameID, frame);
					frameToPrecursorMapping.put(frameID, new HashSet<BrukerPrecusor>());
				}
				// create precursors
				BrukerPrecusor precursor;
				if (precIdsSet.contains(precursorID)) {
					precursor = precursors.get(precursorID);
				} else {
					precursor = new BrukerPrecusor(this, rs, bkFr);
					precursors.put(precursorID, precursor);
					precursorToFrameMapping.put(precursorID, new HashSet<BrukerFrame>());
				}
				// update mappings
				frameToPrecursorMapping.get(frameID).add(precursor);
				precursorToFrameMapping.get(precursorID).add(frame);
			}
			ps.close();
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
	
	public BrukerFrame getFrame(Integer frameID) {
		return frames.get(frameID);
	}

	public BrukerPrecusor getPrecursor(Integer precursorID) {
		return precursors.get(precursorID);
	}
	
	public ArrayList<BrukerPrecusor> getPrecursors() {
		ArrayList<BrukerPrecusor> returnList = new ArrayList<BrukerPrecusor>();
		returnList.addAll(precursors.values());
		return returnList;
	}
	
	
	public Spectrum readRawdata(BrukerFrame brukerFrame, int scanBegin, int scanEnd) {
		
		// TODO: clean up :)
		// TODO: more comments!!
		// TODO: hard-coded values into the property file! 
		
		
		Spectrum spectrum = new Spectrum();
		int[] pivotArr = growBufferSize(brukerFrame.frameId, scanBegin, scanEnd);

		
		// figure out size of arrays
		int arraySize = 0;
		int scanRange = scanEnd - scanBegin;
		for (int i = 0; i < scanRange; i++) {
			arraySize += pivotArr[i];
		}
		// initialize with correct size 
		Integer[] indicies = new Integer[arraySize];
        int[] intensities = new int[arraySize];

		fillIntensitiesAndIndexesForSpecter(pivotArr, indicies, intensities, scanRange );

        // prepare payload container and retrieve mzvalues
		TimsdataPayloadContainer container = new TimsdataPayloadContainer();
		container.handle = dll.getHandle();
		container.frameId = brukerFrame.frameId;
		container.inArrayOfPointers = new double[indicies.length];
		for (int i = 0; i < indicies.length; i++) {
			container.inArrayOfPointers[i] = (double)indicies[i];
		}
		container.outArrayOfPointers = new double[indicies.length];
		container.count = indicies.length;
		// Load mzindex into container.outArrayOfPointers
		dll.indexToMz(container);

		// do we need this check?
		if (container.outArrayOfPointers.length > 0) {
			spectrum.appendData(new Spectrum(container.outArrayOfPointers, intensities));
		}

		spectrum.frameId = brukerFrame.frameId;
		spectrum.scanBegin = scanBegin;
		spectrum.scanEnd = scanEnd;
		spectrum.rtinseconds = brukerFrame.time;
//		spectrum.charge =

		return spectrum;
	}

	public void close() {
		this.dll = null;
		this.sql.closeConnection();
	}

	private int[] growBufferSize(int frameId, int scanBegin, int scanEnd) {
		while (true) {
			int cnt = initialFrameBufferSize;
			int[] pivotArr = new int[cnt];
			int len = 4 * cnt;
			long handle = dll.getHandle();

			long requiredLength = dll.timsReadScansV2(handle, frameId, scanBegin, scanEnd, pivotArr, len);

			if (requiredLength == 0) {
				throw  new RuntimeException("Timsdata error");
			}

			if (requiredLength > len) {
				if (requiredLength > maxBufferSize) {
					throw  new RuntimeException("Maximum expected frame size exceeded");
				}
				initialFrameBufferSize = ((int)requiredLength / 4 )+ 1; // grow buffer size
			} else {
				return pivotArr;
			}
		}
	}

	private void fillIntensitiesAndIndexesForSpecter(int[] pivotArr, Integer[] indicies, int[] intensities, int scanRange){
		// the index running over the concatenated part of the pivotArr where the actual data is
		int indexPivotArr = scanRange;
		// the indicies for our indicies/intensities arrays
		int indexData = 0;
		// loop over first part of pivotArray using i
		for (int i = 0; i <  scanRange; i++) {
			// get the number of peaks to be collected
			int npeaks = pivotArr[i];
			// proceed if there are peaks to be collected
			while (npeaks != 0) {
				// collect peaks
				// mzindex -> -> first we have npeaks times mzindicies
				indicies[indexData] = pivotArr[indexPivotArr];
				// intensity -> first we have npeaks times intensities
				intensities[indexData] = pivotArr[indexPivotArr + pivotArr[i]];
				indexPivotArr++;
				// move indicies
				indexData++;
				npeaks--;
				// weird bit: we have to jump over the intensity portion here to prepare for the next peaks
				if (npeaks == 0) {
					indexPivotArr += pivotArr[i];
				}
			}
		}
	}
	
}

package de.ovgu.msdatastream.brukerraw;

<<<<<<< HEAD
import de.ovgu.msdatastream.brukerraw.dll.TimsdataDLLWrapper;
import de.ovgu.msdatastream.brukerraw.dll.TimsdataPayloadContainer;
import de.ovgu.msdatastream.brukerraw.sqllite.*;

=======
import de.ovgu.msdatastream.ApplicationProperties;
import de.ovgu.msdatastream.brukerraw.dll.TimsdataDLLWrapper;
import de.ovgu.msdatastream.brukerraw.dll.TimsdataPayloadContainer;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerFrame;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPasefFrameMSMSInfo;
import de.ovgu.msdatastream.brukerraw.sqllite.BrukerPrecusor;
import de.ovgu.msdatastream.brukerraw.sqllite.SQLWrapper;
import de.ovgu.msdatastream.model.Spectrum;

import java.sql.Connection;
>>>>>>> refs/heads/merge_branch_streaming_processing
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BrukerRawFormatWrapper {

<<<<<<< HEAD
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

    public BrukerRawFormatWrapper(String analysisDir) {
        dll = new TimsdataDLLWrapper(analysisDir);
        sql = new SQLWrapper();
        frames = new HashMap<Integer, BrukerFrame>();
        precursors = new HashMap<Integer, BrukerPrecusor>();
        pasefItems = new ArrayList<BrukerPasefFrameMSMSInfo>();
        frameToPrecursorMapping = new HashMap<Integer, HashSet<BrukerPrecusor>>();
        precursorToFrameMapping = new HashMap<Integer, HashSet<BrukerFrame>>();
        readMetaData();
    }

    public void readMetaData() {
        try {
            // get all metadata and save it as frames and precursors
            PreparedStatement ps = sql.conn.prepareStatement("SELECT * FROM Frames f INNER JOIN PasefFrameMSMsInfo ms2 ON f.Id = ms2.Frame INNER JOIN Precursors p ON p.Id = ms2.Precursor");
            ResultSet rs = ps.executeQuery();
            HashSet<Long> frameIdSet = new HashSet<Long>();
            HashSet<Long> precIdsSet = new HashSet<Long>();
            while (rs.next()) {
                Integer frameID = rs.getInt("Frame");
                Integer precursorID = rs.getInt("Precursor");
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
//        ArrayList<BrukerFrame> returnList = new ArrayList<BrukerFrame>();
//        returnList.addAll(frames.values());
        return new ArrayList <BrukerFrame>(frames.values());
    }

    public BrukerFrame getFrame(Integer frameID) {
        return frames.get(frameID);
    }

    public ArrayList<BrukerPrecusor> getPrecursors() {
//        ArrayList<BrukerPrecusor> returnList = new ArrayList<BrukerPrecusor>();
//        returnList.addAll(precursors.values());
        return new ArrayList <BrukerPrecusor>(precursors.values());
    }


    public PeakListContainer readRawdata(BrukerFrame f, int scanBegin, int scanEnd) {

        // TODO: clean up :)
        // TODO: more comments!!
        // TODO: hard-coded values into the property file!


        PeakListContainer peakListContainer = new PeakListContainer();

      /*  *//* Trying API*//*
        Comparable id = "xx";
        String name = "1";
        int index =1;
        DataProcessing defaultDataProcessing = new DataProcessing(id, null);
        List<BinaryDataArray> barray = new ArrayList<BinaryDataArray>();
        String spotID = "";
        SourceFile sourceFile = new SourceFile(null,null);
        ScanList scanList = new ScanList(null,null);
        List < Precursor > precursors  = new ArrayList<Precursor>();
        List < ParamGroup > products = new ArrayList<ParamGroup>();
        List < Peptide > peptide  = new ArrayList<Peptide>();*/



        int[] pivotArr = new int[512];
        long requiredLength = dll.timsReadScansV2(dll.handle, f.frameId, scanBegin, scanEnd, pivotArr, 512);
        int correctSize = ((int) requiredLength / 4) + 1;
        if (correctSize > 16777216) {
            throw new RuntimeException("Maximum expected frame size exceeded");
        }
        pivotArr = new int[correctSize];
        dll.timsReadScansV2(dll.handle, f.frameId, scanBegin, scanEnd, pivotArr, correctSize * 4);

        // figure out size of arrays
        int arraySize = 0;
        for (int i = 0; i < (scanEnd - scanBegin); i++) {
            arraySize += pivotArr[i];
        }
        // initialize with correct size
        int[] indicies = new int[arraySize];
=======
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
	private HashSet<String> processedFramePrecursorPairs;

	//constants
    private ApplicationProperties applicationProperties;

	public BrukerRawFormatWrapper(ApplicationProperties applicationProperties) throws SQLException {
	    this.applicationProperties = applicationProperties;
		dll = new TimsdataDLLWrapper(applicationProperties.getAnalysisDir());
		sql = new SQLWrapper(applicationProperties);
		frames = new HashMap<Integer, BrukerFrame>();
		precursors = new HashMap<Integer, BrukerPrecusor>();
		frameToPrecursorMapping = new HashMap<Integer, HashSet<BrukerPrecusor>>();
		precursorToFrameMapping = new HashMap<Integer, HashSet<BrukerFrame>>();

		//ensure table is present
		PreparedStatement createTableIfNotExist = sql.conn.prepareStatement("CREATE TABLE IF NOT EXISTS ProcessedFramePrecursorPairs (FrameId INTEGER , PrecursorId INTEGER, PRIMARY KEY(FrameId, PrecursorId));");
		createTableIfNotExist.execute();
		createTableIfNotExist.close();

		processedFramePrecursorPairs = populateIteratedPairs();

		readMetaData(processedFramePrecursorPairs);
	}

	public Connection getCurrentConnection() {
		return this.sql.conn;
	}

	private HashSet<String> populateIteratedPairs() throws SQLException {
		HashSet<String> resultSet = new HashSet<>();

		PreparedStatement extractPairs = sql.conn.prepareStatement("SELECT * FROM ProcessedFramePrecursorPairs");
		ResultSet rs = extractPairs.executeQuery();

		while (rs.next()){
			String constructedIdentifier = rs.getString("FrameId") + "_" + rs.getString("PrecursorId");
			resultSet.add(constructedIdentifier);
		}


		return resultSet;
	}
	
	public void readMetaData(HashSet<String> processedFramePrecursorSet) {
		try {
			PreparedStatement ps = sql.conn.prepareStatement(applicationProperties.getPrecursorJoinQuerry());

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Integer frameID = rs.getInt("Frame");
				Integer precursorID = rs.getInt("Precursor");
				String framePrecursorIndex = frameID.toString() + "_" + precursorID.toString();

				if (processedFramePrecursorSet.contains(framePrecursorIndex)){
					continue;
				}

				processedFramePrecursorSet.add(framePrecursorIndex);

				// read data
				BrukerPasefFrameMSMSInfo bkFr = new BrukerPasefFrameMSMSInfo(this, rs);

				// create frames
				BrukerFrame frame;
				if (frames.containsKey(frameID)) {
					frame = frames.get(frameID);
					//assume here each iteration is bound to PasefFrameMsMsInfo and this will introduce always new such records
					frame.addPasefItem(bkFr);
				} else {
					frame = new BrukerFrame(this, rs, bkFr);
					frames.put(frameID, frame);
					frameToPrecursorMapping.put(frameID, new HashSet<BrukerPrecusor>());
				}
				// create precursors
				BrukerPrecusor precursor;
				if (precursors.containsKey(precursorID)) {
					precursor = precursors.get(precursorID);
					precursor.addPasefItem(bkFr);
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
>>>>>>> refs/heads/merge_branch_streaming_processing
        int[] intensities = new int[arraySize];
<<<<<<< HEAD
        // the index running over the concatenated part of the pivotArr where the actual data is
        int indexPivotArr = (scanEnd - scanBegin);
        // the indicies for our indicies/intensities arrays
        int indexData = 0;
        // loop over first part of pivotArray using i
        for (int i = 0; i < (scanEnd - scanBegin); i++) {
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
=======

		fillIntensitiesAndIndexesForSpecter(pivotArr, indicies, intensities, scanRange );

>>>>>>> refs/heads/merge_branch_streaming_processing
        // prepare payload container and retrieve mzvalues
<<<<<<< HEAD
        TimsdataPayloadContainer container = new TimsdataPayloadContainer();
        container.handle = dll.handle;
        container.frameId = f.frameId;
        container.inArrayOfPointers = new double[indicies.length];
=======
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

		if (container.outArrayOfPointers.length > 0) {
			spectrum.appendData(new Spectrum(container.outArrayOfPointers, intensities));
		}

		return spectrum;
	}
>>>>>>> refs/heads/merge_branch_streaming_processing

<<<<<<< HEAD

        /*double[] binaryDoubleArray = new double[arraySize];
        uk.ac.ebi.pride.data.core.Spectrum spectrum1 = new uk.ac.ebi.pride.data.core.Spectrum(id, name,index
                , defaultDataProcessing,*//*int defaultArrayLength*//* arraySize ,barray, spotID,  sourceFile,
                scanList,  precursors, products, (Peptide) peptide);*/

        for (int i = 0; i < indicies.length; i++) {
            container.inArrayOfPointers[i] = indicies[i];
        }
        container.outArrayOfPointers = new double[indicies.length];
        container.count = indicies.length;
        // Load mzindex into container.outArrayOfPointers
        dll.indexToMz(container);
        if (container.outArrayOfPointers.length > 0) {
            peakListContainer.appendData(new PeakListContainer(container.outArrayOfPointers, intensities));

        }
        return peakListContainer;
    }

    public void close() {
        this.dll = null;
        this.sql.closeConnection();
    }

=======
	public void close() {
		this.dll = null;
		this.sql.closeConnection();
	}

	public ApplicationProperties getApplicationProperties() {
		return applicationProperties;
	}

	private int[] growBufferSize(int frameId, int scanBegin, int scanEnd) {
		while (true) {
			int cnt = applicationProperties.getInitialFrameBufferSize();
			int[] pivotArr = new int[cnt];
			int len = 4 * cnt;
			long handle = dll.getHandle();

			long requiredLength = dll.timsReadScansV2(handle, frameId, scanBegin, scanEnd, pivotArr, len);

			if (requiredLength == 0) {
				throw  new RuntimeException("Timsdata error");
			}

			if (requiredLength > len) {
				if (requiredLength > applicationProperties.getMaxBufferSize()) {
					throw  new RuntimeException("Maximum expected frame size exceeded");
				}
				applicationProperties.setInitialFrameBufferSize(((int)requiredLength / 4 )+ 1);// grow buffer size
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
	
>>>>>>> refs/heads/merge_branch_streaming_processing
}

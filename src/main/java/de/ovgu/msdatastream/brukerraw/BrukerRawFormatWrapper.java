package de.ovgu.msdatastream.brukerraw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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

	public Spectrum readRawdata(BrukerFrame f) {
		
		// TODO: main logic of the dll, make this work again
		
        int minMz = 0;
        int maxMz = 3000;
        int numPlotBins = 500;
        ArrayList<MzArrayWrapper> frameScanInformationList = new ArrayList<MzArrayWrapper>();
        INDArray mzbinLinspace =  Nd4j.linspace(minMz, maxMz, numPlotBins);
        double[] mzBins = new double[mzbinLinspace.length()];
        for (int i = 0; i < mzbinLinspace.length(); i++) {
            mzBins[i] = mzbinLinspace.getDouble(i);
        }
        double[] summedIntensities = new double[(numPlotBins + 1)];
        // TODO: fix this
        long handle = 1; 
        Timsdata tdimp = new Timsdata();
		ArrayList<ResultWrapper> wrappers = tdimp.readScans(f.frameId, 0, f.numScans, handle);
		PayloadContainer container;
		for (ResultWrapper wrapper: wrappers) {
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
				MzArrayWrapper mzWrapper = new MzArrayWrapper();
				mzWrapper.mzArray = container.outArrayOfPointers;
				mzWrapper.intensitiesArray = wrapper.intensities;
				frameScanInformationList.add(mzWrapper);
			}
		}
		for (MzArrayWrapper spec : frameScanInformationList) {
			for (int i = 0; i > 10; i++) {
				System.out.println("MZ: " + spec.mzArray[i] + " INT: " +spec.intensitiesArray[i]);
			}
		}
		
		return new Spectrum();
	}

	public void close() {
		// TODO Auto-generated method stub
	}
	
}

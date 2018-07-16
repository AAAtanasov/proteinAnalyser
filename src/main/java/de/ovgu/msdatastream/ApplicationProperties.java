package de.ovgu.msdatastream;


// TODO: RENAME!!
public class ApplicationProperties {
	private String connectionUrl;
	private String analysisDir;

	public static final String timsdatadllLocation = "src/main/resources/timsdata.dll";
	private final String targetFile = "test.mgf";

	private int initialFrameBufferSize = 128;
	private int maxBufferSize = 16777216;
	private int batchSize = 1000;
	private final String kafkaUrl  = "localhost:9092";

	public ApplicationProperties(String tdfDirectory){
		this.analysisDir = tdfDirectory;
		this.connectionUrl = "jdbc:sqlite:" + this.analysisDir + "\\analysis.tdf";

	}

	public int getBatchSize() {
		return batchSize;
	}

	public String getKafkaUrl() {
		return kafkaUrl;
	}

	public final String precursorJoinQuerry = "SELECT f.Id, ms2.Frame, p.Id, f.Polarity, f.Time, f.NumScans, f.NumPeaks, ms2.ScanNumBegin, ms2.ScanNumEnd, ms2.Precursor, p.MonoisotopicMz, p.Intensity, p.Charge  FROM Frames f INNER JOIN PasefFrameMSMsInfo ms2 ON f.Id = ms2.Frame INNER JOIN Precursors p ON p.Id = ms2.Precursor";

	public final String insertProcessedIdsSqlString = "INSERT INTO ProcessedFramePrecursorPairs (FrameId, PrecursorId) VALUES (?, ?)";

	public String getTargetFile(){
		return this.targetFile;
	}

	public String getAnalysisDir(){
		return this.analysisDir;
	}

	public String getConnectionUrl(){
		return this.connectionUrl;
	}

	public int getInitialFrameBufferSize(){
		return this.initialFrameBufferSize;
	}

	public void setInitialFrameBufferSize(int newValue){
		this.initialFrameBufferSize = newValue;
	}

	public int getMaxBufferSize(){
		return this.maxBufferSize;
	}
	
}

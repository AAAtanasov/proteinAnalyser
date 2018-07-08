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

	public final String precursorJoinQuerry = "Select f.Id, nt.Frame, p.Id, f.Polarity, f.Time," +
			" f.NumScans, f.NumPeaks, nt.ScanNumBegin, nt.ScanNumEnd, nt.Precursor, p.MonoisotopicMz," +
			" p.Intensity, p.Charge From" +
			" (Select pf.Frame, pf.Precursor, pf.ScanNumBegin, pf.ScanNumEnd from PasefFrameMsMsInfo as pf" +
			" left join ProcessedFramePrecursorPairs as pp on pf.Precursor == pp.PrecursorId and pf.Frame = pp.FrameId" +
			" where FrameId is Null) as nt \n" +
			"inner join Frames as f on nt.Frame = f.Id inner join Precursors as p on nt.Precursor = p.Id";

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

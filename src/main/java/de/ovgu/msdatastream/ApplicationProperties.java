package de.ovgu.msdatastream;


// TODO: RENAME!!
public class ApplicationProperties {
	private String connectionUrl;
	private String analysisDir;

	public static final String timsdatadllLocation = "src/main/resources/timsdata.dll";
	private final String targetFile = "test.mgf";

	private int initialFrameBufferSize = 128;
	private int maxBufferSize = 16777216;

	public ApplicationProperties(String tdfDirectory){
		this.analysisDir = tdfDirectory;
		this.connectionUrl = "jdbc:sqlite:" + this.analysisDir + "\\analysis.tdf";

	}

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

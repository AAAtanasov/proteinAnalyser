package de.ovgu.msdatastream;


// TODO: RENAME!!
public class Properties {
	private String connectionUrl;
	private String analysisDir;

	public static final String timsdatadllLocation = "src/main/resources/timsdata.dll";
	private final String targetFile = "test.mgf";

	private int initialFrameBufferSize = 128;
	private int maxBufferSize = 16777216;



	// TODO: deal with file selection
	public Properties(String tdfDirectory){
		this.analysisDir = tdfDirectory;
		this.connectionUrl = "jdbc:sqlite:" + this.analysisDir + "\\analysis.tdf";

	}
	
//	public static final String connectionUrl = "jdbc:sqlite:C:\\Users\\kaysc\\Desktop\\Ecoli_04_RD2_01_1275.d\\analysis.tdf";
	//private String connectionUrl = "jdbc:sqlite:F:\\proteinProjData\\Roh\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d\\analysis.tdf";
	//	public static final String connectionUrl = "jdbc:sqlite:C:\\Users\\kaysc\\Desktop\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d\\analysis.tdf";
//	public static final String analysisDir = "C:\\Users\\kaysc\\Desktop\\Ecoli_04_RD2_01_1275.d";
	//private String analysisDir = "F:\\proteinProjData\\Roh\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d";
//	public static final String analysisDir = "C:\\Users\\kaysc\\Desktop\\Ecoli_1400V_200grad_PASEF_16_RD2_01_1290.d";

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

package de.ovgu.msdatastream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ApplicationProperties {
	private Properties properties;

	//Fields
	private String analysisDir;
	private String connectionUrl;
	public static final String timsdatadllLocation = "src/main/resources/timsdata.dll"; // always here
	private int initialFrameBufferSize = 0;

	public ApplicationProperties(String tdfDirectory){
		properties = new Properties();
		try {
			InputStream propertiesRes = getClass().getResourceAsStream("/project.properties");
			if(propertiesRes==null) {
				throw new RuntimeException("Cannot find file plugin.properties");
			}
			properties.load(propertiesRes);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read plugin.properties: " + e.getMessage(), e);
		}
		this.analysisDir = tdfDirectory;
		this.connectionUrl = "jdbc:sqlite:" + this.analysisDir + "\\analysis.tdf";

	}

	private String getProperty(String name) {
		Object value = properties.get(name);
		if (value == null)
			throw new RuntimeException("Property \"" + name + "\" is not defined");

		return (String) value;
	}

	public int getBatchSize() {
		return Integer.parseInt(getProperty("batchSize"));
	}

	public String getKafkaUrl() {
		return getProperty("kafkaUrl");
	}

	public  String getPrecursorJoinQuerry() {
		return getProperty("precursorJoinQuerry");
	}

	public String getInsertProcessedIdsSqlString(){
		return getProperty("insertPairsQuerry");
	};

	public String getTargetFile(){
		return getProperty("targetFile");
	}

	public Boolean getIsKafkaProducer() {
		return Boolean.parseBoolean(getProperty("isKafkaProducer"));
	}

	public String getKafkaTopic() {
		return getProperty("kafkaTopic");
	}

	public String getAnalysisDir(){
		return this.analysisDir;
	}

	public String getConnectionUrl(){
		return this.connectionUrl;
	}

	public int getMaxBufferSize(){
		return Integer.parseInt(getProperty("maxBufferSize"));
	}

	public int getInitialFrameBufferSize(){
		if (this.initialFrameBufferSize == 0) {
			this.initialFrameBufferSize = Integer.parseInt(getProperty("initialFrameBufferSize"));
		}
		return this.initialFrameBufferSize;
	}

	public void setInitialFrameBufferSize(int newValue){
		this.initialFrameBufferSize = newValue;
	}

	public Integer getMaxEmptyIterations() {
		return Integer.parseInt(getProperty("maxEmptyIterations"));
	}
	
}

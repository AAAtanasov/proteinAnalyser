package dllWrapper;

//import com.sun.jna.Library;


import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import sqliteReader.SQLiteJDBCDriverConnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimsdataDllWrapper {

    public static void main(String[] args) {

        SQLiteJDBCDriverConnection sqlite = new SQLiteJDBCDriverConnection();
        Connection connection = null;
        String analysisDir = "F:\\proteinProjData\\Ecoli_04_RD2_01_1275.d";
        //TODO: make it a variable
        String dateiName = "Ecoli_04_RD2_01_1275.d";


        try {
            connection = sqlite.getConnection();
            if (connection == null) {
                System.out.println("Failed connecting...");
                return;
            }
            Statement stmt = connection.createStatement();
            int frameId = 11;

            ResultSet frameIdResultSet = stmt.executeQuery("SELECT Id FROM Frames ");

            List<FrameDataWrapper> frames = new ArrayList<>();
            TimsdataService sdll = TimsdataService.INSTANCE;
            long handle = sdll.tims_open(analysisDir);


            int count = 0;

            // init file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("test.mgf")));



            while (frameIdResultSet.next()){
                int currentFrameId = frameIdResultSet.getInt("Id");
                Statement numScanStatement = connection.createStatement();
                ResultSet resultSet = numScanStatement.executeQuery("SELECT NumScans FROM Frames WHERE Id=" + currentFrameId);
                int numScans = resultSet.getInt("NumScans");

                Statement precursorStatement = connection.createStatement();
                ResultSet precursorMsInfo = precursorStatement.executeQuery()

                List<MzArrayWrapper> frameInformation = readFrame(currentFrameId, analysisDir, numScans, sdll, handle );

                FrameDataWrapper frameDataWrapper = new FrameDataWrapper();
                frameDataWrapper.frameId = currentFrameId;
                frameDataWrapper.frameInformation = frameInformation;


//                frames.add(frameDataWrapper);
                bufferedWriter.write("BEGIN IONS\n");
                bufferedWriter.write("TITLE=" + dateiName + "_FRAMEID="+ currentFrameId + "_NUMSCANS="+ numScans);


                bufferedWriter.write("END IONS\n\n");
                bufferedWriter.close();
break;


            }




            bufferedWriter.close();

            System.out.println("test");

        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException("SQLException: " + e.getMessage(), e);


        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("IOException: " + e.getMessage(), e);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }



    public static List<MzArrayWrapper> readFrame(int frameId, String analysisDir, int numScans, TimsdataService sdll, long handle){
        int minMz = 0;
        int maxMz = 3000;
        int numPlotBins = 500;
        List<MzArrayWrapper> frameScanInformationList = new ArrayList<>();


        INDArray mzbinLinspace =  Nd4j.linspace(minMz, maxMz, numPlotBins);
        double[] mzBins = new double[mzbinLinspace.length()];
        for (int i = 0; i < mzbinLinspace.length(); i++) {
            mzBins[i] = mzbinLinspace.getDouble(i);
        }


        //TODO: make this a variable and enable console application variant
        if (handle == 0){
            //This is an error, skip  for now;
            System.out.println("Handle was 0 for frameid: " + frameId);
            return frameScanInformationList;

        }
        double[] summedIntensities = new double[(numPlotBins + 1)];

        Timsdata timsdataImplementation = new Timsdata();
        List<ResultWrapper> wrappers = timsdataImplementation.readScans(frameId, 0, numScans, handle);

        PayloadContainer container;
        for (ResultWrapper wrapper: wrappers) {
            double[] indexArray = new double[wrapper.indicies.length];
            // shouldnt those indexes be an int
            for (int i = 0; i < wrapper.indicies.length; i++) {
                indexArray[i] = (double)wrapper.indicies[i];
            }

            container = new PayloadContainer();
            container.handle = handle;
            container.frameId = frameId;
            container.inArrayOfPointers = indexArray;
            container.outArrayOfPointers = new double[wrapper.indicies.length];
            container.count = wrapper.indicies.length;

            // Load mzindex into container.outArrayOfPointers
            ConversionFunctionHelper.indexToMz(container, sdll);

            if (container.outArrayOfPointers.length > 0) {
                //Needed mzIndex array and intensities array
                MzArrayWrapper mzWrapper = new MzArrayWrapper();
                mzWrapper.mzArray = container.outArrayOfPointers;
                mzWrapper.intensitiesArray = wrapper.intensities;
                frameScanInformationList.add(mzWrapper);
                //TODO: see if this is needed as this is part of the plot functionality
//                    int[] plotBins = ConversionFunctionHelper.
//                            npDigitizeImplementation(container.outArrayOfPointers, mzBins);
//
//                    for (int i = 0; i < wrapper.intensities.length; i++) {
//                        summedIntensities[plotBins[i]] += wrapper.intensities[i];
//
//                    }
            }
        }

        //TODO: check if this functionality will be required or is it just part of the example.
        //pythonExampleImplementation(numScans, handle, frameId, sdll);

        return frameScanInformationList;
    }


    public static void pythonExampleImplementation(int numScans, long handle, int frameId, TimsdataService sdll){
        double[] scanNumberAxis = new double[numScans];
        for (int i = 0; i < numScans; i++) {
            scanNumberAxis[i] = i;
        }
        PayloadContainer scanNumberContainer = new PayloadContainer();
        scanNumberContainer.handle = handle;
        scanNumberContainer.frameId = frameId;
        scanNumberContainer.inArrayOfPointers = scanNumberAxis;
        scanNumberContainer.outArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
        scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;

        //TODO: sdll part of container class
        ConversionFunctionHelper.scanNumToOneOverK0(scanNumberContainer, sdll);
        double[] ook0_axis = scanNumberContainer.outArrayOfPointers;

        scanNumberContainer.inArrayOfPointers = ook0_axis;
        scanNumberContainer.outArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
        ConversionFunctionHelper.oneOverK0ToScanNum(scanNumberContainer, sdll);
        scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
        double[] scan_number_from_ook0_axis = scanNumberContainer.outArrayOfPointers;

        scanNumberContainer.inArrayOfPointers = scanNumberAxis;
        scanNumberContainer.outArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
        scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
        ConversionFunctionHelper.scanNumToVoltage(scanNumberContainer, sdll);
        double[] voltageAxis = scanNumberContainer.outArrayOfPointers;

        scanNumberContainer.inArrayOfPointers = voltageAxis;
        scanNumberContainer.outArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
        scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
        ConversionFunctionHelper.voltageToScanNum(scanNumberContainer, sdll);
        double[] scan_number_from_voltage_axis = scanNumberContainer.outArrayOfPointers;

    }




}

package dllWrapper;

//import com.sun.jna.Library;


import com.sun.jna.*;
import org.nd4j.linalg.api.environment.Nd4jEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import sqliteReader.SQLiteJDBCDriverConnection;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class TimsdataDllWrapper {
//    public interface TimsdataDll extends Library {
//        long tims_open(String analysisDirectory);
//        TimsdataDll INSTANCE = (TimsdataDll)Native.loadLibrary("C:\\Users\\Anton\\Documents\\sqliteReader\\lib\\timsdata\\timsdata.dll", TimsdataDll.class);
//    }

    public static void main(String[] args) {

        SQLiteJDBCDriverConnection sqlite = new SQLiteJDBCDriverConnection();
        Connection connection = null;

        try {
            connection = sqlite.getConnection();
            if (connection == null) {
                System.out.println("Failed connecting...");
                return;
            }
            Statement stmt = connection.createStatement();
            int frameId = 10;

            ResultSet resultSet = stmt.executeQuery("SELECT NumScans FROM Frames WHERE Id=10");
            int numScans = resultSet.getInt("NumScans");


            int minMz = 0;
            int maxMz = 3000;
            int numPlotBins = 500;
            INDArray mzbinLinspace =  Nd4j.linspace(minMz, maxMz, numPlotBins);
            double[] mzBins = new double[mzbinLinspace.length()];
            for (int i = 0; i < mzbinLinspace.length(); i++) {
                mzBins[i] = mzbinLinspace.getDouble(i);
            }


            TimsdataService sdll = TimsdataService.INSTANCE;
            String analysisDir = "F:\\proteinProjData\\Ecoli_04_RD2_01_1275.d";
            long handle = sdll.tims_open(analysisDir);
            double[] summedIntensities = new double[(numPlotBins + 1)];

            Timsdata temp = new Timsdata();
            List<ResultWrapper> wrappers = temp.readScans(frameId, 0, numScans, handle);


            for (ResultWrapper wrapper: wrappers) {
                double[] indexArray = new double[wrapper.indicies.length];
                for (int i = 0; i < wrapper.indicies.length; i++) {
                    indexArray[i] = (double)wrapper.indicies[i];
                }
                PayloadContainer container = new PayloadContainer();
                container.handle = handle;
                container.frameId = frameId;
                container.inArrayOfPointers = indexArray;
                container.ourArrayOfPointers = new double[wrapper.indicies.length];
                container.count = wrapper.indicies.length;


                ConversionFunctionHelper.indexToMz(container, sdll);
                System.out.println("Asdas");

                if (container.ourArrayOfPointers.length > 0) {
                    int[] plotBins = ConversionFunctionHelper.
                            npDigitizeImplementation(container.ourArrayOfPointers, mzBins);
                    for (int i = 0; i < wrapper.intensities.length; i++) {
                        summedIntensities[plotBins[i]] += wrapper.intensities[i];

                    }
                }
            }

            double[] scanNumberAxis = new double[numScans];
            for (int i = 0; i < numScans; i++) {
                scanNumberAxis[i] = i;
            }
            PayloadContainer scanNumberContainer = new PayloadContainer();
            scanNumberContainer.handle = handle;
            scanNumberContainer.frameId = frameId;
            scanNumberContainer.inArrayOfPointers = scanNumberAxis;
            scanNumberContainer.ourArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
            scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;

            //TODO: sdll part of container class
            ConversionFunctionHelper.scanNumToOneOverK0(scanNumberContainer, sdll);
            double[] ook0_axis = scanNumberContainer.ourArrayOfPointers;

            scanNumberContainer.inArrayOfPointers = ook0_axis;
            scanNumberContainer.ourArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
            ConversionFunctionHelper.oneOverK0ToScanNum(scanNumberContainer, sdll);
            scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
            double[] scan_number_from_ook0_axis = scanNumberContainer.ourArrayOfPointers;

            scanNumberContainer.inArrayOfPointers = scanNumberAxis;
            scanNumberContainer.ourArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
            scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
            ConversionFunctionHelper.scanNumToVoltage(scanNumberContainer, sdll);
            double[] voltageAxis = scanNumberContainer.ourArrayOfPointers;

            scanNumberContainer.inArrayOfPointers = voltageAxis;
            scanNumberContainer.ourArrayOfPointers = new double[scanNumberContainer.inArrayOfPointers.length];
            scanNumberContainer.count = scanNumberContainer.inArrayOfPointers.length;
            ConversionFunctionHelper.voltageToScanNum(scanNumberContainer, sdll);
            double[] scan_number_from_voltage_axis = scanNumberContainer.ourArrayOfPointers;






            System.out.println("test");

        } catch (SQLException e){
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println(e.getMessage());

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




}

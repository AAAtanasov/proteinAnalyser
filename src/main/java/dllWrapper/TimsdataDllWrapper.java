package dllWrapper;

//import com.sun.jna.Library;


import com.sun.jna.*;
import org.nd4j.linalg.api.ndarray.INDArray;
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
            INDArray mzbins =  Nd4j.linspace(minMz, maxMz, numScans);


            TimsdataService sdll = TimsdataService.INSTANCE;
            String analysisDir = "F:\\proteinProjData\\Ecoli_04_RD2_01_1275.d";
            long test = sdll.tims_open(analysisDir);
            INDArray summedIntensities = Nd4j.zeros(numPlotBins + 1);

            Timsdata temp = new Timsdata();
            List<ResultWrapper> wrappers = temp.readScans(frameId, 0, numScans, test);



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

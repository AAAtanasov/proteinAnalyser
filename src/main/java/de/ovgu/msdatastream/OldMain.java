//package de.ovgu.msdatastream;
//
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.factory.Nd4j;
//
//import de.ovgu.msdatastream.brukerraw.TimsdataDLLWrapper;
//import de.ovgu.msdatastream.brukerraw.SQLiteJDBCDriverConnection;
//import de.ovgu.msdatastream.brukerraw.TimsdataInterface;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OldMain {
//
//    public static void main(String[] args) {
//        SQLiteJDBCDriverConnection sqlite = new SQLiteJDBCDriverConnection();
//        Connection connection = null;
//        // TODO: change appropriately
//        String analysisDir = "C:\\Users\\kaysc\\Desktop\\Ecoli_04_RD2_01_1275.d";
//        //TODO: make it a variable
//        String dateiName = analysisDir;
//    	
//        try {
//            connection = sqlite.getConnection();
//            if (connection == null) {
//                System.out.println("Failed connecting...");
//                throw new RuntimeException("Failed to connect to a database");
//            }
//            Statement stmt = connection.createStatement();
//
//            ResultSet frameIdResultSet = stmt.executeQuery("SELECT Id FROM Frames ");
//
//            List<FrameDataWrapper> frames = new ArrayList<FrameDataWrapper>();
//            TimsdataInterface sdll = TimsdataInterface.INSTANCE;
//            
//
//            // init file
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("test.mgf")));
//
//            int frameCount = 0;
//            while (frameIdResultSet.next()){
//            	frameCount++;
//            	if (frameCount > 100) {
//            		break;
//            	}
//                int currentFrameId = frameIdResultSet.getInt("Id");
//                System.out.println(currentFrameId);
//                Statement numScanStatement = connection.createStatement();
//                ResultSet resultSet = numScanStatement.executeQuery("SELECT NumScans FROM Frames WHERE Id=" + currentFrameId);
//                int numScans = resultSet.getInt("NumScans");
//
//                Statement precursorStatement = connection.createStatement();
////                ResultSet precursorMsInfo = precursorStatement.executeQuery();
//
//
//
//                FrameDataWrapper frameDataWrapper = new FrameDataWrapper();
//                frameDataWrapper.frameId = currentFrameId;
//                frameDataWrapper.frameInformation = frameInformation;
//
//
//                frames.add(frameDataWrapper);
//                bufferedWriter.write("BEGIN IONS\n");
//                bufferedWriter.write("TITLE=" + dateiName + "_FRAMEID="+ currentFrameId + "_NUMSCANS="+ numScans + "\n");
//
//
//                bufferedWriter.write("END IONS\n\n");
//                bufferedWriter.close();
//            }
//
//
//
//
//            bufferedWriter.close();
//
//            System.out.println("test");
//
//        } catch (SQLException e){
//            System.out.println(e.getMessage());
//            throw new RuntimeException("SQLException: " + e.getMessage(), e);
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException("IOException: " + e.getMessage(), e);
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
////            throw e;
//
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
//            }
//        }
//
//    }
//
//
//
//
//
//
//
//
//
//
//
//}

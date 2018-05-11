package dllWrapper;

//import com.sun.jna.Library;


import com.sun.jna.*;
import sqliteReader.SQLiteJDBCDriverConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TimsdataDllWrapper {
    public interface TimsdataDll extends Library {
        TimsdataDll INSTANCE = (TimsdataDll)Native.loadLibrary("C:\\Users\\Anton\\Documents\\sqliteReader\\lib\\timsdata\\timsdata.dll", TimsdataDll.class);

    }

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



//            TimsdataDll sdll = TimsdataDll.INSTANCE;

            System.out.println("test");
        } catch (SQLException e){
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

package sqliteReader;

import java.sql.*;

/**
 *
 * @author sqlitetutorial.net
 */
public class SQLiteJDBCDriverConnection {
    /**
     * Connect to a sample database
     */

    private Connection connection;

    public static final String connectionUrl = "jdbc:sqlite:F:\\proteinProjData\\Ecoli_04_RD2_01_1275.d\\analysis.tdf";


    public Connection getConnection() {
        if (this.connection == null) {
            Connection conn = null;
            String url = connectionUrl;

            try {
                conn = DriverManager.getConnection(url);
                this.connection = conn;
                return this.connection;

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                this.connection = null;
                return  null;
            }
        } else {
            return this.connection;
        }
    }


    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = connectionUrl;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Id, Type  FROM PropertyDefinitions");

            while (rs.next()) {
                int x = rs.getInt("Id");
                int s = rs.getInt("Type");
                System.out.println(x);
            }

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        connect();
//    }
}
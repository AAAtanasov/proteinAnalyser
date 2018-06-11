package de.ovgu.msdatastream.brukerraw.sqllite;

import java.sql.*;

import de.ovgu.msdatastream.Properties;

public class SQLWrapper {

	// fields

	private Connection conn;

	// constructor

	public SQLWrapper() {
		openConnection();
	}

	// connection

	public void openConnection() {
		try {
			if (conn == null || !conn.isClosed()) {
				conn = DriverManager.getConnection(Properties.connectionUrl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeSQL(String statement) {
		try {
			PreparedStatement ps = conn.prepareStatement(statement);
			ResultSet rs = ps.executeQuery();
//			ps.close();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}

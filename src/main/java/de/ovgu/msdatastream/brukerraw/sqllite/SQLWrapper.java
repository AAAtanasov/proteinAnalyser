package de.ovgu.msdatastream.brukerraw.sqllite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ovgu.msdatastream.Properties;

public class SQLWrapper {

	// fields

	public Connection conn;

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
	
}

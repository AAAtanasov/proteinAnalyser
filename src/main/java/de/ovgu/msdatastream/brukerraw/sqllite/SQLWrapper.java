package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLWrapper {

	// fields

	public Connection conn;
	private Properties properties;

	// constructor

	public SQLWrapper(Properties properties) {
		this.properties = properties;
		openConnection();
	}

	// connection

	public void openConnection() {
		try {
			if (conn == null || !conn.isClosed()) {
				conn = DriverManager.getConnection(this.properties.getConnectionUrl());
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

package de.ovgu.msdatastream.brukerraw.sqllite;

import de.ovgu.msdatastream.Properties;
import de.ovgu.msdatastream.ApplicationProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLWrapper {
	public Connection conn;
	private ApplicationProperties applicationProperties;

	public SQLWrapper(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
		openConnection();
	}

	public void openConnection() {
		try {
			if (conn == null || !conn.isClosed()) {
				conn = DriverManager.getConnection(this.applicationProperties.getConnectionUrl());
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

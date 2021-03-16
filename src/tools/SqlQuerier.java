package tools; 

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class SqlQuerier {
	
	private static Logger logger = Logger.getLogger(SqlQuerier.class);
	
	private Connection connection;
	
	public SqlQuerier(Connection connection) {
		this.connection = connection;
	}
	
	public void execute(String queryStr) {
		try {
			Statement statememt = connection.createStatement();
			ResultSet rs = statememt.executeQuery(queryStr);
			begin();
			while (rs.next()) {
				row(rs);
			}
			end();
			rs.close();
			statememt.close();
		} catch (SQLException e) {
			logger.error(e, e);
		}
	}
	
	public static int getFirstId(Connection connection, String queryStr) {
		int id = -1;
		try {
			Statement statememt = connection.createStatement();
			ResultSet rs = statememt.executeQuery(queryStr);
			if (rs.next()) {
				id = rs.getInt("id");
			}
			rs.close();
			statememt.close();
		} catch (SQLException e) {
			logger.error(e, e);
		}
		return id;
	}
	
	public static boolean exist(Connection connection, String queryStr) {
		boolean isRow = false;
		try {
			Statement statememt = connection.createStatement();
			ResultSet rs = statememt.executeQuery(queryStr);
			if (rs.next()) {
				isRow = true;
			}
			rs.close();
			statememt.close();
		} catch (SQLException e) {
			logger.error(e, e);
		}
		return isRow;
	}
	
	public abstract void begin();
	public abstract void end();	
	public abstract void row(ResultSet rs) throws SQLException;

}

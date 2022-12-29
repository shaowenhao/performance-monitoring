package com.siemens.datalayer.iot.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.siemens.datalayer.iot.util.JdbcDatabaseUtil;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;

@Epic("Support SQL query capability in Data Layer")
@Feature("DataLayer as data source")
public class JDBCDataLayerAsDatasourceTests {

	private String datalayerDriver;
	private String datalayerUrl;
	private String datalayerUser;
	private String datalayerPassword;
	private String oracleDriver;
	private String oracleUrl;
	private String oracleUser;
	private String oraclePassword;

	private enum DatasourceType {
		Datalayer, Oracle
	}

	@BeforeClass(description = "Initialize jdbc connection for both datalayer and oracle")
	public void setup() throws ClassNotFoundException {
		Properties datalayerProperties;
		Properties oracleProperties;
		try {
			datalayerProperties = JdbcDatabaseUtil.getConnectionProperties("iot.test.datalayer.db.properties");
		} catch (IOException e) {
			throw new RuntimeException("Failed to get properties of datalayer", e);
		}
		try {
			oracleProperties = JdbcDatabaseUtil.getConnectionProperties("iot.test.datalayer.oracle.db.properties");
		} catch (IOException e) {
			throw new RuntimeException("Failed to get properties of oracle", e);
		}
		datalayerDriver = datalayerProperties.getProperty("driver");
		datalayerUrl = datalayerProperties.getProperty("url");
		datalayerUser = datalayerProperties.getProperty("user");
		datalayerPassword = datalayerProperties.getProperty("password");
		oracleDriver = oracleProperties.getProperty("driver");
		oracleUrl = oracleProperties.getProperty("url");
		oracleUser = oracleProperties.getProperty("user");
		oraclePassword = oracleProperties.getProperty("password");

		Class.forName(datalayerDriver);
		Class.forName(oracleDriver);
	}

	@Test(priority = 0, description = "Send SQL to datalayer and verify the results", dataProvider = "api-engine-test-data-provider", dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send SQL to datalayer through JDBC and verify results")
	@Story("DataLayer as data source")
	public void sendSqlToDatalayerVerifyResults(Map<String, String> paramMaps) throws SQLException {
		String sqlToDatalayer = paramMaps.get("sqlToDatalayer");
		String sqlToOracle = paramMaps.get("sqlToOracle");
		boolean autoVerified = paramMaps.get("autoVerified").trim().equals("1") ? true : false;

		List<List<String>> dataFromDatalayer = getDataFromDatalayer(sqlToDatalayer);
		List<List<String>> dataFromOracle = getDataFromOracle(sqlToOracle);

		if (autoVerified) {
			verifyResults(dataFromDatalayer, dataFromOracle);
		} else {
			manuallyVerify(dataFromDatalayer, dataFromOracle, paramMaps.get("description"));
		}
	}

	@Step("Send SQL to datalayer")
	private List<List<String>> getDataFromDatalayer(String sqlToDatalayer) throws SQLException {
		return getDataFromDatasource(sqlToDatalayer, DatasourceType.Datalayer);
	}

	@Step("Send SQL to oracle")
	private List<List<String>> getDataFromOracle(String sqlToOracle) throws SQLException {
		return getDataFromDatasource(sqlToOracle, DatasourceType.Oracle);
	}

	@Step("Verify results")
	private void verifyResults(List<List<String>> dataFromDatalayer, List<List<String>> dataFromOracle) {
		Assert.assertEquals(dataFromDatalayer, dataFromOracle, "Data is incorrect");
	}

	@Step("Manually verify this case")
	private void manuallyVerify(List<List<String>> dataFromDatalayer, List<List<String>> dataFromOracle,
			String description) {
		
	}

	private List<List<String>> getDataFromDatasource(String sql, DatasourceType datasourceType) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			if (datasourceType == DatasourceType.Datalayer) {
				connection = DriverManager.getConnection(datalayerUrl, datalayerUser, datalayerPassword);
			} else if (datasourceType == DatasourceType.Oracle) {
				connection = DriverManager.getConnection(oracleUrl, oracleUser, oraclePassword);
			} else {
				throw new RuntimeException("Wrong datasource type");
			}
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			List<List<String>> results = transform(resultSet);
			return results;
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
//				ignore
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
//				ignore
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
//				ignore
				}
			}
		}
	}

	private List<List<String>> transform(ResultSet resultSet) throws SQLException {
		List<List<String>> results = new ArrayList<List<String>>();
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();
		while (resultSet.next()) {
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < columnCount; i++) {
				Object o = resultSet.getObject(i + 1);
				row.add((o != null) ? o.toString() : "NULL");
			}
			results.add(row);
		}
		return results;
	}
}

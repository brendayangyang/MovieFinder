package coen280.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by yangyang on 5/26/17.
 */
public class DbUtils
{
    private static Properties properties = loadProperties();

    private static Properties loadProperties()
    {
        Properties prop = new Properties();
        try (InputStream input = DbUtils.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            prop.load(input);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return prop;
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");


        String host = properties.getProperty("db.host");
        String dbName = properties.getProperty("db.name");
        int port = Integer.parseInt(properties.getProperty("db.port"));

        String oracleURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;

        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(oracleURL, username, password);
    }
    public static ResultSet runQuery(Connection conn, String sql) throws SQLException
    {
        Statement s = conn.createStatement();
        s.execute(sql);
        ResultSet res = s.getResultSet();
        return res;
    }

}

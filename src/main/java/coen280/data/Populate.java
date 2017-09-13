package coen280.data;

import coen280.common.DbUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

/**
 *
 */
public class Populate
{
    private Connection connection;


    private String[] tablesToDelete = {"user_taggedmovies_timestamps", "movie_tags", "movie_genres", "movie_director",
            "movie_countries", "movie_actor", "tag", "movie"};

    private void deleteTable(String tableName) throws SQLException
    {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM " + tableName);
    }

    private void deleteAllTables() throws SQLException
    {
        System.out.println("Deleting data from tables...");
        for(int i = 0; i < tablesToDelete.length; i++)
        {
            deleteTable(tablesToDelete[i]);
        }
    }

    private String[] splitLine(String s)
    {
        String[] parts = s.split("\t");
        for(int i = 0; i < parts.length; i++)
        {
            if(parts[i].equals("\\N"))
            {
                parts[i] = null;
            }
        }
        return parts;
    }

    private void insertMovieTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie VALUES(?,?,?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = splitLine(line);
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.setInt(3, Integer.parseInt(parts[5]));
            if(parts[17] != null)
            {
                statement.setBigDecimal(4, new BigDecimal(parts[17]));
            }
            if(parts[18] != null)
            {
                statement.setInt(5, Integer.parseInt(parts[18]));
            }
            statement.executeUpdate();
        }
        br.close();
    }


    private void tag(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting tag table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tag VALUES(?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.executeUpdate();
        }
        br.close();
    }

    private void insertMovieActorTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie_actor table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie_actor VALUES(?,?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.setString(3, parts[2]);
            statement.setInt(4, Integer.parseInt(parts[3]));
            statement.executeUpdate();
        }
        br.close();
    }


    private void insertMovieCountriesTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie_countries table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie_countries VALUES(?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            if (parts.length != 2)
            {
                continue;
            }
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.executeUpdate();

        }
        br.close();
    }

    private void insertMovieDirectorTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie_director table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie_director VALUES(?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.setString(3, parts[2]);
            statement.executeUpdate();
        }
        br.close();
    }

    private void insertMovieGenreTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie_genres table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie_genres VALUES(?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            statement.executeUpdate();
        }
        br.close();
    }




    private void insertMovieTagsTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting movie_tags table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO movie_tags VALUES(?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setInt(2, Integer.parseInt(parts[1]));
            statement.setInt(3, Integer.parseInt(parts[2]));
            statement.executeUpdate();
        }
        br.close();
    }

    private void insertUserTagMvTsTable(String fileName) throws SQLException, IOException
    {
        System.out.println("Inserting user_taggedmovies_timestamps table...");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user_taggedmovies_timestamps VALUES(?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine();
        while((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setInt(2, Integer.parseInt(parts[1]));
            statement.setInt(3, Integer.parseInt(parts[2]));
            statement.executeUpdate();
        }
        br.close();
    }

    public static void main(String[] args) throws Exception
    {
        Populate app = new Populate();

        app.connection = DbUtils.getConnection();

        app.deleteAllTables();

        app.insertMovieTable(args[0]);
        app.tag(args[1]);
        app.insertMovieActorTable(args[2]);
        app.insertMovieCountriesTable(args[3]);
        app.insertMovieDirectorTable(args[4]);
        app.insertMovieGenreTable(args[5]);
        app.insertMovieTagsTable(args[6]);
        app.insertUserTagMvTsTable(args[7]);
        app.connection.close();
    }
}

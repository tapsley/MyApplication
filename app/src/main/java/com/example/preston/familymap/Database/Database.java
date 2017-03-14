package com.example.preston.familymap.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by preston on 2/14/2017.
 */
public class Database {

    Connection connection;

    public UserDAO usersTable = new UserDAO(this);
    public EventDAO eventsTable = new EventDAO(this);
    public PersonDAO personTable = new PersonDAO(this);

    public Database() {
        loadDriver();
    }

    public void loadDriver() {
        try {
            String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() {
        File directory = new File("db");
        if(!directory.exists()) {
            try {
                directory.mkdirs();
            } catch(SecurityException e) {
                e.printStackTrace();
                return;
            }
        }

        String dbName = "db/database.sqlite";
        String connectionURL = "jdbc:sqlite:" + dbName;
        connection = null;

        try {
            connection = DriverManager.getConnection(connectionURL);
            createTables();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    public void startTransaction() {
        openConnection();
        try {
            connection.setAutoCommit(false);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeTransaction(boolean commit) {
        try {
            if(commit) {
                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection = null;
    }

    public void createTables() throws SQLException {
        PreparedStatement users = this.connection.prepareStatement(CREATE_USERS);
        users.executeUpdate();
        PreparedStatement events = this.connection.prepareStatement(CREATE_EVENTS);
        events.executeUpdate();
        PreparedStatement persons = this.connection.prepareStatement(CREATE_PERSONS);
        persons.executeUpdate();
    }

    public void resetDB(boolean dropUsers) throws SQLException {
        if(dropUsers) {
            PreparedStatement dropUserTable = this.connection.prepareStatement(DROP_USERS);
            dropUserTable.executeUpdate();

            PreparedStatement createUserTable = this.connection.prepareStatement(CREATE_USERS);
            createUserTable.executeUpdate();
        }

        PreparedStatement dropPersons = this.connection.prepareStatement(DROP_PERSONS);
        dropPersons.executeUpdate();
        PreparedStatement createPersons = this.connection.prepareStatement(CREATE_PERSONS);
        createPersons.executeUpdate();

        PreparedStatement dropEvents = this.connection.prepareStatement(DROP_EVENTS);
        dropEvents.executeUpdate();
        PreparedStatement createEvents = this.connection.prepareStatement(CREATE_EVENTS);
        createEvents.executeUpdate();
    }


    public void fillReset(String username) {
        try {
            String personTable = "DELETE FROM persons WHERE descendant = ?";
            String eventTable = "DELETE FROM events WHERE descendant = ?";
            PreparedStatement events = this.connection.prepareStatement(eventTable);
            PreparedStatement persons = this.connection.prepareStatement(personTable);

            events.setString(1, username);
            persons.setString(1, username);

            events.executeUpdate();
            persons.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        
    }

    //CREATE STATEMENTS

    public static String CREATE_USERS = "CREATE TABLE IF NOT EXISTS users" +
            "("+
            "username TEXT NOT NULL primary key," +
            "password TEXT NOT NULL," +
            "email TEXT NOT NULL," +
            "firstName TEXT NOT NULL," +
            "lastName TEXT NOT NULL," +
            "token TEXT NOT NULL," +
            "gender TEXT NOT NULL," +
            "personID TEXT NOT NULL" +
            ");";

    public static String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS events" +
            "("+
            "descendant TEXT NOT NULL," +
            "eventID TEXT NOT NULL primary key," +
            "personID TEXT NOT NULL," +
            "latitude real," +
            "longitude real," +
            "country TEXT NOT NULL," +
            "city TEXT NOT NULL," +
            "eventType TEXT NOT NULL," +
            "year TEXT NOT NULL" +
            ");";

    public static String CREATE_PERSONS = "CREATE TABLE IF NOT EXISTS persons" +
            "("+
            "descendant TEXT NOT NULL," +
            "personID TEXT NOT NULL primary key," +
            "firstName TEXT NOT NULL," +
            "lastName TEXT NOT NULL," +
            "gender TEXT NOT NULL," +
            "father TEXT," +
            "mother TEXT," +
            "spouse TEXT" +
            ");";

    //DROP STATEMENTS

    public static String DROP_USERS = "DROP TABLE IF EXISTS users";
    public static String DROP_EVENTS = "DROP TABLE IF EXISTS events";
    public static String DROP_PERSONS = "DROP TABLE IF EXISTS persons";
}

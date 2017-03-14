package com.example.preston.familymap.Database;

import com.example.preston.familymap.Model.IDGenerator;
import com.example.preston.familymap.Model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tyler on 2/14/2017.
 * I will help you access things in the User Table
 */
public class UserDAO {
    private Database db;
    IDGenerator idMaker = IDGenerator.SINGLETON;

    UserDAO(Database db) {
        this.db = db;
    }

    /**
     * This function will insert the user information into the database
     *
     * @param user The User being registered
     * @return boolean True if successful; else false.
     */
    public boolean registerUser(User user) throws SQLException {
        PreparedStatement statement = null;
        String sqlString = "INSERT INTO users " +
                "(userName, password, email, firstName, lastName, token, gender, personID) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        boolean success = false;
        try{
            statement = db.connection.prepareStatement(sqlString);
            statement.setString(1, user.username);
            statement.setString(2, user.password);
            statement.setString(3, user.email);
            statement.setString(4, user.firstName);
            statement.setString(5, user.lastName);
            statement.setString(6, makeToken());
            statement.setString(7, user.gender);
            statement.setString(8, user.personID);

            if(statement.executeUpdate() == 1) {
                success = true;
                updateUserToken(user.username);
            } else {
                throw new SQLException();
            }


        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null)
                statement.close();
        }

        return success;
    }

    /**
     * Retrieves a User from the table based on the given username.
     * @param username String the user we want
     * @return User
     */
    public User getUserByUsername(String username) throws SQLException{
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User tableUser = null;
        try {
            String sql = "SELECT * FROM users WHERE users.username = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                tableUser = new User();
                tableUser.username = resultSet.getString(1);
                tableUser.password = resultSet.getString(2);
                tableUser.email = resultSet.getString(3);
                tableUser.firstName = resultSet.getString(4);
                tableUser.lastName = resultSet.getString(5);
                tableUser.authToken = resultSet.getString(6);
                tableUser.gender = resultSet.getString(7);
                tableUser.personID = resultSet.getString(8);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }

        return tableUser;
    }

    /**
     * Retrieves a User from the database based on the Authentication Token.
     * @param authToken String the authorization token.
     * @return User
     */
    public User getUserByAuthToken(String authToken) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User tableUser = null;
        try {
            String sql = "SELECT * FROM users WHERE users.token = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, authToken);
            resultSet = statement.executeQuery();

            while(resultSet.next()) {
                tableUser = new User();
                tableUser.username = resultSet.getString(1);
                tableUser.password = resultSet.getString(2);
                tableUser.email = resultSet.getString(3);
                tableUser.firstName = resultSet.getString(4);
                tableUser.lastName = resultSet.getString(5);
                tableUser.authToken = resultSet.getString(6);
                tableUser.gender = resultSet.getString(7);
                tableUser.personID = resultSet.getString(8);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
        return tableUser;
    }

    /**
     * This is just going to check the database to see if the username exists already
     * and if then checks to see if the password matches.
     * @param user The User being authenticated
     * @return boolean True is successful; else false.
     */
    public boolean authenticateUser(User user) throws SQLException {
        User theUser = getUserByUsername(user.username);
        if(theUser == null) {
            return false;
        }

        if(user.password.equals(theUser.password)) {
            updateUserToken(user.username);
            return true;
        }
        return false;
    }

    /**
     * This will authenticate the user by checking the database for the authentication
     * token and checking if the user matches.
     * @param auth This object contains the authorization code that we need.
     * @return boolean True if successful; else false.
     */
    public boolean authenticateUser(String auth) {

        try {
            return getUserByAuthToken(auth) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void updateUserToken(String username) throws SQLException {
        PreparedStatement statement = null;
        try {
            String sql = "UPDATE users SET token = ? WHERE users.username = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, makeToken());
            statement.setString(2, username);
            statement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                statement.close();
            }
        }

    }

    private String makeToken() {
        return idMaker.buildToken();
    }
}

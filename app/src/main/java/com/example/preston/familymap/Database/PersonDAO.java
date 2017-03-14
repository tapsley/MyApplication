package com.example.preston.familymap.Database;

import com.example.preston.familymap.Model.IDGenerator;
import com.example.preston.familymap.Model.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyler on 2/14/2017.
 * I will help you access things in the Person Table
 */
public class PersonDAO {
    private Database db;

    public PersonDAO(Database db) {
        this.db = db;
    }
    /**
     * Adds the given Person to the Person Table.
     * @param person A Person object
     */
    public void addPerson(Person person) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "INSERT INTO persons "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        if(person.personID == null) {
            person.personID = IDGenerator.SINGLETON.createPersonID();
        }

        try {
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, person.descendant);
            statement.setString(2, person.personID);
            statement.setString(3, person.firstName);
            statement.setString(4, person.lastName);
            statement.setString(5, person.gender);
            statement.setString(6, person.father);
            statement.setString(7, person.mother);
            statement.setString(8, person.spouse);

            if(statement.executeUpdate() == 1) {

            } else {
                throw new SQLException();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Will get a single Person from the database.
     * @param personID An ID corresponding to a Person in the database.     *
     * @return Person The Person object that is returned.
     */
    public Person getPerson(String personID) throws SQLException{
        if(personID == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Person person = null;
        try {
            String sql = "SELECT * FROM persons WHERE persons.personID = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, personID);
            resultSet = statement.executeQuery();

            while(resultSet.next()) {
                person = new Person();
                person.descendant = resultSet.getString(1);
                person.personID = resultSet.getString(2);
                person.firstName = resultSet.getString(3);
                person.lastName = resultSet.getString(4);
                person.gender = resultSet.getString(5);
                person.father = resultSet.getString(6);
                person.mother = resultSet.getString(7);
                person.spouse = resultSet.getString(8);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                statement.close();
            }
            if(resultSet != null) {
                resultSet.close();
            }
        }

        return person;
    }

    /**
     * Returns all the Person objects related to the user.
     * @param username The username whose family will be returned
     * @return List of Person objects :) The whole family.
     */
    public List<Person> getUserFamily(String username) throws SQLException{
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Person> persons = null;

        try {
            String sql = "SELECT * FROM persons WHERE persons.descendant = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            persons = new ArrayList<>();

            while(resultSet.next()) {
                Person person = new Person();

                person.descendant = resultSet.getString(1);
                person.personID = resultSet.getString(2);
                person.firstName = resultSet.getString(3);
                person.lastName = resultSet.getString(4);
                person.gender = resultSet.getString(5);
                person.father = resultSet.getString(6);
                person.mother = resultSet.getString(7);
                person.spouse = resultSet.getString(8);

                persons.add(person);
            }


        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                statement.close();
            }
            if(resultSet != null) {
                resultSet.close();
            }
        }
        return persons;
    }
}

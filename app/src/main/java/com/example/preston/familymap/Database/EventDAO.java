package com.example.preston.familymap.Database;

import com.example.preston.familymap.Model.Event;
import com.example.preston.familymap.Model.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyler on 2/14/2017.
 * I will help you access things in the Event Table.
 */
public class EventDAO {
    private Database db;


    public EventDAO(Database db) {
        this.db = db;
    }
    /**
     * Adds the given Event to the database.
     * @param event The Event that will be added.
     */
    public void addEvent(Event event) throws SQLException {
        PreparedStatement statement = null;
        String sql = "INSERT INTO events "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, event.descendant);
            statement.setString(2, event.eventID);
            statement.setString(3, event.personID);
            statement.setDouble(4, event.latitude);
            statement.setDouble(5, event.longitude);
            statement.setString(6, event.country);
            statement.setString(7, event.city);
            statement.setString(8, event.eventType);
            statement.setString(9, event.year);

            if(statement.executeUpdate() != 1) {
                throw new SQLException();
            }

        }catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Retrieves an Event object from the database according to the given EventID.
     * @param eventID String of the Event's ID.
     * @return Event The requested Event.
     */
    public Event getEventByID(String eventID) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Event event = null;

        try {
            String sql = "SELECT * FROM events WHERE events.eventid = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, eventID);
            resultSet = statement.executeQuery();

            while(resultSet.next()) {
                event = new Event();
                event.descendant = resultSet.getString(1);
                event.eventID = resultSet.getString(2);
                event.personID = resultSet.getString(3);
                event.latitude = resultSet.getDouble(4);
                event.longitude = resultSet.getDouble(5);
                event.country = resultSet.getString(6);
                event.city = resultSet.getString(7);
                event.eventType = resultSet.getString(8);
                event.year = resultSet.getString(9);
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
        return event;
    }

    /**
     * Retrieves all Event objects associated with a particular Person.
     * @param personID String of the Person's ID.
     * @return List of Events The requested Events.
     */
    public List<Event> getEventByPersonID(String personID) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Event> events = null;
        try {
            String sql = "SELECT * FROM events WHERE events.personid = ?";
            statement = db.connection.prepareStatement(sql);
            statement.setString(1, personID);
            resultSet = statement.executeQuery();
            events = new ArrayList<>();

            while(resultSet.next()) {
                Event event = new Event();
                event.descendant = resultSet.getString(1);
                event.eventID = resultSet.getString(2);
                event.personID = resultSet.getString(3);
                event.latitude = resultSet.getDouble(4);
                event.longitude = resultSet.getDouble(5);
                event.country = resultSet.getString(6);
                event.city = resultSet.getString(7);
                event.eventType = resultSet.getString(8);
                event.year = resultSet.getString(9);

                events.add(event);
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
        return events;
    }

    /**
     * Retrieves all Event objects associated with everyone in
     * the User's family.
     * @param username String the current User's username.
     * @return List of Events The requested Events.
     */
    public List<Event> getAllFamilyEvents(String username) throws SQLException {
        List<Person> persons = db.personTable.getUserFamily(username);
        List<Event> events = new ArrayList<Event>();
        for(Person person : persons) {
            events.addAll(db.eventsTable.getEventByPersonID(person.personID));
        }
        return events;
    }
}

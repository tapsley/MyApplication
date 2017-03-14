package com.example.preston.familymap.Service;

import com.example.preston.familymap.Database.DataImporter;
import com.example.preston.familymap.Database.Database;
import com.example.preston.familymap.Model.Event;
import com.example.preston.familymap.Model.Person;
import com.example.preston.familymap.Model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by preston on 3/7/2017.
 */
public class Facade {
    private static int MAX_GENERATIONS;

    public Facade(int max) {
        MAX_GENERATIONS = max;
    }

    public List<Person> getUserFamily(String username) {
        Database db = new Database();
        List<Person> persons = null;
        db.startTransaction();
        try {
            persons = db.personTable.getUserFamily(username);
            db.closeTransaction(true);
        } catch (SQLException e) {
            db.closeTransaction(false);
            e.printStackTrace();
        }

        return persons;
    }

    public List<Event> getAllFamilyEvents(String username) {
        Database db = new Database();
        List<Event> events = null;
        db.startTransaction();
        try {
            events = db.eventsTable.getAllFamilyEvents(username);
            db.closeTransaction(true);
        } catch(SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return events;
    }

    public List<Event> getEventsByPerson(String personID) {
        Database db = new Database();
        List<Event> events = null;
        db.startTransaction();
        try {
            events = db.eventsTable.getEventByPersonID(personID);
            db.closeTransaction(true);
        } catch (SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return events;
    }

    public Event getEventByID(String eventID, String username) {
        Database db = new Database();
        Event event = null;
        db.startTransaction();
        try {
            event = db.eventsTable.getEventByID(eventID);
            if(event ==  null || !event.descendant.equals(username)) {
                event = null;
            }
            db.closeTransaction(true);
        } catch(SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return event;
    }

    public Person getPerson(String personID, String username) {
        Database db = new Database();
        Person person = null;
        db.startTransaction();
        try {
            person = db.personTable.getPerson(personID);
            if(person == null || !person.descendant.equals(username)) {
                person = null;
            }
            db.closeTransaction(true);
        } catch (SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return person;
    }

    public boolean registerUser(User user) {
        Database db = new Database();
        boolean success = false;
        db.startTransaction();
        try {
            Person person = new Person();
            person.createNewUser(user);
            user.personID = person.personID;

            success = db.usersTable.registerUser(user);
            db.closeTransaction(true);

            success = new DataImporter().runImport(user.username, MAX_GENERATIONS, null).status;
        } catch(SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }
        return success;
    }

    public boolean authenticateUser(User user) {
        Database db = new Database();
        boolean success = false;
        db.startTransaction();
        try {
            success = db.usersTable.authenticateUser(user);
            db.closeTransaction(true);
        } catch (SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return success;
    }

    public User getUserByUsername(String username) {
        Database db = new Database();
        User user = null;
        db.startTransaction();
        try {
            user = db.usersTable.getUserByUsername(username);
            db.closeTransaction(true);
        } catch (SQLException e) {
            e.printStackTrace();
            db.closeTransaction(false);
        }

        return user;
    }

    public boolean authenticateToken(String token) {
        Database db = new Database();
        db.startTransaction();
        boolean result = db.usersTable.authenticateUser(token);
        db.closeTransaction(false);

        return result;
    }

    public User getUserByAuthToken(String token) {
        User user = null;
        try {
            Database db = new Database();
            db.startTransaction();
            if(db.usersTable.authenticateUser(token)) {
                user = db.usersTable.getUserByAuthToken(token);
            }
            db.closeTransaction(false);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<Event> getAllFamilyEventsByAuthToken(String token) {
        if(authenticateToken(token)) {
            Database db = new Database();
            try {
                db.startTransaction();
                User user = db.usersTable.getUserByAuthToken(token);
                if(user != null) {
                    return db.eventsTable.getAllFamilyEvents(user.username);
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
            finally {
                db.closeTransaction(false);
            }
        }
        return null;
    }

    public boolean duplicateNameFound(String username) {
        Database db = new Database();
        try {
            db.startTransaction();
            if (db.usersTable.getUserByUsername(username) == null) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeTransaction(false);
        }

        return true;
    }
}

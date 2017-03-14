package com.example.preston.familymap.Database;

import com.example.preston.familymap.Model.Event;
import com.example.preston.familymap.Model.IDGenerator;
import com.example.preston.familymap.Model.LatLng;
import com.example.preston.familymap.Model.Person;
import com.example.preston.familymap.Model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by preston on 2/27/2017.
 */
public class DataImporter {
    Gson gson = new Gson();
    String username;
    JsonArray femaleNames;
    JsonArray maleNames;
    JsonArray surnames;
    JsonArray locations;
    IDGenerator idMaker = IDGenerator.SINGLETON;
    Random rand = new Random();

    int usersAdded = 0;
    int personsAdded = 0;
    int eventsAdded = 0;

    HashSet<LatLng> locationsUsed = new HashSet<>();
    boolean duplicateLocationsUsed = false;

    Database db = new Database();

    public class ReturnObject {
        public boolean status;
        public String message;

        public ReturnObject(String message, boolean status) {
            this.status = status;
            this.message = message;
        }
    }

    //THIS IS FOR WHEN YOU LOAD AND STUFF
    public ReturnObject runImport(String json) {
        try{
            db.startTransaction();
            db.resetDB(true);
            db.closeTransaction(true);

            db.startTransaction();

            locationsUsed.clear();
            duplicateLocationsUsed = false;
            JsonObject obj = (JsonObject) new JsonParser().parse(json);

            //register a user
            JsonArray userJson = null;
            if(obj.has("users")){
                userJson = obj.getAsJsonArray("users");
            }

            if(userJson == null) {
                db.closeTransaction(false);
            }

            usersAdded = userJson.size();

            for(Object object : userJson) {
                db.usersTable.registerUser(createUser((JsonObject)object));
            }
            //add people to database
            JsonArray people = null;
            if(obj.has("persons")) {
                people = obj.getAsJsonArray("persons");
            }
            personsAdded = people.size();
            for(Object object : people) {
                db.personTable.addPerson(createPerson((JsonObject)object));
            }

            //add events
            JsonArray events = null;
            if(obj.has("events")) {
                events = obj.getAsJsonArray("events");
            }
            eventsAdded = events.size();
            for(Object object : events) {
                db.eventsTable.addEvent(createEvent((JsonObject)object));
            }

            db.closeTransaction(true);
            return new ReturnObject("Successfully added "
                    + String.valueOf(usersAdded) +
                    " users and "
                    + String.valueOf(personsAdded) +
                    " people and " + String.valueOf(eventsAdded) +
                    " events to the database.", true);

        } catch (SQLException e) {
            db.closeTransaction(false);
            return new ReturnObject("Failed", false);
        }
    }

    //THIS IS FOR THE FILL COMMAND
    public ReturnObject runImport(String username, int level, Integer seed) {

        locationsUsed.clear();
        duplicateLocationsUsed = false;
        if(seed == null) {
            rand.setSeed((int) System.nanoTime());
        } else {
            rand.setSeed(seed);
        }
        this.username = username;

        String fnames = "app/data/fnames.json";
        String locations = "app/data/locations.json";
        String mnames = "app/data/mnames.json";
        String snames = "app/data/snames.json";

        femaleNames = readData(fnames);
        maleNames = readData(mnames);
        surnames = readData(snames);
        this.locations = readData(locations);

        if(femaleNames != null && maleNames != null
                && surnames != null && this.locations != null) {
            try {
                db.startTransaction();
                db.fillReset(username);
                db.closeTransaction(true);
                db.startTransaction();

                User user = db.usersTable.getUserByUsername(username);
                if(user == null) {
                    db.closeTransaction(false);
                    return new ReturnObject("The user is not registered", false);
                }
                Person theUser = new Person();
                theUser.createNewUser(user);
                fillUserEvents(theUser, (int)(rand.nextDouble() * 500) + 1500);

                //Recursive function to create the rest of the stuff
                fillUserFamily(theUser, level);

                db.closeTransaction(true);
                return new ReturnObject("Successfully added " + String.valueOf(personsAdded) +
                        " persons and " + String.valueOf(eventsAdded) +
                        " events to the database.", true);
            } catch(Exception e) {
                e.printStackTrace();
                db.closeTransaction(false);
                return new ReturnObject("An error occurred while loading the Database.", false);
            }
        }
        return new ReturnObject("Could not import. At least one of the files was bad", false);
    }

    private void fillUserEvents(Person user, int birth)  throws SQLException {
        int birthYear = ((int)(rand.nextDouble() * 7) + birth);
        int deathYear = birthYear + (int)(rand.nextDouble() * 80) + 20;
        int christening = birthYear + 1;
        int baptism = birthYear + 8;
        int census = birth + 10;

        makeEvent(user, "birth", birthYear);
        makeEvent(user, "death", deathYear);
        makeEvent(user, "christening", christening);
        makeEvent(user, "baptism", baptism);
        makeEvent(user, "census", census);
    }

    private Event makeEvent(Person person, String type, int year) throws SQLException {
        Event event = new Event();
        event.descendant = username;
        event.eventID = idMaker.createEventID();

        event.personID = person.personID;

        JsonObject location = getLocation();

        event.longitude = location.get("longitude").getAsDouble();
        event.latitude = location.get("latitude").getAsDouble();
        event.country = location.get("country").getAsString();
        event.city = location.get("city").getAsString();
        event.eventType = type;
        event.year = String.valueOf(year);
        db.eventsTable.addEvent(event);
        eventsAdded++;

        return event;
    }

    private Person fillUserFamily(Person child, int levelsToGo) throws SQLException {
        if(levelsToGo <= 0) {
            db.personTable.addPerson(child);
            personsAdded++;
            return null;
        }

        levelsToGo--;

        int birthYear = 1960;
        Person father = fillPerson(true, birthYear);
        Person mother = fillPerson(false, birthYear);

        marry(father, mother);
        child.father = father.personID;
        child.mother = mother.personID;
        father = fillUserFamily(father, levelsToGo);
        mother = fillUserFamily(mother, levelsToGo);

        db.personTable.addPerson(child);
        personsAdded++;
        return child;
    }

    private Person fillPerson(boolean male, int birthYear) throws SQLException {
        Person person = new Person();
        person.descendant = username;
        person.personID = IDGenerator.SINGLETON.createPersonID();
        Random rand = new Random();

        if(male) {
            person.firstName = (maleNames.get((int)(rand.nextDouble() * maleNames.size())).getAsString());
            person.gender = "m";
        } else {
            person.firstName = (femaleNames.get((int)(rand.nextDouble() * femaleNames.size())).getAsString());
            person.gender = "f";
        }

        person.lastName = (surnames.get((int)(rand.nextDouble() * surnames.size())).getAsString());
        fillUserEvents(person, birthYear);

        return person;
    }

    private void marry(Person father, Person mother) throws SQLException {
        List<Event> events = db.eventsTable.getEventByPersonID(father.personID);
        if(events != null && events.size() > 0) {
            int marriageYear = 1980;
            for(Event event : events) {
                if(event.eventType.contains("birth")) {
                    marriageYear = Integer.parseInt(event.year) + 20;
                }
            }
            Event marriage = makeEvent(father, "marriage", marriageYear);

            marriage.personID = mother.personID;
            marriage.eventID = IDGenerator.SINGLETON.createEventID();
            db.eventsTable.addEvent(marriage);
            eventsAdded++;

            father.spouse = mother.personID;
            mother.spouse = father.personID;
        }
    }

    private JsonArray readData(String names) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(names));
            BufferedReader br = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            try {
                JsonObject namesJson = gson.fromJson(sb.toString(), JsonObject.class);

                return namesJson.getAsJsonArray("data");
            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject getLocation() {
        JsonObject location;
        Random rand = new Random();
        LatLng latLng = new LatLng();
        location = locations.get((int)(rand.nextDouble() * locations.size())).getAsJsonObject();
        latLng.lat = location.get("latitude").getAsDouble();
        latLng.lng = location.get("longitude").getAsDouble();

        locationsUsed.add(latLng);
        return location;
    }

    //These methods parse the Json to create new objects.......
    private User createUser(JsonObject obj) {
        User user = new User();
        user.email = obj.get("email").getAsString();
        user.firstName = obj.get("firstName").getAsString();
        user.lastName = obj.get("lastName").getAsString();
        user.gender = obj.get("gender").getAsString();
        user.username = obj.get("userName").getAsString();
        user.password = obj.get("password").getAsString();
        user.personID = obj.get("personID").getAsString();
        return user;
    }

    private Person createPerson(JsonObject obj) {
        Person person = new Person();
        person.firstName = obj.get("firstName").getAsString();
        person.lastName = obj.get("lastName").getAsString();
        person.gender = obj.get("gender").getAsString();
        person.personID = obj.get("personID").getAsString();

        person.spouse = obj.has("spouse") ? obj.get("spouse").getAsString() : null;
        person.father = obj.has("father") ? obj.get("father").getAsString() : null;
        person.mother = obj.has("mother") ? obj.get("mother").getAsString() : null;

        person.descendant = obj.get("descendant").getAsString();
        return person;
    }

    private Event createEvent(JsonObject obj) {

        Event event = new Event();
        event.personID = obj.get("personID").getAsString();
        event.city = obj.get("city").getAsString();
        event.country = obj.get("country").getAsString();
        event.latitude = obj.get("latitude").getAsDouble();
        event.longitude = obj.get("longitude").getAsDouble();
        event.year = obj.get("year").getAsString();
        event.eventType = obj.get("eventType").getAsString();
        event.descendant = obj.get("descendant").getAsString();

        event.eventID = obj.get("eventID").getAsString();

        return event;
    }

}

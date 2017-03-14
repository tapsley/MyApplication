package com.example.preston.familymap.Server;

import com.example.preston.familymap.Model.AuthToken;
import com.example.preston.familymap.Model.Event;
import com.example.preston.familymap.Model.Person;
import com.example.preston.familymap.Model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tyler on 2/14/2017.
 * This class represents the Server to the Client.
 * It has the same methods as the Web API.
 */
public class ServerProxy {

    public static void main(String[] args) {
        String serverHost = args[0];
        String serverPort = args[1];

        connectToServer(serverHost, serverPort);
    }

    private static void connectToServer(String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            //http.setDoOutput(false);

            //http.addRequestProperty("Authorization")

            http.connect();

            User hi = new User();
            register(hi);

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                System.out.println(respData);
            } else {
                System.out.println("ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }



    /**
     * Creates a new user account, generates 4 generations of ancestor data
     * for the new user, logs the user in, and returns an auth token.
     * @param user Contains the information of the user to be registered.
     * @return AuthToken The Authorization Token so the user can log in.
     */
    public static AuthToken register(User user) {



        return null;
    }

    /**
     * Logs in the user and returns an auth token.
     * @param name Name of the user.
     * @param password The users password.
     * @return AuthToken
     */
    public AuthToken login(String name, String password) {
        return null;
    }

    /**
     *  Deletes ALL data from the database, including user accounts,
     *  auth tokens, and generated person and event data.
     */
    public void clear() {

    }

    /**
     * Populates the server's database with generated data for the specified user name.
     * @param username Must already exist.
     * @param generations Optional. Lets the caller specify the number of
     * generations of ancestors to be generated. Must be non-negative.
     */
    public void fill(String username, int generations) {

    }

    /**
     *  Clears all data from the database (just like the /clear API),
     *  and then loads the posted user, person, and event data into the database.
     * @param users Array of User objects, same format as register.
     * @param persons Array of Person objects, same format as register.
     * @param events Array of Event objects, same format as register.
     */
    public void load(User[] users, Person[] persons, Event[] events) {

    }

    /**
     * Returns the Person object with the specified ID.
     * @param personID ID that corresponds with a Person in the Person table.
     * @return Person The Person that was requested.
     */
    public Person person(String personID) {
        return null;
    }

    /**
     * Returns all family members of the current user. User is determined from the
     * provided Auth Token.
     * @return Person[] An array of all the Person objects.
     */
    public Person[] person() {
        return null;
    }

    /**
     * Returns the Event objects with the specified ID.
     * @param eventID Event ID
     * @return Event The Event that was requested.
     */
    public Event event(String eventID) {
        return null;
    }

    /**
     * Returns all Events for all family members of the current user.
     * AuthToken required.
     * @return Event[] Array of all the Event objects.
     */
    public Event[] event() {
        return null;
    }


}

package com.example.preston.familymap.Model;

import java.util.Random;

/**
 * Created by preston on 3/13/2017.
 */
public class IDGenerator {
    public static IDGenerator SINGLETON = new IDGenerator();

    public int personID;
    public int eventID;
    public int auth;
    public static int eventIDLength = 8;
    public static int tokenLength = 3;
    public static final String options = "abcdefghijklmnopqrstuvwxyz012345678901234567890123456789";
    public static final char[] symbols = options.toCharArray();
    Random rand = new Random();

    private IDGenerator() {
        personID = 0;
        eventID = 0;
        auth = 0;
        rand = new Random();
    }

    public String createPersonID() {
        return buildID();
    }

    public String createEventID() {
        return buildID();
    }

    public String buildID() {
        StringBuilder id = new StringBuilder();
        for(int i = 0; i < eventIDLength; i++) {
            id.append(symbols[rand.nextInt(symbols.length)]);
        }
        return id.toString();
    }

    public String buildToken() {
        StringBuilder token = new StringBuilder();
        for(int i = 0; i < tokenLength; i++) {
            token.append(symbols[rand.nextInt(symbols.length)]);
        }
        return token.toString();
    }
}

package com.example.preston.familymap.Model;

/**
 * Created by tyler on 2/14/2017.
 * A Person object, generally a component of a larger family, and has associated events.
 */
public class Person {
    public String personID;
    public String descendant;
    public String firstName;
    public String lastName;
    public String gender;
    public String father; //personID of father
    public String mother; //personID of mother
    public String spouse; //personID of spouse

    public void createNewUser(User user) {
        descendant = user.username;
        personID = user.personID == null ? IDGenerator.SINGLETON.createPersonID() : user.personID;
        firstName = user.firstName;
        lastName = user.lastName;
        gender = user.gender;
    }
}

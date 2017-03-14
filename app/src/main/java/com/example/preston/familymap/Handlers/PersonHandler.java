package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Model.Person;
import com.example.preston.familymap.Model.User;
import com.example.preston.familymap.Service.Facade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by preston on 2/14/2017.
 */
public class PersonHandler implements HttpHandler {

    private Facade facade;
    public static int MAX_GENERATIONS = 4;
    private Gson gson = new Gson();

    public PersonHandler() {
        facade = new Facade(MAX_GENERATIONS);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String command = exchange.getRequestURI().toString();

        String[] params = command.split("/");
        String token = exchange.getRequestHeaders().getFirst("Authorization");

        if(token == null) {
            String resp = "No Authorization token found.";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(resp, respBody);
            respBody.close();
        } else if(facade.authenticateToken(token)) {
            User user = facade.getUserByAuthToken(token);
            //check parameters to know which "person" to call
            if(params.length <= 2) //get all people
            {
                List<Person> persons = facade.getUserFamily(user.username);
                if(persons != null) {
                    JsonObject json = new JsonObject();
                    json.add("data", gson.toJsonTree(persons));
                    //
                    Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                    String resp = gson_two.toJson(json);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                } else {
                    String resp = "Error retrieving family";
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                }
            } else if (params.length == 3) {
                Person person = facade.getPerson(params[2], user.username);
                if(person == null) {
                    //Person doesn't exist
                    String resp = "There is no person with that ID in the user's family.";
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                } else {
                    //Person does exist
                    Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                    String resp = gson_two.toJson(person);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                }
            } else {
                //bad call
                String resp = "Badly formed URI.";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                writeString(resp, respBody);
                respBody.close();
            }
        } else {
            //bad token
            String resp = "Authorization token is not right.";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(resp, respBody);
            respBody.close();
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}

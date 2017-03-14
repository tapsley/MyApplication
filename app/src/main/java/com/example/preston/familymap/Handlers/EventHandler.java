package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Model.Event;
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
public class EventHandler implements HttpHandler {


    private Facade facade;
    public static int MAX_GENERATIONS = 4;
    private Gson gson = new Gson();

    public EventHandler() {
        facade = new Facade(MAX_GENERATIONS);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String command = exchange.getRequestURI().toString();

        String[] params = command.split("/");

        if(params.length < 2) {
            String resp = "Not enough information";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(resp, respBody);
            respBody.close();
        } else {
            String token = exchange.getRequestHeaders().getFirst("Authorization");

            if(token == null) {
                //Bad token
                String resp = "No Authorization token found.";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                writeString(resp, respBody);
                respBody.close();
            } if(facade.authenticateToken(token)) {
                User user = facade.getUserByAuthToken(token);
                if(params.length == 3) {
                    Event event = facade.getEventByID(params[2], user.username);
                    if(event == null) {
                        //Event not found
                        String resp = "No event found in the users family with that ID.";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    } else {
                        //Event found
                        Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                        String resp = gson_two.toJson(event);
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    }
                } else if (params.length > 3) {
                    //bad call
                    String resp = "Badly formed URI.";
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                } else {
                    List<Event> events = facade.getAllFamilyEventsByAuthToken(token);
                    if(events == null) {
                        String resp = "Error getting events.";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    } else {
                        JsonObject json = new JsonObject();
                        json.add("data", gson.toJsonTree(events));
                        Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                        String resp = gson_two.toJson(json);
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    }
                }
            }
            else {
                //bad token
                String resp = "Authorization token is not right.";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                writeString(resp, respBody);
                respBody.close();
            }
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
};

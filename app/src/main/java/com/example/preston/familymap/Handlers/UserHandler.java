package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Model.AuthToken;
import com.example.preston.familymap.Model.User;
import com.example.preston.familymap.Service.Facade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/**
 * Created by preston on 3/14/2017.
 */
public class UserHandler implements HttpHandler {
    private Gson gson = new Gson();
    private Facade facade;
    private static int MAX_GENERATIONS = 4;

    public UserHandler() {
        facade = new Facade(MAX_GENERATIONS);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String command = exchange.getRequestURI().toString();
        String[] commandParts = command.split("/");

        if(commandParts.length < 3) {
            String resp = "Bad URI call.";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(resp, respBody);
            respBody.close();
            return;
        } else {
            if(commandParts[2].equals("login")) {
                try {
                    InputStream body = exchange.getRequestBody();
                    String bodyParts = streamToString(body);

                    JsonObject json = gson.fromJson(bodyParts, JsonObject.class);

                    User user = new User();

                    user.username = json.get("userName").getAsString();
                    user.password = json.get("password").getAsString();

                    if(facade.authenticateUser(user)) {
                        user = facade.getUserByUsername(user.username);
                        AuthToken token = new AuthToken();
                        token.userName = user.username;
                        token.authorizationToken = user.authToken;
                        token.personId = user.personID;

                        Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                        String resp = gson_two.toJson(token);
                        //
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();

                    } else {
                        //username or password is wrong
                        String resp = "The username or password was incorrect";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            } else if(commandParts[2].equals("register")) {
                try {
                    InputStream body = exchange.getRequestBody();
                    String bodyParts = streamToString(body);

                    JsonObject json = gson.fromJson(bodyParts, JsonObject.class);

                    User user = new User();

                    user.username = json.get("userName").getAsString();
                    user.password = json.get("password").getAsString();
                    user.email = json.get("email").getAsString();
                    user.firstName = json.get("firstName").getAsString();
                    user.lastName = json.get("lastName").getAsString();
                    user.gender = json.get("gender").getAsString();

                    if(facade.duplicateNameFound(user.username)) {
                        String resp = "That username already exists";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    } else if(facade.registerUser(user)) {
                        user = facade.getUserByUsername(user.username);
                        AuthToken token = new AuthToken();
                        token.userName = user.username;
                        token.authorizationToken = user.authToken;
                        token.personId = user.personID;
                        //
                        Gson gson_two = new GsonBuilder().disableHtmlEscaping().create();
                        String resp = gson_two.toJson(token);

                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    } else {
                        String resp = "Registration failed";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(resp, respBody);
                        respBody.close();
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            out.append(line);
        }
        br.close();
        return out.toString();
    }

    private String readString(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buf = new char[1024];
        int length;
        while ((length = streamReader.read(buf)) > 0) {
            sb.append(buf, 0, length);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}

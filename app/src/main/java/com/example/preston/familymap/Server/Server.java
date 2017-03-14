package com.example.preston.familymap.Server;

import com.example.preston.familymap.Handlers.ClearHandler;
import com.example.preston.familymap.Handlers.EventHandler;
import com.example.preston.familymap.Handlers.FillHandler;
import com.example.preston.familymap.Handlers.IndexHandler;
import com.example.preston.familymap.Handlers.LoadHandler;
import com.example.preston.familymap.Handlers.PersonHandler;
import com.example.preston.familymap.Handlers.UserHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by preston on 2/14/2017.
 */
public class Server {
    public static int SERVER_PORT_NUMBER = 8080;
    private static int MAX_WAITING_CONNECTIONS = 20;

    private HttpServer server;
    public static void main(String[] args) {
        //Start listening
        new Server().run();
    }

    private void run() {
        try{
            server = HttpServer.create(new InetSocketAddress(SERVER_PORT_NUMBER),
                    MAX_WAITING_CONNECTIONS);
            System.out.println("The server is running");
        } catch(IOException e) {
            System.out.println("There was an error: " + e.getMessage());
        }

        server.setExecutor(null);

        server.createContext("/clear", clearHandler);
        server.createContext("/fill", fillHandler);
        server.createContext("/person", personHandler);
        server.createContext("/event", eventHandler);
        server.createContext("/user", userHandler);
        server.createContext("/load", loadHandler);
        server.createContext("/close", closeHandler);

        server.createContext("/", indexHandler);

        System.out.println("Server started on port:" + SERVER_PORT_NUMBER);
        server.start();
    }

    //ALL MY HANDLERS

    private HttpHandler userHandler = new UserHandler();

    private HttpHandler indexHandler = new IndexHandler();

    private HttpHandler clearHandler = new ClearHandler();

    private HttpHandler loadHandler = new LoadHandler();

    private HttpHandler fillHandler = new FillHandler();

    private HttpHandler personHandler = new PersonHandler();

    private HttpHandler eventHandler = new EventHandler();

    private HttpHandler closeHandler = new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Runtime.getRuntime().exit(0);
        }
    };
}

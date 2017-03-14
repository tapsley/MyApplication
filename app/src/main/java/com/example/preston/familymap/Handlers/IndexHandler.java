package com.example.preston.familymap.Handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Scanner;

/**
 * Created by preston on 3/14/2017.
 */
public class IndexHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Headers head = exchange.getResponseHeaders();
        URI command = exchange.getRequestURI();
        String theCommand = command.toString();

        String[] params = theCommand.split("/", 2);

        String path = null;

        if(params.length <= 1 || params[1].equals("")) {
            path = "index.html";
            head.set("Content-Type", "text/html");
        } else {
            path = params[1];
            if(theCommand.split("/")[1].equals("css")) {
                head.set("Content-Type", "text/css");
            } else if(theCommand.split("/")[1].equals("img")) {
                head.set("Content-Type", "image/png");
            } else {
                head.set("Content-Type", "text/html");
            }
        }
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

        OutputStreamWriter sendBack = new OutputStreamWriter(exchange.getResponseBody());

        String file = "app/HTML/" + path;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(file));

        } catch(IOException e) {
            System.out.println("File not found");
            String notFound = "HTML/404.html";
            scanner = new Scanner(new FileReader(notFound));
        }

        StringBuilder stringBuilder = new StringBuilder();
        while(scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine() + "\n");
        }

        scanner.close();
        sendBack.write(stringBuilder.toString());
        sendBack.close();

    }
}

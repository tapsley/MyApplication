package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Database.DataImporter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/**
 * Created by preston on 2/14/2017.
 */
public class FillHandler implements HttpHandler {
    public static final int MAX_GENERATIONS = 4;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String command = exchange.getRequestURI().toString();

        String[] params = command.split("/");

        int gens = MAX_GENERATIONS;

        if(params.length <= 2) {
            String resp = "Please specify a user.";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(resp, respBody);
            respBody.close();
            return;
        }

        String username = params[2].split("\\?")[0];

        if(params.length == 4) {
            try {
                gens = Integer.parseInt(params[3]);
                gens = Math.abs(gens);
                if(gens > MAX_GENERATIONS) {
                    String resp = "That's too many generations. The maximum is " + String.valueOf(MAX_GENERATIONS) + ".";
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    writeString(resp, respBody);
                    respBody.close();
                }
            } catch (NumberFormatException e) {
                String resp = "The number of generations is either too big or not a number.";
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBody = exchange.getResponseBody();
                writeString(resp, respBody);
                respBody.close();
                e.printStackTrace();
                return;
            }
        }

        String report = new DataImporter().runImport(username, gens, 1).message;
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString(report, respBody);
        respBody.close();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}

package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Database.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.sql.SQLException;

/**
 * Created by preston on 2/14/2017.
 */
public class ClearHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Database db = new Database();
        try {
            db.startTransaction();
            db.resetDB(true);
            db.closeTransaction(true);
        }catch(SQLException e){
            e.printStackTrace();
            db.closeTransaction(false);
        }
        String resp = "The Database was cleared";

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream respBody = exchange.getResponseBody();
        writeString(resp, respBody);
        respBody.close();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}

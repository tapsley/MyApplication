package com.example.preston.familymap.Handlers;

import com.example.preston.familymap.Database.DataImporter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

/**
 * Created by preston on 2/14/2017.
 */
public class LoadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream reqBody = exchange.getRequestBody();
        String reqData = readString(reqBody);

        try {
            String report = new DataImporter().runImport(reqData).message;
            //Report
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(report, respBody);
            respBody.close();
        } catch(IOException e) {
            String report = "There was a problem in loading";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = exchange.getResponseBody();
            writeString(report, respBody);
            respBody.close();
            e.printStackTrace();
        }
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

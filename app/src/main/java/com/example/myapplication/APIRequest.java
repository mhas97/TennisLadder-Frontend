package com.example.myapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to make API requests via the HTTPURLConnection class.
 * 
 * The structure and methods found in this class are discussed in the following tutorials:
 * https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/#Android-MySQL-Tutorial]
 * https://developer.android.com/reference/java/net/HttpURLConnection
 *
 * HTTPURLConnection as described in the Android documentation should follow a basic pattern:
 * - Open the connection
 * - Prepare the request
 * - Upload a request body if needed
 * - Read the response
 * - Close the connection
 */
public class APIRequest {

    /**
     * Execute a post request via HTTPURLConnection. Use a string builder to format the response.
     * @param API_URL The request URL.
     * @param params Parameters required by the API.
     * @return The formatted response.
     */
    public String executePostRequest(String API_URL, HashMap<String, String> params) {
        URL url;
        StringBuilder responseBuilder = new StringBuilder();
        try {
            /* Open the connection, set a 20 second timeout. */
            url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            /* Create an output stream for preparing the request and format the post data. */
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(formatPostData(params));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            /* Get the response. */
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response;
                while ((response = bufferedReader.readLine()) != null) {
                    responseBuilder.append(response);
                }
            }
            /* Disconnect. */
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }

    /**
     * Use a string builder and URL encoder to produce a formatted string for a post request.
     * @param params Parameters required by the API.
     * @return The URL encoded string.
     */
    private String formatPostData(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder formattedParams = new StringBuilder();
        /* Encode each parameter */
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formattedParams.append("&");
            formattedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            formattedParams.append("=");
            formattedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return formattedParams.toString();
    }

    /**
     * Execute a get request via HTTPURLConnection. Use a string builder to format the response.
     * @param API_URL The request URL.
     * @return The formatted response.
     */
    public String executeGetRequest(String API_URL) {
        StringBuilder responseBuilder = new StringBuilder();
        try {
            /* Open the connection, set a 20 second timeout. */
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);

            /* Get the response. */
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;
            while ((response = br.readLine()) != null) {
                responseBuilder.append(response).append("\n");
            }
            /* Disconnect. */
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }
}

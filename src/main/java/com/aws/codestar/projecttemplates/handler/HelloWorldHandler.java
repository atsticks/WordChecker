package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.aws.codestar.projecttemplates.GatewayResponse;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Handler for requests to Lambda function.
 */
public class HelloWorldHandler implements RequestHandler<Object, Object> {

    private static final String TARGET_METHOD_GET = "GET";
    private static final String TARGET_METHOD_POST = "POST";
    private static final String QUERY_PARAMS = "queryStringParameters";
    private static final String QUERY_METHOD = "httpMethod";
    private static final String HEADERS = "headers";
    private static final String BODY = "body";
    private static final String TARGET_URL = "url";


    public Object handleRequest(final Object input, final Context context) {
        JSONObject inputObject = new JSONObject(input.toString());

        JSONObject headers = inputObject.getJSONObject(HEADERS);
        switch(inputObject.getString(QUERY_METHOD)){
            case TARGET_METHOD_GET:
                JSONObject queryParams = inputObject.getJSONObject(QUERY_PARAMS);
                return handleGet(headers, queryParams.getString("url"));
            case TARGET_METHOD_POST:
                String body = inputObject.get("body").toString();
                return handlePost(headers, body);
            default:
                return new GatewayResponse(
                        new JSONObject()
                                .put("error", "Unsupported method, use GET, POST.")
                                .toString(),
                        Collections.emptyMap(),
                        HttpStatus.METHOD_NOT_ALLOWED.value());
        }
    }

    private GatewayResponse handlePost(JSONObject headers, String source) {
        try{
            return countWords(source, headers.optString(HttpHeaders.CONTENT_LOCATION, "N/A"),
                    headers.optString(HttpHeaders.CONTENT_ENCODING, "N/A"),
                    headers.optString(HttpHeaders.CONTENT_TYPE, "N/A"));
        } catch(Exception e){
            return new GatewayResponse(
                    new JSONObject()
                            .put("error", e.getMessage())
                            .toString(),
                    Collections.emptyMap(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private GatewayResponse handleGet(JSONObject headerObject, String urlString) {
        try{
            URL url = new URL(urlString);
            String source = "";

            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType();
            String encoding = connection.getContentEncoding();
            if(encoding==null){
                encoding = "UTF-8";
            }
            InputStream is = connection.getInputStream();
            byte[] bytes = new byte[256];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int read = is.read(bytes);
            while(read>0){
                bos.write(bytes, 0, read);
                read = is.read(bytes);
            }
            source = bos.toString(encoding);
            return countWords(source, urlString, encoding, contentType);
        } catch (MalformedURLException e) {
            return new GatewayResponse(
                    new JSONObject()
                            .put("error", "Malformed url: " + urlString)
                            .toString(),
                    Collections.emptyMap(),
                    HttpStatus.NOT_FOUND.value());
        } catch(Exception e){
            return new GatewayResponse(
                    new JSONObject()
                            .put("error", e.getMessage())
                            .toString(),
                    Collections.emptyMap(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private GatewayResponse countWords(String source, @Nullable String urlString, @Nullable String encoding, @Nullable String contentType) {
        int inputLength = source.length();
        source = source.trim();
        int totalLength = 0;
        int wordCount = 0;
        int differentWordCount = 0;
        String minWord = "";
        String maxWord = "";
        double avgLength = 0;
        if(!source.isEmpty()){
            StringTokenizer tokenizer = new StringTokenizer(source, " \t\n\r.,:;<>\\/+*%&()=?![]{}", false);
            Set<String> differentWords = new TreeSet<>();
            while (tokenizer.hasMoreTokens()){
                String w = tokenizer.nextToken();
                wordCount++;
                totalLength += w.length();
                differentWords.add(w);
                if(minWord.length()==0 || minWord.length()>w.length()){
                    minWord = w;
                }
                if(maxWord.length() < w.length()){
                    maxWord = w;
                }
            }
            differentWordCount = differentWords.size();
            avgLength = ((double)totalLength) / wordCount;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(
                new JSONObject()
                        .put("url", urlString)
                        .put("content-type", contentType==null?"N/A":contentType)
                        .put("content-encoding", encoding==null?"N/A":encoding)
                        .put("content-length", inputLength)
                        .put("trimmed-length", source.length())
                        .put("total-word-chars", totalLength)
                        .put("total-word-count", wordCount)
                        .put("different-word-count", differentWordCount)
                        .put("min-word", minWord)
                        .put("max-word", maxWord)
                        .put("average-word-length", avgLength)
                        .toString(),
                headers,
                HttpStatus.OK.value());
    }
}

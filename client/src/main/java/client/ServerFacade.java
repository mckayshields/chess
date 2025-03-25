package client;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

public class ServerFacade {
    private final String serverURL;
    public ServerFacade(String url){
        this.serverURL = url;
    }


    private<T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException{
        try{
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
        catch(ResponseException ex){
            throw ex;
        }
        catch(Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException{
        if(request != null){
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try(OutputStream reqBody = http.getOutputStream()){
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if(http.getContentLength() < 0){
            try(InputStream respBody = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(respBody);
                if(responseClass != null){
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException{
        var status = http.getResponseCode();
        if(status / 100 != 2){
            try(InputStream respErr = http.getErrorStream()){
                if (respErr != null){
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }
}

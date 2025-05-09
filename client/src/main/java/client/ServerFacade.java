package client;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

public class ServerFacade {
    private final String serverURL;
    public ServerFacade(String url){
        this.serverURL = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException{
            UserData user = new UserData(username, password, email);
            var path = "/user";
            return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws ResponseException{
        UserData user = new UserData(username, password, null);
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);

    }

    public void logout(String authToken) throws ResponseException{
            var path = "/session";
            this.makeRequest("DELETE", path, null, null, authToken);

    }

    public CreateResponse createGame(String gameName, String authToken)  throws ResponseException{
            var path = "/game";
            CreateRequest request = new CreateRequest(authToken, gameName);
            return this.makeRequest("POST", path, request, CreateResponse.class, authToken);

    }

    public ListResponse listGames(String authToken) throws ResponseException{
            var path = "/game";
            return this.makeRequest("GET", path, null, ListResponse.class, authToken);
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ResponseException{
            var path = "/game";
            JoinRequest request;
            if(playerColor.equals("BLACK")){
                request = new JoinRequest(authToken, ChessGame.TeamColor.BLACK, gameID);
            }
            else{
                request = new JoinRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
            }
            this.makeRequest("PUT", path, request, null, authToken);
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, null);
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

package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.Map;

public class GameHandler {
    private final GameService service;
    public GameHandler(GameService service) {
        this.service = service;
    }

    public Object createHandler(Request req, Response res) throws ResponseException {
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        String modifiedObjects = jsonObject.toString();
        var newRequest = new Gson().toJson(modifiedObjects, CreateRequest.class);
        CreateRequest createRequest = new Gson().fromJson(newRequest, CreateRequest.class);
        try {
            CreateResponse createResponse = service.createGame(createRequest);
            return new Gson().toJson(createResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    public Object joinHandler(Request req, Response res) throws ResponseException{
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        String modifiedObjects = jsonObject.toString();
        var newRequest = new Gson().toJson(modifiedObjects, JoinRequest.class);
        JoinRequest joinRequest = new Gson().fromJson(newRequest, JoinRequest.class);
        try {
            service.join(joinRequest);
            return new Gson().toJson(null);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    public Object listHandler(Request req, Response res) throws ResponseException{
        ListRequest listRequest = new ListRequest(req.headers("authorization"));
        try{
            ListResponse listResponse = service.list(listRequest);
            return new Gson().toJson(Map.of("games", listResponse));
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }
}

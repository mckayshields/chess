package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {
    private final GameService service;
    public GameHandler(GameService service) {
        this.service = service;
    }

    public Object createHandler(Request req, Response res) throws ResponseException {
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        CreateRequest createRequest = new Gson().fromJson(jsonObject, CreateRequest.class);
        try {
            CreateResponse createResponse = service.createGame(createRequest);
            return new Gson().toJson(createResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.getStatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    public Object joinHandler(Request req, Response res) throws ResponseException{
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        JoinRequest joinRequest = new Gson().fromJson(jsonObject, JoinRequest.class);
        try {
            service.join(joinRequest);
            return new Gson().toJson(null);
        }
        catch(ResponseException e) {
            int statusCode = e.getStatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    public Object listHandler(Request req, Response res) throws ResponseException{
        ListRequest listRequest = new ListRequest(req.headers("authorization"));
        try{
            ListResponse listResponse = service.list(listRequest);
            return new Gson().toJson(listResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.getStatusCode();
            res.status(statusCode);
            throw e;
        }
    }
}

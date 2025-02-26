package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

public class GameHandler {
    private final GameService service;
    public GameHandler(GameService service) {
        this.service = service;
    }

    public Object createHandler(Request req, Response res) throws ResponseException {
        try {
            CreateResponse createResponse = service.create(req.body());
            return new Gson().toJson(createResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    public Object joinHandler(Request req, Response res) throws ResponseException{
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
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

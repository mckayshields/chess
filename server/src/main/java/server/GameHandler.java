package server;

import com.google.gson.Gson;
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

    private Object createHandler(Request req, Response res) throws ResponseException {
        var createRequest = new Gson().fromJson(req.body(), CreateRequest.class);
        try {
            CreateResponse createResponse = service.create(createRequest);
            return new Gson().toJson(createResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    private Object joinHandler(Request req, Response res) throws ResponseException{
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
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

    private Object listHandler(Request req, Response res) throws ResponseException{
        var listRequest = new Gson().fromJson(req.body(), ListRequest.class);
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

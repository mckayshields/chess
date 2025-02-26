package server;

import com.google.gson.Gson;
import exception.ResponseException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearService service;

    public ClearHandler(ClearService service) {
        this.service = service;
    }

    public Object clearHandler(Request req, Response res) throws ResponseException {
        try {
            service.clear();
            return new Gson().toJson(null);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

}

package server;
import exception.ResponseException;
import model.*;
import model.RegisterResponse;
import spark.*;
import service.UserService;
import com.google.gson.Gson;

public class UserHandler {
    private final UserService service;
    public UserHandler(UserService service) {
        this.service = service;
    }
    private Object registerHandler(Request req, Response res) throws ResponseException{
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResponse registerResponse = service.register(registerRequest);
            return new Gson().toJson(registerResponse);
        }
        catch(ResponseException e){
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    private Object loginHandler(Request req, Response res) throws ResponseException{
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        try {
            LoginResponse loginResponse = service.login(loginRequest);
            return new Gson().toJson(loginResponse);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }

    private Object logoutHandler(Request req, Response res) throws ResponseException{
        var logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        try {
            service.logout(logoutRequest);
            return new Gson().toJson(null);
        }
        catch(ResponseException e) {
            int statusCode = e.StatusCode();
            res.status(statusCode);
            throw e;
        }
    }
}

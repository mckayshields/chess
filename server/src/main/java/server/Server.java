package server;

import dataaccess.*;
import exception.ResponseException;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import javax.xml.crypto.Data;

public class Server {
    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;

    UserService userService;
    GameService gameService;
    ClearService clearService;

    UserHandler userHandler;
    GameHandler gameHandler;
    ClearHandler clearHandler;
    WebSocketHandler wsHandler;

    public Server(){
        try{
            userDataAccess = new SqlUserData();
            authDataAccess = new SqlAuthData();
            gameDataAccess = new SqlGameData();
        }
        catch(DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }

        userService = new UserService(userDataAccess, authDataAccess);
        gameService = new GameService(gameDataAccess, authDataAccess);
        clearService = new ClearService(gameDataAccess, authDataAccess, userDataAccess);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
        clearHandler = new ClearHandler(clearService);
        wsHandler = new WebSocketHandler(userService, gameService);

        try { DatabaseManager.createDatabase(); } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listHandler);
        Spark.post("/game", gameHandler::createHandler);
        Spark.put("/game", gameHandler::joinHandler);
        Spark.delete("/db", clearHandler::clearHandler);

        Spark.webSocket("/ws", wsHandler);

        Spark.exception(ResponseException.class, (e, request, response) -> {
            response.type("application/json");
            response.status(e.getStatusCode());
            response.body(e.toJson());
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

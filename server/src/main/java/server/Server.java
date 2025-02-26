package server;

import dataaccess.*;
import model.GameData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Collection;
import java.util.List;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDataAccess userDataAccess = new MemoryUserData();
        AuthDataAccess authDataAccess = new MemoryAuthData();
        GameDataAccess gameDataAccess = new MemoryGameData();
        UserService userService = new UserService(userDataAccess, authDataAccess);
        GameService gameService = new GameService(gameDataAccess, authDataAccess);
        ClearService clearService = new ClearService(gameDataAccess, authDataAccess, userDataAccess);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listHandler);
        Spark.post("/game", gameHandler::createHandler);
        Spark.put("/game", gameHandler::joinHandler);
        Spark.delete("/db", clearHandler::clearHandler);

        //TODO handle exceptions

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

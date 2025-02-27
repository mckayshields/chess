package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    GameDataAccess gameDataAccess;
    AuthDataAccess authDataAccess;
    UserDataAccess userDataAccess;
    GameService gameService;
    UserService userService;
    String authToken;



    @BeforeEach
    void initialize() throws ResponseException {
        gameDataAccess = new MemoryGameData();
        authDataAccess = new MemoryAuthData();
        userDataAccess = new MemoryUserData();
        gameService = new GameService(gameDataAccess, authDataAccess);
        userService = new UserService(userDataAccess, authDataAccess);
        RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        this.authToken = registerResponse.authToken();
    }

    @Test
    void createGame() throws ResponseException {
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        assertEquals(1, createResponse.gameID());
    }

    @Test
    void createGameBadToken() throws ResponseException {
        String fakeAuthToken = UUID.randomUUID().toString();
        CreateRequest createRequest = new CreateRequest(fakeAuthToken, "newGame");
        ResponseException responseException;
        responseException = assertThrows(ResponseException.class, () -> gameService.createGame(createRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }

    @Test
    void listGames() throws ResponseException {
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        ListRequest listRequest = new ListRequest(authToken);
        ListResponse listResponse = gameService.list(listRequest);
        assertEquals(1, listResponse.games().size());
    }

    @Test
    void listGamesEmpty() throws ResponseException{
        ListRequest listRequest = new ListRequest(authToken);
        ListResponse listResponse = gameService.list(listRequest);
        assertEquals(0, listResponse.games().size());
    }

    @Test
    void joinGame() throws ResponseException{
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        JoinRequest joinRequest = new JoinRequest(authToken, ChessGame.TeamColor.WHITE, 1);
        gameService.join(joinRequest);
        ListRequest listRequest = new ListRequest(authToken);
        ListResponse listResponse = gameService.list(listRequest);
        String username = listResponse.games().iterator().next().whiteUsername();
        assertEquals("myname", username);

    }

    @Test
    void joinMissingGame() throws ResponseException{
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        JoinRequest joinRequest = new JoinRequest(authToken, ChessGame.TeamColor.WHITE, 0);
        ResponseException responseException;
        responseException = assertThrows(ResponseException.class, () -> gameService.join(joinRequest));
        assertEquals("Error: bad request", responseException.getMessage());
    }
}

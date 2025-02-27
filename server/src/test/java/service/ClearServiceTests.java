package service;

import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.eclipse.jetty.util.preventers.GCThreadLeakPreventer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearServiceTests {
        GameDataAccess gameDataAccess;
        AuthDataAccess authDataAccess;
        UserDataAccess userDataAccess;
        GameService gameService;
        UserService userService;
        ClearService clearService;
        String authToken;

        @BeforeEach
        void initialize() throws ResponseException {
            gameDataAccess = new MemoryGameData();
            authDataAccess = new MemoryAuthData();
            userDataAccess = new MemoryUserData();
            gameService = new GameService(gameDataAccess, authDataAccess);
            userService = new UserService(userDataAccess, authDataAccess);
            clearService = new ClearService(gameDataAccess, authDataAccess, userDataAccess);
            RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
            RegisterResponse registerResponse = userService.register(registerRequest);
            this.authToken = registerResponse.authToken();
        }

    @Test
    void clearUsers() throws ResponseException {
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        clearService.clear();
        LoginRequest loginRequest = new LoginRequest("myname", "fakePassword");
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.login(loginRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }

    @Test
    void clearGames() throws ResponseException {
        CreateRequest createRequest = new CreateRequest(authToken, "newGame");
        CreateResponse createResponse = gameService.createGame(createRequest);
        clearService.clear();
        //Register Again and see if all games are removed
        RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        authToken = registerResponse.authToken();
        ListRequest listRequest = new ListRequest(authToken);
        ListResponse listResponse = gameService.list(listRequest);
        assertEquals(0, listResponse.games().size());
    }
}

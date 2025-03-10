package service;

import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {
    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    UserService userService;


    @BeforeEach
    void initialize() throws DataAccessException {
        userDataAccess = new SqlUserData();
        authDataAccess = new SqlAuthData();
        userDataAccess.clear();
        authDataAccess.clear();
        userService = new UserService(userDataAccess, authDataAccess);
    }

    @Test
    void register() throws ResponseException {
        RegisterRequest request = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse response = userService.register(request);
        assertEquals("myname", response.username());
    }

    @Test
    void registerNameTaken() throws ResponseException{
        RegisterRequest request = new RegisterRequest("awesomeName", "password", "email@email");
        RegisterResponse response = userService.register(request);
        RegisterRequest request2 = new RegisterRequest("awesomeName", "password2", "email2@email");
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.register(request2));
        assertEquals("Error: already taken.", responseException.getMessage());
    }

    @Test
    void loginCorrectPassword() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("newName", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("newName", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
        assertEquals("newName", loginResponse.username());
    }

    @Test
    void loginIncorrectPassword() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("Name", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Name", "fakePassword");
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.login(loginRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }

    @Test
    void logoutRealToken() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("RealName", "password", "email@email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("RealName", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
        String authToken = loginResponse.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertEquals("RealName", loginResponse.username());
    }

    @Test
    void logoutBadToken() throws ResponseException{
        String fakeAuthToken = UUID.randomUUID().toString();
        LogoutRequest logoutRequest = new LogoutRequest(fakeAuthToken);
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.logout(logoutRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }
}

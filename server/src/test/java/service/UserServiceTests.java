package service;

import dataaccess.AuthDataAccess;
import dataaccess.MemoryAuthData;
import dataaccess.MemoryUserData;
import dataaccess.UserDataAccess;
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
    void initialize() throws ResponseException {
        userDataAccess = new MemoryUserData();
        authDataAccess = new MemoryAuthData();
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
        RegisterRequest request = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse response = userService.register(request);
        RegisterRequest request2 = new RegisterRequest("myname", "password2", "email2@email");
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.register(request2));
        assertEquals("Error: already taken.", responseException.getMessage());
    }

    @Test
    void loginCorrectPassword() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myname", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
        assertEquals("myname", loginResponse.username());
    }

    @Test
    void loginIncorrectPassword() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myname", "fakePassword");
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.login(loginRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }

    @Test
    void logoutRealToken() throws ResponseException{
        RegisterRequest registerRequest = new RegisterRequest("myname", "password", "email@email");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("myname", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
        String authToken = loginResponse.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertEquals("myname", loginResponse.username());
    }

    @Test
    void logoutBadToken() throws ResponseException{
        String fakeAuthToken = UUID.randomUUID().toString();
        LogoutRequest logoutRequest = new LogoutRequest(fakeAuthToken);
        ResponseException responseException = assertThrows(ResponseException.class, () -> userService.logout(logoutRequest));
        assertEquals("Error: unauthorized", responseException.getMessage());
    }

}

package service;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.*;
import java.util.UUID;
import dataaccess.*;

public class UserService {
    private final MemoryUserData userDataAccess;
    private final MemoryAuthData authDataAccess;

    public UserService(MemoryUserData userDataAccess, MemoryAuthData authDataAccess){
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null ||
                registerRequest.username().isBlank() || registerRequest.password().isBlank() || registerRequest.email().isBlank()) {
            throw new ResponseException(400, "Error: Bad request");
        }

        try {
            UserData newUser= new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            UserData existingUser = userDataAccess.getUser(registerRequest.username());
            if (existingUser != null){
                throw new ResponseException(403, "Error: already taken.");
            }
            userDataAccess.createUser(newUser);
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, registerRequest.username());
            authDataAccess.createAuth(authData);
            return new RegisterResponse(registerRequest.username(), authToken);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }

    }
    public LoginResponse login(LoginRequest loginRequest) throws ResponseException{
        try {
            String username = loginRequest.username();
            String password = loginRequest.password();
            UserData userData = userDataAccess.getUser(username);
            // verify password
            if(!userData.password().equals(password)){
                throw new ResponseException(401, "Error: unauthorized");
            }
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, username);
            authDataAccess.createAuth(authData);
            return new LoginResponse(username, authToken);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }
    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        try{
            String authToken = logoutRequest.authToken();
            if (authDataAccess.getAuth(authToken) == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            authDataAccess.deleteAuth(authToken);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }
}

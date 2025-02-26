package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import exception.ResponseException;

public class ClearService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;
    private final UserDataAccess userDataAccess;

    public ClearService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess, UserDataAccess userDataAccess){
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
    }
    public void clear() throws ResponseException{
        try{
            gameDataAccess.clear();
            authDataAccess.clear();
            userDataAccess.clear();
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }
}
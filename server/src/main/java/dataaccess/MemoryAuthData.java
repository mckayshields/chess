package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthData {
    private final Map<String, AuthData> authDatabase = new HashMap<>();

    public void createAuth(AuthData authData) throws DataAccessException {
        authDatabase.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken){
        return authDatabase.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }

    public void clear(){
        authDatabase.clear();
    }
}

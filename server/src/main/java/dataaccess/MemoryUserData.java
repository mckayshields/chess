package dataaccess;
import java.util.HashMap;
import java.util.Map;
import model.UserData;

public class MemoryUserData implements UserDataAccess{
    private final Map<String, UserData> userDatabase = new HashMap<>();

    public void createUser(UserData userData) {
        userDatabase.put(userData.username(), userData);
    }

    public UserData getUser(String username){
        return userDatabase.get(username);
    }

    public void clear() {
        userDatabase.clear();
    }
}

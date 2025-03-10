package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTests {
    SqlUserData userDataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDataAccess = new SqlUserData();
        userDataAccess.clear();
    }

    @Test
    void testCreateUser() throws DataAccessException {
        UserData userData = new UserData("JohnDoe", "123password", "fake@email");
        userDataAccess.createUser(userData);
        UserData retrievedData = userDataAccess.getUser("name");
        assertEquals(userData.username(), retrievedData.username());
        assertEquals(userData.email(), retrievedData.email());
    }

    @Test
    void createUserBad() throws DataAccessException {
        UserData retrievedData = userDataAccess.getUser("name");
        assertNull(retrievedData);

    }

    @Test
    void testGetUser() throws DataAccessException {
        UserData userData = new UserData("name", "mypassword", "email@email");
        userDataAccess.createUser(userData);
        UserData retrievedData = userDataAccess.getUser("name");
        assertEquals(userData.username(), retrievedData.username());
        assertEquals(userData.email(), retrievedData.email());
    }

    @Test
    void getUserBad() throws DataAccessException {
        UserData userData = new UserData("name", "mypassword", "email@email");
        userDataAccess.createUser(userData);
        UserData retrievedData = userDataAccess.getUser("wrongName");
        assertNull(retrievedData);
    }

    @Test
    void testClear() throws DataAccessException {
        UserData userData = new UserData("name", "mypassword", "email@email");
        userDataAccess.createUser(userData);
        userDataAccess.clear();
        UserData retrievedData = userDataAccess.getUser("name");
        assertNull(retrievedData);
    }

}

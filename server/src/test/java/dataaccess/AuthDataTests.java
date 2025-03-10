package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDataTests {

    @BeforeEach
    void setUp() throws DataAccessException {
        SqlAuthData authDataAccess = new SqlAuthData();
        authDataAccess.clear();
    }

    @Test
    void testCreateAuth() throws DataAccessException {
        AuthData authData = new AuthData("testToken", "testUser");
        AuthDataAccess authDataAccess = new SqlAuthData();
        authDataAccess.createAuth(authData);

        AuthData retrievedAuth = authDataAccess.getAuth("testToken");
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.username());
    }

    @Test
    void createAuthBad() throws DataAccessException {
        AuthDataAccess authDataAccess = new SqlAuthData();
        AuthData retrievedAuth = authDataAccess.getAuth("nonExistentToken");
        assertNull(retrievedAuth);
    }

}
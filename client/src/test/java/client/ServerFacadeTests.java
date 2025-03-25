package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+ port);
        System.out.println("Started Facade");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearServer() throws ResponseException {facade.clear();}


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest() throws Exception{
        AuthData authData = facade.register("username", "password","email@email");
        assertNotNull(authData);
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginTest() throws Exception{
        facade.register("username2", "password2","email2@email");
        AuthData authData = facade.login("username2", "password2");
        assertNotNull(authData);
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginTestBadPassword() throws Exception{
        facade.register("username3", "password3","email3@email");
        assertThrows(ResponseException.class, () -> facade.login("username3", "wrongPassword"));
    }

    @Test
    public void logoutTest() throws Exception{
        facade.register("username4", "password4","email4@email");
        AuthData authData = facade.login("username4", "password4");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutTestBadAuth() throws Exception{
        facade.register("username5", "password5","email5@email");
        AuthData authData = facade.login("username5", "password5");
        assertThrows(ResponseException.class, () -> facade.logout(authData.username()));
    }


}

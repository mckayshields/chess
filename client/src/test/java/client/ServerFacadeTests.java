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

    @Test
    public void createGameTestBadAuth() throws Exception{
        facade.register("player6", "password6", "email6@email");
        var authData = facade.login("player6", "password6");
        assertThrows(ResponseException.class, () -> facade.createGame( "New Game", "bad auth"));
    }

    @Test
    public void createGameTest() throws Exception{
        facade.register("player7", "password7", "email7@email");
        var authData = facade.login("player7", "password7");
        int gameID = facade.createGame( "Game name", authData.authToken()).gameID();
        assertNotEquals(0, gameID);
    }

    @Test
    public void listGameTest() throws Exception{
        facade.register("player8", "password8", "email8@email");
        var authData = facade.login("player8", "password8");
        int gameID = facade.createGame( "Game name", authData.authToken()).gameID();
        Collection<GameData> games = facade.listGames(authData.authToken()).games();
        assertEquals(1, games.size());
    }

    @Test
    public void listGameTestEmpty() throws Exception{
        facade.register("player9", "password9", "email9@email");
        var authData = facade.login("player9", "password9");
        Collection<GameData> games = facade.listGames(authData.authToken()).games();
        assertEquals(0, games.size());
    }



    @Test
    public void observeGameTest() throws Exception{
        var authData = facade.register("player6", "password6", "player6@email.com");
        assertDoesNotThrow(() -> facade.observeGame(authData.authToken(), 0));
    }



}

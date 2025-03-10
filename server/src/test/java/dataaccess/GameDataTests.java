package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameDataTests {
    SqlGameData gameDataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDataAccess = new SqlGameData();
        gameDataAccess.clear();
    }

    @Test
    void testCreateGame() throws DataAccessException {
        int gameID = gameDataAccess.createGame("funGame");
        GameData gameData = gameDataAccess.getGame(gameID);
        assertEquals(1, gameID);
        assertEquals("funGame", gameData.gameName());
    }

    @Test
    void testGetGame() throws DataAccessException {
        int gameID = gameDataAccess.createGame("testGetGame");
        GameData gameData = gameDataAccess.getGame(gameID);
        assertEquals(1, gameID);
        assertEquals("testGetGame", gameData.gameName());
    }

    @Test
    void getMissingGame() throws DataAccessException {
        gameDataAccess.createGame("fungame");
        GameData gameData = gameDataAccess.getGame(2);
        assertNull(gameData);
    }

    @Test
    void testListGames() throws DataAccessException {
        int gameID = gameDataAccess.createGame("funGame");
        int gameID2 = gameDataAccess.createGame("boringGame");
        Collection<GameData> gameList = gameDataAccess.listGames();
        assertEquals(1, gameID);
        assertEquals(2, gameID2);
        assertEquals(2, gameList.size());
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        Collection<GameData> gameList = gameDataAccess.listGames();
        assertEquals(0, gameList.size());
    }

    @Test
    void testClear() throws DataAccessException {
        gameDataAccess.createGame("fungame");
        gameDataAccess.clear();
        GameData gameData = gameDataAccess.getGame(1);
        assertNull(gameData);
    }
}

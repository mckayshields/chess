package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDataAccess {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void update(Integer gameID, GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;
}

package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class MemoryGameData implements GameDataAccess{
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();
    public void createGame(GameData gameData){
        gameDatabase.put(gameData.gameID(), gameData);
    }

    public GameData getGame(Integer gameID) {
        return gameDatabase.get(gameID);
    }

    public Collection<GameData> listGames() {
        return gameDatabase.values();
    }

    public void update(Integer gameID, GameData gameData){
        gameDatabase.replace(gameID, gameData);
    }

    public void clear(){
        gameDatabase.clear();
    }
}

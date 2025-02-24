package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class MemoryGameData {
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();
    private void createGame(GameData gameData){
        gameDatabase.put(gameData.gameID(), gameData);
    }

    private GameData getGame(Integer gameID){
        return gameDatabase.get(gameID);
    }

    private Collection<GameData> listGames() {
        return gameDatabase.values();
    }

    private void update(Integer gameID, GameData gameData){
        gameDatabase.replace(gameID, gameData);
    }

    private void clear(){
        gameDatabase.clear();
    }
}

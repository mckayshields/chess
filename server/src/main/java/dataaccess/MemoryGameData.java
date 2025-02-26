package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import chess.ChessGame;
import model.GameData;

public class MemoryGameData implements GameDataAccess{
    private int gameID = 0;
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();
    public int createGame(String gameName){
        gameID++;
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDatabase.put(gameData.gameID(), gameData);
        return gameID;
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

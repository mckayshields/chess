package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class SqlGameData implements GameDataAccess{
    private int gameCount =0;

    public SqlGameData() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){

            var statement = "INSERT INTO " +
                    "gameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try(var ps = conn.prepareStatement(statement)){
                gameCount++;
                ps.setInt(1, gameCount);
                ps.setString(2, null);
                ps.setString(3, null);
                ps.setString(4, gameName);
                ps.setString(5, gameToString(new ChessGame()));
                ps.executeUpdate();
                return gameCount;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try(var rs = ps.executeQuery()){
                    if (rs.next()){
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameString = rs.getString("game");
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, stringToGame(gameString));
                    }
                    else{
                        return null;
                    }

                }
            }
        }
        catch(Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData";
            try(var ps = conn.prepareStatement(statement)){
                try(var rs = ps.executeQuery()){
                    while(rs.next()){
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameString = rs.getString("game");
                        ChessGame game = stringToGame(gameString);
                        result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    @Override
    public void update(Integer gameID, GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("UPDATE gameData SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?")) {
                statement.setString(1, gameData.whiteUsername());
                statement.setString(2, gameData.blackUsername());
                statement.setString(3, gameData.gameName());
                statement.setString(4, gameToString(gameData.game()));
                statement.setInt(5, gameData.gameID());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) throw new DataAccessException("Item requested to be updated not found");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE gameData";
            try(var ps = conn.prepareStatement(statement)){
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String gameToString(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame stringToGame(String serializedString) {
        return new Gson().fromJson(serializedString, ChessGame.class);
    }

}
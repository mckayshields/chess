package service;
import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;

import javax.xml.crypto.Data;
import java.util.Collection;

public class GameService {

    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;

    public GameService(GameDataAccess gameDataAccess, AuthDataAccess authDataAccess){
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public ListResponse list(ListRequest listRequest) throws ResponseException{
        try {
            String authToken = listRequest.authToken();
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            Collection<GameData> games = gameDataAccess.listGames();
            return new ListResponse(games);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }

    }
    public CreateResponse createGame(CreateRequest createRequest) throws ResponseException {
        try {
            if (createRequest.gameName() == null){
                throw new ResponseException(400, "Error: bad request");
            }
            String authToken = createRequest.authToken();
            String gameName = createRequest.gameName();
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            int gameID = gameDataAccess.createGame(gameName);
            return new CreateResponse(gameID);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }
    public void join(JoinRequest joinRequest) throws ResponseException{
        try {
            if (joinRequest.authToken() == null || joinRequest.gameID() <= 0 || joinRequest.playerColor() == null){
                throw new ResponseException(400, "Error: bad request");
            }
            String authToken = joinRequest.authToken();
            int gameID = joinRequest.gameID();
            ChessGame.TeamColor teamColor = joinRequest.playerColor();
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData == null){
                throw new ResponseException(401, "Error: unauthorized");
            }

            GameData gameData = gameDataAccess.getGame(gameID);
            if (gameData.blackUsername() != null && teamColor == ChessGame.TeamColor.BLACK){
                throw new ResponseException(403, "Error: already taken");
            }
            if (gameData.whiteUsername() != null && teamColor == ChessGame.TeamColor.WHITE){
                throw new ResponseException(403, "Error: already taken");
            }
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            GameData newGameData;
            if (teamColor == ChessGame.TeamColor.BLACK){
                newGameData = new GameData(gameID, gameData.whiteUsername(), authData.username(), gameName, game);
            }
            else{
                newGameData = new GameData(gameID, authData.username(), gameData.blackUsername(), gameName, game);
            }
            gameDataAccess.update(gameID, newGameData);
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }

    public void update(int gameID, GameData gameData) throws DataAccessException{
        gameDataAccess.update(gameID, gameData);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataAccess.getGame(gameID);
    }
}

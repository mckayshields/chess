package service;
import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;
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
            String authToken = joinRequest.authToken();
            int gameID = joinRequest.gameID();
            ChessGame.TeamColor teamColor = joinRequest.playerColor();
            AuthData authData = authDataAccess.getAuth(authToken);
            if (authData == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            GameData gameData = gameDataAccess.getGame(gameID);
            if (gameData == null){
                throw new ResponseException(400, "Error: bad request");
            }
            if (gameData.blackUsername() != null && teamColor == ChessGame.TeamColor.BLACK){
                throw new ResponseException(403, "Error: already taken");
            }
            if (gameData.whiteUsername() != null && teamColor == ChessGame.TeamColor.WHITE){
                throw new ResponseException(403, "Error: already taken");
            }
        }
        catch(DataAccessException e){
            throw new ResponseException(500, "Error: " +e.getMessage());
        }
    }
}

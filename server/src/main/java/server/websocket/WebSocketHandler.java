package server.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final UserService userService;
    private final GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException{
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()){
            case CONNECT -> connect(command, session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
            case MAKE_MOVE -> move((MakeMoveCommand) command, session);
            default -> sendError(session, "Error: command type invalid");
        }
    }

    private void connect(UserGameCommand command, Session session){
        try {
            String username = userService.getUsername(command.getAuthToken());
            GameData gameData = gameService.getGame(command.getGameID());
            connections.add(username, session);
            String message;
            if (username.equals(gameData.whiteUsername())){
                message = "Player " + username + " has joined as WHITE.";
            }
            else if(username.equals(gameData.blackUsername())){
                message = "Player " + username + " has joined as BLACK.";
            }
            else{
                message = "Player " + username + " has joined as an OBSERVER.";
            }
            connections.broadcast(username, new NotificationMessage(message));
            //connections.broadcastGame(new LoadGameMessage(gameData));

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Session session){
        try {
            String username = userService.getUsername(command.getAuthToken());
            GameData gameData = gameService.getGame(command.getGameID());
            GameData newGame = getGameData(username, gameData);
            gameService.update(command.getGameID(), newGame)
            connections.remove(username);
            String message = "Player " + username + " has left the game. They will be missed dearly.";
            connections.broadcast(username, new NotificationMessage(message));
        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private static GameData getGameData(String username, GameData gameData) {
        GameData newGame;
        if (username.equals(gameData.whiteUsername())){
            newGame =  new GameData(gameData.gameID(), null , gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }
        else if (username.equals(gameData.blackUsername())){
            newGame =  new GameData(gameData.gameID(), gameData.whiteUsername(),  null,
                    gameData.gameName(), gameData.game());
        }
        else{
            newGame = new GameData(gameData.gameID(), gameData.whiteUsername(),  gameData.blackUsername(),
                    gameData.gameName(), gameData.game());

        }
        return newGame;
    }

    private void resign(UserGameCommand command, Session session){
        try {
            String username = userService.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = gameService.getGame(gameID);
            gameData.game().setTeamTurn(null);
            gameService.update(gameID, gameData);
            String message = "Player " + username + " has admitted defeat and resigned.";
            connections.broadcast(username, new NotificationMessage(message));
        }
        catch (Exception e){
            sendError(session,"Error: "+ e.getMessage());
        }
    }

    private void move(MakeMoveCommand command, Session session){
        try {
            String username = userService.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            ChessMove move = command.getMove();
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            GameData gameData = gameService.getGame(gameID);
            String piece = gameData.game().getBoard().getPiece(startPosition).getPieceType().toString();
            gameData.game().makeMove(move);
            gameService.update(gameID, gameData);
            String message = "Player " + username + " has moved their " + piece + " from " + startPosition.toString()
                    + " to " + endPosition.toString();
            connections.broadcast(username, new NotificationMessage(message));
            connections.broadcastGame(new LoadGameMessage(gameData));

        } catch (Exception e){
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void sendError(Session session, String message){
        try {
            ErrorMessage errorMessage = new ErrorMessage(message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}


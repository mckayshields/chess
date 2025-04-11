package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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


@WebSocket
public class WebSocketHandler {
    private final UserService userService;
    private final GameService gameService;
    private final ConnectionManager connections = new ConnectionManager();
    private boolean hasResigned;

    public WebSocketHandler(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message){
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        String type = json.get("commandType").getAsString();
        UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(type);
        switch (commandType) {
            case CONNECT -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                connect(command, session);
            }
            case LEAVE -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                leave(command, session);
            }
            case RESIGN -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                resign(command, session);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
                move(command, session);
            }
            default -> sendError(session, "Error: command type invalid");
        }
    }

    private void connect(UserGameCommand command, Session session){
        try {
            hasResigned = false;
            String username = userService.getUsername(command.getAuthToken());
            GameData gameData = gameService.getGame(command.getGameID());
            connections.add(username, session, command.getGameID());
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
            connections.broadcast(username, new NotificationMessage(message), gameData.gameID());
            connections.sendGame(username, new LoadGameMessage(gameData));

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Session session){
        try {
            String username = userService.getUsername(command.getAuthToken());
            GameData gameData = gameService.getGame(command.getGameID());
            GameData newGame = getGameData(username, gameData);
            gameService.update(command.getGameID(), newGame);
            connections.remove(username);
            String message = "Player " + username + " has left the game. They will be missed dearly.";
            connections.broadcast(username, new NotificationMessage(message), command.getGameID());
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
            if (hasResigned){
                sendError(session, "Error: game is now over.");
                return;
            }
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())){
                sendError(session, "Error: observer cannot resign.");
                return;
            }
            hasResigned = true;
            gameData.game().setTeamTurn(null);
            gameService.update(gameID, gameData);
            String message = "Player " + username + " has admitted defeat and resigned.";
            connections.broadcast(null, new NotificationMessage(message), gameID);
        }
        catch (Exception e){
            sendError(session,"Error: "+ e.getMessage());
        }
    }

    private void move(MakeMoveCommand command, Session session){
        try {
            if(hasResigned){
                sendError(session, "Error: game is now over.");
                return;
            }
            String username = userService.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            ChessMove move = command.getMove();
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            GameData gameData = gameService.getGame(gameID);
            ChessGame.TeamColor turnColor = gameData.game().getTeamTurn();
            ChessGame.TeamColor requestColor = null;
            if (username.equals(gameData.whiteUsername())){
                requestColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                requestColor = ChessGame.TeamColor.BLACK;
            }
            if (turnColor != requestColor){
                sendError(session, "Error: You cannot make moves at this time.");
                return;
            }
            String piece = gameData.game().getBoard().getPiece(startPosition).getPieceType().toString();
            gameData.game().makeMove(move);
            gameService.update(gameID, gameData);
            String startString = positionToString(startPosition);
            String endString = positionToString(endPosition);
            String message = "Player " + username + " has moved their " + piece + " from " + startString
                    + " to " + endString;
            System.out.println("METHOD CHECK");
            boolean whiteCheck = gameData.game().isInCheck(ChessGame.TeamColor.WHITE);
            boolean blackCheck = gameData.game().isInCheck(ChessGame.TeamColor.BLACK);
            boolean whiteCheckmate = gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE);
            boolean blackCheckmate = gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK);
            boolean whiteStalemate = gameData.game().isInStalemate(ChessGame.TeamColor.WHITE);
            boolean blackStalemate = gameData.game().isInStalemate(ChessGame.TeamColor.BLACK);
            if (whiteCheckmate){
                connections.broadcast(null,
                        new NotificationMessage(gameData.whiteUsername() +
                                " (WHITE) is in checkmate."), command.getGameID());
            }
            else if(blackCheckmate){
                connections.broadcast(null,
                        new NotificationMessage(gameData.blackUsername() +
                                " (BLACK) is in checkmate."), command.getGameID());
            }
            else if(whiteCheck){
                connections.broadcast(null,
                        new NotificationMessage(gameData.whiteUsername() +
                                " (WHITE) is in check."), command.getGameID());
            }
            else if(blackCheck){
                connections.broadcast(null,
                        new NotificationMessage(gameData.blackUsername() +
                                " (BLACK) is in check."), command.getGameID());
            }

            if (whiteStalemate){
                connections.broadcast(null,
                        new NotificationMessage(gameData.whiteUsername() +
                                " (WHITE) is in stalemate."), command.getGameID());
            }
            if (blackStalemate){
                connections.broadcast(null,
                        new NotificationMessage(gameData.blackUsername() +
                                " (BLACK) is in stalemate."), command.getGameID());
            }
            connections.broadcast(username, new NotificationMessage(message), command.getGameID());
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

    public String positionToString(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        char colChar = (char) ('a' + col - 1);
        String colString = String.valueOf(colChar);
        String rowString = Integer.toString(row);
        return (colString + rowString);
    }
}
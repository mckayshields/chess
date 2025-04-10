package client.websocket;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.JoinCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;
import javax.websocket.*;
import javax.websocket.MessageHandler;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.serverMessageHandler = serverMessageHandler;
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType type = serverMessage.getServerMessageType();
                    switch (type){
                        case LOAD_GAME -> serverMessageHandler.loadGame(new Gson().fromJson(message, LoadGameMessage.class));
                        case ERROR -> serverMessageHandler.error(new Gson().fromJson(message, ErrorMessage.class));
                        case NOTIFICATION -> serverMessageHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                    }
                }
            });
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws ResponseException{
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException{
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException{
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException{
        try {
            var command = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch(Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }
}

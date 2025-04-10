package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException{
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()){
            case CONNECT -> connect( );
            case LEAVE -> leave(command, session);
            case RESIGN -> resign();
            case MAKE_MOVE -> move();
        }
    }

    private void connect(){
        //TODO add connections
        var message = "";
    }

    private void leave(UserGameCommand command, Session session){
        String username = command.getAuthToken()
    }

    private void resign(){}

    private void move(){}
}


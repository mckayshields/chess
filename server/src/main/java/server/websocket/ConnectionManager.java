package server.websocket;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    public final HashMap<String, Connection> connections = new HashMap<>();

    public void add(String username, Session session){
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username){
        connections.remove(username);
    }

    public void broadcast(String excludedName, NotificationMessage notificationMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludedName)) {
                    c.send(new Gson().toJson(notificationMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void broadcastGame(LoadGameMessage loadGame) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c: connections.values()){
            if (c.session.isOpen()){
                    c.send(new Gson().toJson(loadGame));
            }
            else{
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}

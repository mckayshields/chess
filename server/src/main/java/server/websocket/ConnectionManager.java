package server.websocket;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    public final HashMap<String, Connection> connections = new HashMap<>();

    public void add(String username, Session session, int gameID){
        var connection = new Connection(username, session, gameID);
        connections.put(username, connection);
    }

    public void remove(String username){
        connections.remove(username);
    }

    public void broadcast(String excludedName, NotificationMessage notificationMessage, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludedName) && c.getGameID() == gameID) {
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
        int gameID = loadGame.getGame().gameID();
        var removeList = new ArrayList<Connection>();
        for (var c: connections.values()){
            if (c.session.isOpen()){
                if (c.getGameID() == gameID) {
                    c.send(new Gson().toJson(loadGame));
                }
            }
            else{
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void sendGame(String username, LoadGameMessage loadGame) throws IOException {
        var c = connections.get(username);
        c.send(new Gson().toJson(loadGame));
    }
}

package server.websocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public Session session;
    public String username;
    public int gameID;
    public Connection(String username, Session session, int gameID){
        this.username = username;
        this.session = session;
        this.gameID = gameID;

    }
    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public int getGameID(){
        return gameID;
    }
}

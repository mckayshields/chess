package client.websocket;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface ServerMessageHandler {
    void notify(NotificationMessage notificationMessage);
    void loadGame(LoadGameMessage loadGameMessage);
    void error(ErrorMessage errorMessage);
}

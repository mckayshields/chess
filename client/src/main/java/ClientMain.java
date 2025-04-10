import chess.ChessBoard;
import exception.ResponseException;
import ui.ClientUI;
import ui.DrawBoard;

public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            new ClientUI(serverUrl).run();
        }
        catch (ResponseException e){
            System.out.println(e.getMessage());
        }
    }
}
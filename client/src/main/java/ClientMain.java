import chess.*;
import exception.ResponseException;
import ui.ClientUI;

public class ClientMain {
    public static void main(String[] args) {
        ChessBoard defaultBoard = new ChessBoard();
        defaultBoard.resetBoard();
        //new DrawBoard(defaultBoard, false);

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
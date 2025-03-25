import chess.*;
import ui.ClientUI;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        ChessBoard defaultBoard = new ChessBoard();
        defaultBoard.resetBoard();
        //new DrawBoard(defaultBoard, false);

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new ClientUI(serverUrl).run();
    }
}
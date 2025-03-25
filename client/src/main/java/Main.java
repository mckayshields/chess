import chess.*;
import server.Server;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        Pre
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessBoard defaultBoard = new ChessBoard();
        defaultBoard.resetBoard();
        new DrawBoard(defaultBoard, false);
    }
}
import chess.*;
import ui.ClientUI;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        ClientUI ui = new ClientUI("http://localhost:"+ "8080");
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessBoard defaultBoard = new ChessBoard();
        defaultBoard.resetBoard();
        new DrawBoard(defaultBoard, false);
    }
}
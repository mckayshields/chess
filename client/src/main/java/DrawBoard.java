import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class DrawBoard {
    private final ChessBoard chessboard;

    public DrawBoard(ChessBoard chessboard) {
        this.chessboard = chessboard;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if(row % 2 == col % 2){
                    out.print(SET_BG_COLOR_DARK_GREY);
                }
                else{
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessboard.getPiece(position);
                String pieceCharacter = getUnicode(piece);
                out.print(pieceCharacter);
            }
            out.println();
        }
    }

    String getUnicode(ChessPiece piece){
        String pieceCharacter = " ";
        if(piece.getPieceType() == null){
            return pieceCharacter;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            switch (piece.getPieceType()){
                case PAWN:
                    pieceCharacter = "♙";
                    break;
                case ROOK:
                    pieceCharacter = "♖";
                    break;
                case KNIGHT:
                    pieceCharacter = "♘";
                    break;
                case BISHOP:
                    pieceCharacter = "♗";
                    break;
                case QUEEN:
                    pieceCharacter = "♕";
                    break;
                case KING:
                    pieceCharacter = "♔";
                    break;
            }
        }
        else{
            switch (piece.getPieceType()){
                case PAWN:
                    pieceCharacter = "♟";
                    break;
                case ROOK:
                    pieceCharacter = "♜";
                    break;
                case KNIGHT:
                    pieceCharacter = "♞";
                    break;
                case BISHOP:
                    pieceCharacter = "♝";
                    break;
                case QUEEN:
                    pieceCharacter = "♛";
                    break;
                case KING:
                    pieceCharacter = "♚";
                    break;
            }
        }
        return pieceCharacter;
    }

}

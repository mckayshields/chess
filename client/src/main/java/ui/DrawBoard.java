package ui;
import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class DrawBoard {
    private final boolean isBlackPOV;
    private final int start;
    private final int end;
    private final int direction;
    private static final String PIECE_PADDING = " ";

    public DrawBoard(ChessBoard chessboard, boolean isBlackPOV) {
        this.isBlackPOV = isBlackPOV;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        if (isBlackPOV){
            start = 1;
            end = 8;
            direction = 1;
        }
        else{
            start = 8;
            end = 1;
            direction = -1;
        }

        makeHeader(out);
        for (int row = start;  isBlackPOV ? row <= end : row >= end; row += direction) {
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(PIECE_PADDING + row + PIECE_PADDING);
            for(int col = end;  isBlackPOV ? col >= start : col <= start; col += -1*direction) {
                if(row % 2 == col % 2){
                    out.print(SET_BG_COLOR_DARK_GREY);
                }
                else{
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessboard.getPiece(position);
                String pieceCharacter = getUnicode(piece);
                out.print(PIECE_PADDING + pieceCharacter + PIECE_PADDING);
                out.print("\u001B[22m");
            }
            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(PIECE_PADDING + row + PIECE_PADDING);
            out.println();
        }
        makeHeader(out);
    }

    String getUnicode(ChessPiece piece){
        String pieceCharacter = "\u3000";
        if(piece == null){
            return pieceCharacter;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            out.print(SET_TEXT_COLOR_WHITE);
        }
        else {
            //out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_BLACK);
        }
        pieceCharacter = switch (piece.getPieceType()) {
            case PAWN -> "♟";
            case ROOK -> "♜";
            case KNIGHT -> "♞";
            case BISHOP -> "♝";
            case QUEEN -> "♛";
            case KING -> "♚";
        };
        return pieceCharacter;
    }

    void makeHeader(PrintStream out){
        out.print(PIECE_PADDING + " " + PIECE_PADDING);
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for(int col = end;  isBlackPOV ? col >= start : col <= start; col += -1*direction){
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(SET_TEXT_BOLD);
            out.print("\u2001\u200A" + columns[col-1] + "\u2001");
            if (col%3 != 0){
                out.print("\u200A");
            }
            out.print("\u001B[22m");
        }
        out.println();
    }

}

package ui;
import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class DrawBoard {
    private final boolean isBlackPOV;
    private final int start;
    private final int end;
    private final int direction;
    private static final String PIECE_PADDING = "\u2009\u2005";

    public DrawBoard(ChessBoard chessboard, boolean isBlackPOV, ChessPosition currentSquare,
                     Collection<ChessPosition> highlightedSquares) {
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
            out.print("\u001B[49m"); //Default background
            out.print(PIECE_PADDING + row + PIECE_PADDING);
            out.print(SET_BG_COLOR_DARK_GREY);
            for(int col = end;  isBlackPOV ? col >= start : col <= start; col += -1*direction) {
                if(row % 2 == col % 2){
                    out.print(SET_BG_COLOR_DARK_GREY);
                }
                else{
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessboard.getPiece(position);
                if (highlightedSquares != null && highlightedSquares.contains(position)){
                    if (piece == null){
                        out.print(SET_BG_COLOR_GREEN);
                        if (row %2 == col%2){
                            out.print(SET_BG_COLOR_DARK_GREEN);
                        }
                    }
                    else{
                        out.print(SET_BG_COLOR_RED);
                    }
                }
                if (position.equals(currentSquare) && piece !=null){
                    out.print(SET_BG_COLOR_BLUE);
                }
                String pieceCharacter = getUnicode(piece);
                out.print(PIECE_PADDING + pieceCharacter + PIECE_PADDING);
                out.print("\u001B[22m");
            }
            out.print("\u001B[49m"); //Default background
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
        out.print("\u001B[49m"); //Default background
        out.print(PIECE_PADDING + " " + PIECE_PADDING);
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for(int col = end;  isBlackPOV ? col >= start : col <= start; col += -1*direction){
            out.print(SET_TEXT_COLOR_BLUE);
            out.print("\u2001" + columns[col-1] + "\u2005\u2005\u2005");
            out.print("\u001B[22m");
        }
        out.println();
    }

}

package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (type == ChessPiece.PieceType.KING) {
            moves.addAll(KingMoves(board,myPosition));
        } else if (type == ChessPiece.PieceType.QUEEN) {
            moves.addAll(QueenMoves(board,myPosition));
        } else if (type == ChessPiece.PieceType.BISHOP) {
            moves.addAll(BishopMoves(board,myPosition));
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            moves.addAll(KnightMoves(board,myPosition));
        } else if (type == ChessPiece.PieceType.ROOK) {
            moves.addAll(RookMoves(board,myPosition));
        } else if (type == ChessPiece.PieceType.PAWN) {
            moves.addAll(PawnMoves(board,myPosition));
        }
        return moves;
    }

    private Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {1,1}, {1,-1}, {-1,1},{-1,-1}};

        for (int[] direction: directions){
            int newRow = row + direction[0];
            int newCol = column + direction[1];
            ChessPosition newPosition = new ChessPosition(newRow,newCol);

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null) {
                    //add to moves if empty
                    moves.add(new ChessMove(position, newPosition,null));
                }
                else if(piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    //add to moves if killing
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> QueenMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(BishopMoves(board, position));
        moves.addAll(RookMoves(board, position));
        return moves;
    }
    private Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {{1, 1}, {1, -1}, {-1,1},{-1,-1}};

        for (int[] direction: directions){
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];
            while(true){
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                    break;
                }
                ChessPiece piece = board.getPiece(newPosition);
                if (piece ==null){
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else if (piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {{1, 2}, {2, 1}, {-1, -2}, {-2, -1}, {1,-2}, {-2,1}, {-1,2},{2,-1}};

        for (int[] direction: directions){
            int newRow = row + direction[0];
            int newCol = column + direction[1];
            ChessPosition newPosition = new ChessPosition(newRow,newCol);

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null) {
                    //add to moves if empty
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else if(piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    //add to moves if killing
                    moves.add(new ChessMove(position, newPosition,null));
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {{0, 1}, {0, -1}, {1,0},{-1,0}};

        for (int[] direction: directions){
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];
            while(true){
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                    break;
                }
                ChessPiece piece = board.getPiece(newPosition);
                if (piece ==null){
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else if (piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();
        if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE ){
            if (row == 7 && board.getPiece(new ChessPosition(8, column)) == null){
                //TODO PROMOTION
                ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            }
            if (board.getPiece(new ChessPosition(row+1, column)) == null){
                ChessPosition newPosition = new ChessPosition(row + 1,column);
                moves.add(new ChessMove(position,newPosition,null));
                if(row == 2 && board.getPiece(new ChessPosition(row+2, column)) == null) {
                    ChessPosition extraPosition = new ChessPosition(row + 2, column);
                    moves.add(new ChessMove(position, extraPosition, null));
                }
            }
            if (board.getPiece(new ChessPosition(row+1, column+1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                ChessPosition extraPosition = new ChessPosition(row + 1, column + 1);
                moves.add(new ChessMove(position, extraPosition, null));
            }
            if (board.getPiece(new ChessPosition(row+1, column-1)).getTeamColor() == ChessGame.TeamColor.BLACK){
                ChessPosition extraPosition = new ChessPosition(row + 1, column - 1);
                moves.add(new ChessMove(position, extraPosition, null));
            }

        }

        return moves;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
            //off the board
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
        //queen is just rook and bishop combined
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(BishopMoves(board, position));
        moves.addAll(RookMoves(board, position));
        return moves;
    }
    private Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1,1}, {-1,-1}};
        for (int[] direction: directions){
            int newRow = position.getRow();
            int newCol = position.getColumn();
            while(true){
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                // off the board
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                    break;
                }
                ChessPiece piece = board.getPiece(newPosition);
                // empty space
                if (piece ==null){
                    moves.add(new ChessMove(position, newPosition, null));
                }
                // enemy space
                else if (piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                    break;
                }
                //friendly space
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
            //off the board
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

        // rooks four directions
        int[][] directions = {{0, 1}, {0, -1}, {1,0},{-1,0}};

        for (int[] direction: directions){
            int newRow = position.getRow();
            int newCol = position.getColumn();
            while(true){
                // get new position
                newRow += direction[0];
                newCol += direction[1];
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                // off the board
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                    break;
                }
                // empty space
                ChessPiece piece = board.getPiece(newPosition);
                if (piece ==null){
                    moves.add(new ChessMove(position, newPosition, null));
                }
                // enemy space
                else if (piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                    break;
                }
                // friendly space
                else {
                    break;
                }
            }
        }
        return moves;
    }
    private Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //determine direction based on color
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        int direction;
        int promotingRow;
        int beginningRow;
        if (teamColor == ChessGame.TeamColor.BLACK){
            direction = -1;
            promotingRow = 1;
            beginningRow = 7;
        }
        else {
            direction = 1;
            promotingRow = 8;
            beginningRow = 2;
        }
        //get position
        int row = position.getRow();
        int column = position.getColumn();

        ChessPosition forwardPosition = new ChessPosition(row + direction,column);
        if (board.getPiece(forwardPosition) == null){
            if (row + direction == promotingRow){
                moves.add(new ChessMove(position, forwardPosition, PieceType.QUEEN));
                moves.add(new ChessMove(position, forwardPosition, PieceType.BISHOP));
                moves.add(new ChessMove(position, forwardPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(position, forwardPosition, PieceType.ROOK));
            }
            else {
                moves.add(new ChessMove(position, forwardPosition, null));
            }
            if (row == beginningRow){
                ChessPosition twoForwardPosition = new ChessPosition(row + 2 * direction,column);
                if (board.getPiece(twoForwardPosition) == null){
                    moves.add(new ChessMove(position, twoForwardPosition, null));
                }
            }
        }

        // kitty corner captures right
        if (column < 8){
            ChessPosition rightPosition = new ChessPosition(row + direction,column + 1);
            ChessPiece rightPiece = board.getPiece(rightPosition);
            if (rightPiece != null && rightPiece.getTeamColor() != teamColor){
                if (row + direction == promotingRow){
                    moves.add(new ChessMove(position, rightPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(position, rightPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(position, rightPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(position, rightPosition, PieceType.ROOK));
                }
                else {
                    moves.add(new ChessMove(position, rightPosition, null));
                }
            }
        }
        // kitty corner captures left
        if (column > 1){
            ChessPosition leftPosition = new ChessPosition(row + direction,column - 1);
            ChessPiece leftPiece = board.getPiece(leftPosition);
            if (leftPiece != null && leftPiece.getTeamColor() != teamColor){
                if (row + direction == promotingRow){
                    moves.add(new ChessMove(position, leftPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(position, leftPosition, PieceType.BISHOP));
                    moves.add(new ChessMove(position, leftPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(position, leftPosition, PieceType.ROOK));
                }
                else {
                    moves.add(new ChessMove(position, leftPosition, null));
                }
            }
        }
        return moves;
    }

    //overriding hash
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

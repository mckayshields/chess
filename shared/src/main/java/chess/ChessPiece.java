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
        Collection<ChessMove> moves = new ArrayList<>();
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
        return moves;
    }
    private Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }
    private Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }
    private Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }
}

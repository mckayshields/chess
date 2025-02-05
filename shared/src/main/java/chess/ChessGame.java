package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board;

    public ChessGame() {
        this.currentTeam = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (piece == null){
            return validMoves;
        }
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> moves = piece.pieceMoves(board,startPosition);
        for (ChessMove move: moves){
            ChessPiece killedPiece = board.getPiece(move.getEndPosition());
            tryMove(move);
            if (!isInCheck(teamColor)){
                validMoves.add(move);
            }
            undoMove(move, killedPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (!validMoves.contains(move)){
            throw new InvalidMoveException("Invalid move.");
        }

        if (piece.getTeamColor() != currentTeam){
            throw new InvalidMoveException("It is not your turn.");
        }

        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece == null){
            board.addPiece(endPosition, piece);
        }
        else{
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), promotionPiece));
        }
        board.addPiece(startPosition, null);

        if (isInCheck(currentTeam)){
            throw new InvalidMoveException("You are in check!");
        }

        if (currentTeam == TeamColor.WHITE){
            currentTeam = TeamColor.BLACK;
        }
        else{
            currentTeam = TeamColor.WHITE;
        }
    }

    public void undoMove(ChessMove move, ChessPiece killedPiece){
        //undoes a move if testing moves for checkmates

        //get move info
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(endPosition);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        //move piece back
        if (promotionPiece == null){
            board.addPiece(startPosition, piece);
        }
        //undo pawn promotion
        else{
            board.addPiece(startPosition, new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.PAWN));
        }
        //return killed piece
        if (killedPiece != null){
            board.addPiece(endPosition, new ChessPiece(killedPiece.getTeamColor(), killedPiece.getPieceType()));
        }
        else{
            board.addPiece(endPosition, null);
        }
        //switch teams back
        switchTeams();

    }

    public void tryMove(ChessMove move){
        //this makes move without checking if valid (used as helper for valid moves)

        //get move info
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        //put piece on new space
        if (promotionPiece == null){
            board.addPiece(endPosition, piece);
        }
        //promote as needed
        else{
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), promotionPiece));
        }
        //remove from old space
        board.addPiece(startPosition, null);

        //switch teams using helper function
        switchTeams();
    }

    public void switchTeams(){
        if (currentTeam == TeamColor.WHITE){
            currentTeam = TeamColor.BLACK;
        }
        else{
            currentTeam = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        //find the king
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                //make sure right piece and right team
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    //save king's position
                    kingPosition = position;
                    break;
                }
            }
        }
        //look at all enemy pieces
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor){
                    //try all moves and see if any threaten king
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move: moves){
                        if (move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        //not in check if no one threatens the king
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // not in checkmate if not in check
        if(!isInCheck(teamColor)){
            return false;
        }
        //look at all possible space
        for(int row = 1; row<=8; row ++){
            for(int col=1; col<=8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                //if correct team's piece, look at possible moves
                if (piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);
                    for (ChessMove move: moves){
                        ChessPiece killedPiece = board.getPiece(move.getEndPosition());
                        //make move and see if still in check
                        tryMove(move);
                        if(!isInCheck(teamColor)){
                            return false;
                        }
                        //undoMove
                        undoMove(move, killedPiece);
                    }
                }
            }
        }
        //true if all options leave king in check
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // not stalemate if king in check
        if (isInCheck(teamColor)){
            return false;
        }
        //look at all possible spaces
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                //if right team, look to see if valid moves
                if (piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> validMoves = validMoves(position);
                    //if move available
                    if (!validMoves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}

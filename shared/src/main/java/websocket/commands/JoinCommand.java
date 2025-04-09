package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class JoinCommand extends UserGameCommand{
    private final ChessGame.TeamColor color;

    public JoinCommand(String authToken, Integer gameID, ChessGame.TeamColor color){
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public ChessGame.TeamColor getColor(){
        return color;
    }
}

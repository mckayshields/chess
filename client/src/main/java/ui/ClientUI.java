package ui;

import chess.*;
import client.ServerFacade;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import client.websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import static ui.EscapeSequences.*;
import java.util.*;

public class ClientUI {
    private static ServerFacade facade;
    private static WebSocketFacade wsf;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static boolean isRunning = true;
    private static boolean isLoggedIn = false;
    private static boolean isInGameplay = false;
    private static boolean isObserving = false;
    private static boolean isBlack = false;
    private static GameData currentGame;
    private static String authToken;
    private static final Map<Integer, GameData> gamesMap = new HashMap<>();

    public ClientUI(String url){
        facade = new ServerFacade(url);
        ServerMessageHandler serverMessageHandler = new ServerMessageHandler() {
            @Override
            public void notify(NotificationMessage notificationMessage) {
                System.out.println(SET_BG_COLOR_BLUE + notificationMessage.getMessage());
            }

            @Override
            public void loadGame(LoadGameMessage loadGameMessage) {
                GameData chessGame = loadGameMessage.getGame();
                currentGame = chessGame;
                new DrawBoard(chessGame.game().getBoard(), isBlack, null, null);
                System.out.print(getHeader());
            }

            @Override
            public void error(ErrorMessage errorMessage) {
                System.out.println(SET_BG_COLOR_RED + errorMessage.getErrorMessage());
            }
        };
        try {
            wsf = new WebSocketFacade(url, serverMessageHandler);
        }catch (ResponseException e){
            System.out.println(e.getMessage());
        }
    }

    public void run() throws ResponseException {
        System.out.println("♕ Welcome to 240 Chess. We're happy you are here. ♕");
        System.out.println("Type 'help' to get started.");

        while (isRunning) {
            System.out.print(getHeader());
            String input = SCANNER.nextLine();
            handleCommand(input);
        }
    }

    private static String getHeader(){
        if (isInGameplay || isObserving){
            return "[GAME_PLAY] >>> ";
        }
        else if (isLoggedIn){
            return "[LOGGED_IN] >>> ";
        }
        else{
            return "[LOGGED_OUT] >>> ";
        }
    }

    private static void handleCommand(String input)  {
        if (isInGameplay){
            gameplay(input);
        }
        else if (isObserving){
            observing(input);
        }
        else if (isLoggedIn){
            afterLogin(input);
        }
        else{
            beforeLogin(input);
        }
    }

    private static void beforeLogin(String input){
        String[] arguments = input.split("\\s+");
        if (arguments.length == 0){
            return;
        }
        String inputCommand = arguments[0].toUpperCase();
        switch (inputCommand){
            case "HELP":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                displayHelpMenu();
                break;
            case "REGISTER":
                if (arguments.length != 4){
                    System.out.println("Invalid input format");
                    break;
                }
                else{
                    String username = arguments[1];
                    String password = arguments[2];
                    String email = arguments[3];
                    register(username, password,email);
                }
                break;
            case "LOGIN":
                if (arguments.length != 3){
                    System.out.println("Invalid input format");
                    break;
                }
                else{
                    String username = arguments[1];
                    String password = arguments[2];
                    login(username, password);
                }
                break;
            case "QUIT":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                quit();
                break;
            default:
                System.out.println("Unknown command. Type 'HELP' to see a list of possible commands.");
        }
    }

    private static void afterLogin(String input) {
        String[] arguments = input.split("\\s+");
        if (arguments.length == 0){
            return;
        }
        String inputCommand = arguments[0].toUpperCase();
        switch (inputCommand){
            case "CREATE":
                if (arguments.length != 2){
                    System.out.println("Invalid input format");
                    break;
                }
                String gameName = arguments[1];
                create(gameName);
                break;
            case "LIST":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                list();
                break;
            case "JOIN":
                if (arguments.length != 3){
                    System.out.println("Invalid input format");
                    break;
                }
                try {
                    int gameID = Integer.parseInt(arguments[1]);
                    String teamColor = arguments[2];
                    join(gameID, teamColor.toUpperCase());
                    break;
                }
                catch(NumberFormatException e){
                    System.out.println(e.getMessage());
                }

            case "OBSERVE":
                if (arguments.length != 2){
                    System.out.println("Invalid input format");
                    break;
                }
                int observeID = Integer.parseInt(arguments[1]);
                observe(observeID);
                break;
            case "HELP":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                displayHelpMenu();
                break;
            case "LOGOUT":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                logout();
                break;
            case "QUIT":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                quit();
                break;
            default:
                System.out.println("Unknown command. Type 'HELP' to see a list of possible commands.");
        }
    }

    private static void displayHelpMenu(){
        if (isInGameplay) {
            System.out.println("""
                redraw - display the chessboard again
                leave - remove self from game
                move <STARTING SQUARE> <ENDING SQUARE> - make a chess move
                resign - forfeit the game
                highlight <PIECE SQUARE> - see legal moves for a given piece
                """);
        }
        else if (isObserving){
            System.out.println("""
                redraw - display the chessboard again
                leave - remove self from game
                highlight <PIECE SQUARE> - see legal moves for a given piece
                """);
        }
        else if (isLoggedIn){
            System.out.println("""
                create <NAME> - start new game
                list - see current games
                join <ID> [WHITE|BLACK] - play a game
                observe <ID> - watch a game
                logout - when you are done
                quit - exit the chess client
                help - show possible commands
                """);
        }
        else{System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit the chess client
                help - show possible commands
                """);}
    }

    private static void register(String username, String password, String email)  {
        try{
        facade.register(username, password, email);
        System.out.println("Registering " + username + "... ");
        login(username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void login(String username, String password) {
        try{
            AuthData authData = facade.login(username, password);
            System.out.println("Logging in " + username + "... ");
            authToken = authData.authToken();
            isLoggedIn = true;
            System.out.println("     Please type 'list' to see current games.");
            System.out.println("     Type 'help' for more options.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void logout(){
        try{
        facade.logout(authToken);
        System.out.println("Logging out...");
        isLoggedIn = false;
        authToken = null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void quit()  {
        System.out.println("Quitting. Sad to see you go!");
        isRunning = false;
    }

    private static void create(String gameName) {
        try{
        System.out.println("Creating " + gameName);
        facade.createGame(gameName,authToken);
        System.out.println("Available games:");
        list();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void list() {
        try{
        gamesMap.clear();
        Collection<GameData> games = facade.listGames(authToken).games();
        if (games.isEmpty()){
            System.out.println("There are no games running. Please create a game with the following command:");
            System.out.println("create <NAME> - start new game");
        }
        int gameNumber = 1;
        for (GameData game : games) {
            System.out.println(gameNumber + ". " + game.gameName());
            System.out.println("   Players: ");
            System.out.println("       WHITE: " + game.whiteUsername());
            System.out.println("       BLACK: " + game.blackUsername());
            System.out.println();
            gamesMap.put(gameNumber, game);
            gameNumber++;
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void join(int gameNumber, String teamColor) {
        try{
        if (!teamColor.equals("BLACK") && !teamColor.equals("WHITE")){
            System.out.println("Invalid Team Color");
        }
        else {
            isBlack = teamColor.equals("BLACK");
            try{
                int gameID = gamesMap.get(gameNumber).gameID();
                System.out.println("Joining game "+gameNumber + " as " + teamColor + " player.");
                facade.joinGame(gameID, teamColor, authToken);
                ChessBoard board = gamesMap.get(gameNumber).game().getBoard();
                currentGame = gamesMap.get(gameNumber);
                //new DrawBoard(board, isBlack, null, null);
                isInGameplay = true;
                wsf.connect(authToken, currentGame.gameID());
            }
            catch(NullPointerException e){
                System.out.println("Sorry, game "+gameNumber+" does not exist.");
            }
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void observe(int gameNumber) {
        try{
        System.out.println("Displaying game " + gameNumber + "...");
        try {
            ChessBoard board = gamesMap.get(gameNumber).game().getBoard();
            currentGame = gamesMap.get(gameNumber);
            new DrawBoard(board, false, null, null);
            wsf.connect(authToken, currentGame.gameID());
            isObserving = true;
        }
        catch(NullPointerException e){
            System.out.println("Sorry, game "+gameNumber+" does not exist.");
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void gameplay(String input) {
        String[] arguments = input.split("\\s+");
        if (arguments.length == 0){
            return;
        }
        String inputCommand = arguments[0].toUpperCase();
        switch (inputCommand){
            case "HELP":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                displayHelpMenu();
                break;
            case "REDRAW":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                redraw();
                break;
            case "LEAVE":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                leave();
                break;
            case "MOVE":
                if (arguments.length != 3){
                    System.out.println("Invalid input format");
                    break;
                }
                movePiece(arguments[1], arguments[2]);
                break;
            case "RESIGN":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                resign();
                break;
            case "HIGHLIGHT":
                if (arguments.length != 2){
                    System.out.println("Invalid input format");
                    break;
                }
                highlight(arguments[1]);
                break;
            default:
                System.out.println("Unknown command. Type 'HELP' to see a list of possible commands.");
        }
    }

    private static void observing(String input) {
        String[] arguments = input.split("\\s+");
        if (arguments.length == 0){
            return;
        }
        String inputCommand = arguments[0].toUpperCase();
        switch (inputCommand){
            case "HELP":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                displayHelpMenu();
                break;
            case "REDRAW":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                redraw();
                break;
            case "LEAVE":
                if (arguments.length != 1){
                    System.out.println("Invalid input format");
                    break;
                }
                leave();
                break;
            case "HIGHLIGHT":
                if (arguments.length != 2){
                    System.out.println("Invalid input format");
                    break;
                }
                highlight(arguments[1]);
                break;
            default:
                System.out.println("Unknown command. Type 'HELP' to see a list of possible commands.");
        }
    }

    private static void movePiece(String startSquare,String endSquare){
        ChessPiece.PieceType promotionPiece = null;
        ChessPosition startPosition = getPosition(startSquare);
        ChessPosition endPosition = getPosition(endSquare);
        if (endPosition.getRow() == 1 || endPosition.getRow() == 8){
            if (currentGame.game().getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN){
                System.out.println("Congratulations! Your pawn is getting promoted! Please input a piece name");
                String input = SCANNER.nextLine().toUpperCase();
                promotionPiece = ChessPiece.PieceType.valueOf(input);
            }
        }
        try {
            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            currentGame.game().makeMove(move);
            //new DrawBoard(currentGame.game().getBoard(), isBlack, null, null);
            wsf.makeMove(authToken, currentGame.gameID(), move);
        }
        catch(InvalidMoveException e){
            System.out.println("Invalid move. Please give it another go.");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void highlight(String square){
        ChessPosition position = getPosition(square);
        Collection<ChessMove> moves = currentGame.game().validMoves(position);
        Collection<ChessPosition> highlightPositions = new ArrayList<>();
        for (ChessMove move:moves){
            highlightPositions.add(move.getEndPosition());
        }
        new DrawBoard(currentGame.game().getBoard(), isBlack, position, highlightPositions);

    }

    private static void redraw(){
        new DrawBoard(currentGame.game().getBoard(), isBlack, null, null);
    }

    private static void leave(){
        try{
            System.out.println("Leaving game");
            wsf.leave(authToken, currentGame.gameID());
            isInGameplay = false;
            isObserving = false;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void resign(){
        System.out.println("Are you sure you want to admit defeat? (Y/N)");
        String input = SCANNER.nextLine().toUpperCase();
        if (input.equals("Y")){
            try {
                System.out.println("Resigning...");
                wsf.resign(authToken, currentGame.gameID());
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } else if (!input.equals("N")) {
            System.out.println("Invalid input.");
        }
    }

    private static ChessPosition getPosition(String position) {
        position = position.toLowerCase();
        char row;
        char col;
        if (position.length() != 2) {
            System.out.println("Invalid input.");
        }
        char char1 = position.charAt(0);
        char char2 = position.charAt(1);
        if (Character.isLetter(char1)) {
            row = char2;
            col = char1;
        } else {
            row = char1;
            col = char2;
        }
        if (col < 'a' || col > 'h' || row < '1' || row > '8') {
            System.out.println("Invalid input.");
            return null;
        }
        int colInteger = col - 'a' + 1;
        int rowInteger = Character.getNumericValue(row);
        return new ChessPosition(rowInteger, colInteger);
    }
}

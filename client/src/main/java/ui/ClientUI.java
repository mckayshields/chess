package ui;

import chess.*;
import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    private static ServerFacade facade;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static boolean isRunning = true;
    private static boolean isLoggedIn = false;
    private static boolean isInGameplay = false;
    private static boolean isBlack = false;
    private static ChessGame currentGame;
    private static String authToken;
    private static Map<Integer, GameData> gamesMap = new HashMap<>();

    public ClientUI(String url){
        facade = new ServerFacade(url);
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
        if (isLoggedIn){
            return "[LOGGED_IN] >>> ";
        }
        else{
            return "[LOGGED_OUT] >>> ";
        }
    }

    private static void handleCommand(String input) throws ResponseException {
        if (isLoggedIn){
            afterLogin(input);
        }
        else if (isInGameplay){
            gameplay(input);
        }
        else{
            beforeLogin(input);
        }
    }

    private static void beforeLogin(String input) throws ResponseException {
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

    private static void afterLogin(String input) throws ResponseException {
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
        if (isLoggedIn){
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
        else if (isInGameplay) {
            System.out.println("""
                redraw - display the chessboard again
                leave - remove self from game
                move <STARTING SQUARE> <ENDING SQUARE> - make a chess move
                resign - forfeit the game
                highlight <PIECE SQUARE> - see legal moves for a given piece
                """);
        }
        else{System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit the chess client
                help - show possible commands
                """);}
    }

    private static void register(String username, String password, String email) throws ResponseException {
        try{
        facade.register(username, password, email);
        System.out.println("Registering " + username + "... ");
        login(username, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void login(String username, String password) throws ResponseException {
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

    private static void logout() throws ResponseException {
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

    private static void create(String gameName) throws ResponseException {
        try{
        System.out.println("Creating " + gameName);
        facade.createGame(gameName,authToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void list() throws ResponseException {
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

    private static void join(int gameNumber, String teamColor) throws ResponseException {
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
                currentGame = gamesMap.get(gameNumber).game();
                new DrawBoard(board, isBlack, null);
                isInGameplay = true;
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
        System.out.println("Displaying game " + gameNumber+ "...");
        try {
            ChessBoard board = gamesMap.get(gameNumber).game().getBoard();
            currentGame = gamesMap.get(gameNumber).game();
            new DrawBoard(board, false, null);
        }
        catch(NullPointerException e){
            System.out.println("Sorry, game "+gameNumber+" does not exist.");
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void gameplay(String input) throws ResponseException {
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
                if (arguments.length != 3){
                    System.out.println("Invalid input format");
                    break;
                }
                resign();
                break;
            case "HIGHLIGHT":
                if (arguments.length != 3){
                    System.out.println("Invalid input format");
                    break;
                }
                highlight(arguments[1]);
                break;
        }
    }

}

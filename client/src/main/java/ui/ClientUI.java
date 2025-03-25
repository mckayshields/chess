package ui;

import chess.ChessBoard;
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
    private static String authToken;
    private static Map<Integer, GameData> gamesMap = new HashMap<>();

    public ClientUI(String url){
        facade = new ServerFacade(url);
    }

    public void run() throws ResponseException {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

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
                int gameID = Integer.parseInt(arguments[1]);
                String teamColor = arguments[2];
                join(gameID, teamColor.toUpperCase());
                break;
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
        else{System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit the chess client
                help - show possible commands
                """);}
    }

    private static void register(String username, String password, String email) throws ResponseException {
        facade.register(username, password, email);
        System.out.println("Registering " + username + "... ");
        login(username, password);
    }

    private static void login(String username, String password) throws ResponseException {
        AuthData authData = facade.login(username, password);
        System.out.println("Logging in " + username + "... ");
        authToken = authData.authToken();
        isLoggedIn = true;
        System.out.println("     Please type 'list' to see current games.");
        System.out.println("     Type 'help' for more options.");
    }

    private static void logout() throws ResponseException {
        facade.logout(authToken);
        System.out.println("Logging out...");
        isLoggedIn = false;
        authToken = null;
    }

    private static void quit()  {
        System.out.println("Quitting. Sad to see you go!");
        isRunning = false;
    }

    private static void create(String gameName) throws ResponseException {
        System.out.println("Creating " + gameName);
        facade.createGame(gameName,authToken);
    }

    private static void list() throws ResponseException {
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
    }

    private static void join(int gameNumber, String teamColor) throws ResponseException {
        if (!teamColor.equals("BLACK") && !teamColor.equals("WHITE")){
            System.out.println("Invalid Team Color");
        }
        else {
            boolean isBlack = teamColor.equals("BLACK");
            try{
                int gameID = gamesMap.get(gameNumber).gameID();
                System.out.println("Joining game "+gameNumber + " as " + teamColor + " player.");
                facade.joinGame(gameID, teamColor, authToken);
                ChessBoard board = gamesMap.get(gameNumber).game().getBoard();
                DrawBoard drawing = new DrawBoard(board, isBlack);
            }
            catch(NullPointerException e){
                System.out.println("Sorry, game "+gameNumber+" does not exist.");
            }
        }
    }
    private static void observe(int gameNumber) {
        System.out.println("Displaying game " + gameNumber+ "...");
        try {
            ChessBoard board = gamesMap.get(gameNumber).game().getBoard();
            DrawBoard drawing = new DrawBoard(board, false);
        }
        catch(NullPointerException e){
            System.out.println("Sorry, game "+gameNumber+" does not exist.");
        }


    }
}

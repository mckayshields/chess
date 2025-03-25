package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {
    private static ServerFacade facade;
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isRunning = true;
    private static boolean isLoggedIn = false;
    private static String authToken;
    private static Map<Integer, GameData> gamesMap = new HashMap<>();

    public ClientUI(String url){
        facade = new ServerFacade(url);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        while (isRunning) {
            System.out.print(getHeader());
            String input = scanner.nextLine();
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

    private static void handleCommand(String input) {
        if (isLoggedIn){
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
                displayHelpMenu();
                break;
            case "REGISTER":
                if (arguments.length != 4){
                    System.out.println("Invalid format");
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
                    System.out.println("Invalid format");
                }
                else{
                    String username = arguments[1];
                    String password = arguments[2];
                    login(username, password);
                }
                break;
            case "QUIT":
                quit();
                break;
            default:
                System.out.println("Unknown command. Type 'HELP' to see a list of possible commands.");
        }
    }

    private static void afterLogin(String input){
        String[] arguments = input.split("\\s+");
        if (arguments.length == 0){
            return;
        }
        String inputCommand = arguments[0].toUpperCase();
        switch (inputCommand){
            case "CREATE":
                if (arguments.length != 2){
                    System.out.println("Invalid format");
                }
                String gameName = arguments[1];
                create(gameName);
                break;
            case "LIST":
                list();
                break;
            case "JOIN":
                if (arguments.length != 3){
                    System.out.println("Invalid format");
                }
                int gameID = Integer.parseInt(arguments[1]);
                String teamColor = arguments[2];
                join(gameID, teamColor.toUpperCase());
                break;
            case "OBSERVE":
                if (arguments.length != 2){
                    System.out.println("Invalid format");
                }
                int observeID = Integer.parseInt(arguments[1]);
                observe(observeID);
                break;
            case "HELP":
                displayHelpMenu();
                break;
            case "LOGOUT":
                logout();
                break;
            case "QUIT":
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

    private static void register(String username, String password, String email){
        facade.register(username, password, email);
        System.out.println("Registering " + username + "... ");
    }

    private static void login(String username, String password){
        AuthData authData = facade.login(username, password);
        System.out.println("Logging in " + username + "... ");
        authToken = authData.authToken();
        isLoggedIn = true;
    }

    private static void logout(){
        facade.logout(authToken);
        System.out.println("Logging out...");
        isLoggedIn = false;
    }

    private static void quit(){
        System.out.println("Quitting. Sad to see you go!");
        isRunning = false;
    }

    private static void create(String gameName){
        System.out.println("Creating " + gameName);
        facade.createGame(gameName,authToken);
    }

    private static void list(){
        Collection<GameData> games = facade.listGames(authToken).games();
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

    private static void join(int gameNumber, String teamColor){
        if (!teamColor.equals("BLACK") && !teamColor.equals("WHITE")){
            System.out.println("Invalid Team Color");
        }
        else {
            int gameID = gamesMap.get(gameNumber).gameID();
            facade.joinGame(gameID, teamColor, authToken);
        }
    }
    private static void observe(int gameID){
        facade.observeGame(authToken, gameID);
    }
}

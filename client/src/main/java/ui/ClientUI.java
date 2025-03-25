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

    public static void main(String[] args) {

        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");

        while (isRunning) {
            System.out.print(getHeader());
            String input = scanner.nextLine().trim();
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
            beforeLogin(input);
        }
        else{
            afterLogin(input);
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
                join(gameID, teamColor);
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

}

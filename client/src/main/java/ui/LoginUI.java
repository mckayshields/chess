package ui;

import client.ServerFacade;
import model.AuthData;
import java.util.Scanner;

public class LoginUI {
    private static ServerFacade facade;
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isRunning = true;
    private static boolean isLoggedIn = false;
    private static String authToken;

    public LoginUI(String url){
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

  
}

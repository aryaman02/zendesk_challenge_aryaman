package net.aryaman.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Scanner;

public class TicketViewerApp {
    //private final ObjectMapper m = new ObjectMapper();
    private OkHttpClient client = new OkHttpClient();
    private String validSubDomain;
    private String validEmail;
    private String validPassword;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        TicketViewerApp tViewerApp = new TicketViewerApp();

        System.out.println("Welcome to the Ticket Viewer Application! Here you can view the exact details of all the tickets posted in your "
            + "zendesk account, or you can choose to view a single ticket of your choice. Follow the instructions below, and you should be good to go!");

        tViewerApp.askUserForLoginCredentials(in);
        tViewerApp.handleUserPreferences(in);

        System.out.println("Thanks for using the Ticket Viewer Application! See you next time!");
        in.close();
    }

    private void askUserForLoginCredentials(Scanner in) {
        System.out.print("Enter the subdomain for your account: ");
        String subDomain = in.nextLine();

        System.out.print("Enter the email address associated with your account: ");
        String email = in.nextLine();

        System.out.print("Enter the password: ");
        String password = in.nextLine();

        boolean isSuccessfulLogin = isValidLogin(subDomain, email, password);

        while (!isSuccessfulLogin) { // keep prompting user to enter login info until he/she gets it right
            System.out.println("Oops! Something went wrong. Please check your subdomain name and/or your login credentials. If all these pieces of information"
                + "are correct, then the server might be temporarily down, and you have to try again later");

            System.out.print("Enter the subdomain for your account: ");
            subDomain = in.nextLine();

            System.out.print("Enter the email address associated with your account: ");
            email = in.nextLine();

            System.out.print("Enter the password: ");
            password = in.nextLine();

            isSuccessfulLogin = isValidLogin(subDomain, email, password);
        }
    }

    private void handleUserPreferences(Scanner in) throws IOException {
        System.out.println("Here are your choices: ");
        System.out.println("all: Display all tickets and their corresponding info (how much you want to see is up to you)");
        System.out.println("single: Display information about ticket of your choice (by id)");
        System.out.println("quit: Quit");

        System.out.print("What would you like to do? ");
        String choice = in.nextLine();
        UserPreferenceHandler handler = new UserPreferenceHandler(in, validSubDomain, validEmail, validPassword);

        while (!choice.equals("quit")) {
            if (choice.equals("all")) {
                handler.displayMultipleTickets();
            } else if (choice.equals("single")) {
                handler.displayInfoAboutSingleTicket();
            } else {
                System.out.println("Invalid input! Please try again.\n");
            }
            System.out.println("Here are your choices: ");
            System.out.println("all: Display all tickets and their corresponding info (how much you want to see is up to you)");
            System.out.println("single: Display information about ticket of your choice (by id)");
            System.out.println("quit: Quit");

            System.out.print("What would you like to do? ");
            choice = in.nextLine();
        }
    }

    private boolean isValidLogin(String sDomain, String email, String pwd) {
        String ticketsRequestURL = "https://" + sDomain + ".zendesk.com/api/v2/tickets.json";
        client = new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationConfig(email, pwd))
            .build();

        Request ticketsRequest = new Request.Builder().url(ticketsRequestURL).build();
        Response ticketsResponse = null;

        try {
            ticketsResponse = client.newCall(ticketsRequest).execute();

            if (ticketsResponse.code() == 200) {
                this.validSubDomain = sDomain;
                this.validEmail = email;
                this.validPassword = pwd;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ticketsResponse.close();
        }
        return (ticketsResponse.code() == 200);
    }
}

package net.aryaman.challenge;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserPreferenceHandler {
    private List<Ticket> zDeskTickets = new ArrayList<>();
    private OkHttpClient client;
    private Scanner input;
    private String validSubDomain;
    private String validEmail;
    private String validPassword;
    private String currentURL;
    private int currReadPtr = 0;

    public UserPreferenceHandler(Scanner in, String sDomain, String email, String pwd) {
        input = in;
        validSubDomain = sDomain;
        validEmail = email;
        validPassword = pwd;
        client = new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationConfig(validEmail, validPassword))
            .build();
    }

    public void displayMultipleTickets() throws IOException {
        if (zDeskTickets.size() == 0) {
            String url = "https://" + validSubDomain + ".zendesk.com/api/v2/tickets.json?page[size]=25";
            Request initRequest = new Request.Builder().url(url).build();
            Response resp = client.newCall(initRequest).execute();
            String ticketsInfo = resp.body().string();
            populateListOfTickets(ticketsInfo);

            for (Ticket t : zDeskTickets) {
                displayTicket(t);
            }
            currReadPtr = zDeskTickets.size();

            DocumentContext jsonContext = JsonPath.parse(ticketsInfo);
            boolean moreTickets = jsonContext.read("$['meta']['has_more']");
            if (moreTickets) {
                currentURL = jsonContext.read("$['links']['next']");
                retrieveMoreTickets();
            } else {
                currentURL = null;
            }

        } else {
            if (currentURL == null) { // finished retrieving all tickets
                if (zDeskTickets.size() <= 25) {
                    for (Ticket t : zDeskTickets) {
                        displayTicket(t);
                    }
                    currReadPtr = 0;
                } else {
                    displayTicketsAlreadyThere();
                }

            } else {
                boolean flag = displayTicketsAlreadyThere();

                if (!flag) {
                    return;
                }
                String choice = askUserForYesOrNoAnswer();
                if (choice.equals("no")) {
                    return;
                }
                // now add more tickets to list for user to see!
                currReadPtr = zDeskTickets.size();
                retrieveMoreTickets();
            }
        }
    }

    private void retrieveMoreTickets() throws IOException {
        boolean moreTickets = true;

        while (moreTickets) {
            if (askUserForYesOrNoAnswer().equals("yes")) {
                moreTickets = handleMoreThan25Tickets();
            } else {
                break;
            }
        }
        currReadPtr = 0;
    }

    private boolean displayTicketsAlreadyThere() {
        for (int i = 0; i < 25; i++) {
            displayTicket(zDeskTickets.get(i));
        }
        currReadPtr = 25;
        String choice = askUserForYesOrNoAnswer();

        if (choice.equals("yes")) {
            int numTicketsRemaining = zDeskTickets.size() - currReadPtr;

            while (numTicketsRemaining > 25 && choice.equals("yes")) {
                for (int i = currReadPtr; i < currReadPtr + 25; i++) {
                    displayTicket(zDeskTickets.get(i));
                }
                currReadPtr += 25;
                numTicketsRemaining = zDeskTickets.size() - currReadPtr;
                choice = askUserForYesOrNoAnswer();
            }
            if (choice.equals("yes")) {
                for (int i = currReadPtr; i < zDeskTickets.size(); i++) {
                    displayTicket(zDeskTickets.get(i));
                }
            } else {
                currReadPtr = 0;
                return false;
            }
            currReadPtr = 0;

        } else {
            currReadPtr = 0;
            return false;
        }
        return true;
    }

    private String askUserForYesOrNoAnswer() {
        System.out.print("Do you want to see more tickets? Type yes or no. ");
        String choice = input.nextLine();

        while (!(choice.equals("yes") || choice.equals("no"))) {
            System.out.println("Invalid input! Please try again.\n");
            System.out.print("Do you want to see more tickets? Type yes or no. ");
            choice = input.nextLine();
        }
        return choice;
    }

    private boolean handleMoreThan25Tickets() throws IOException {
        Request initRequest = new Request.Builder().url(currentURL).build();
        Response resp = client.newCall(initRequest).execute();
        String ticketsInfo = resp.body().string();
        populateListOfTickets(ticketsInfo);

        for (int i = currReadPtr; i < zDeskTickets.size(); i++) {
            displayTicket(zDeskTickets.get(i));
        }
        currReadPtr = zDeskTickets.size();
        DocumentContext jsonContext = JsonPath.parse(ticketsInfo);
        boolean moreTickets = jsonContext.read("$['meta']['has_more']");

        if (moreTickets) {
            currentURL = jsonContext.read("$['links']['next']");
        } else {
            currentURL = null;
        }
        return moreTickets;
    }

    private void displayTicket(Ticket ticket) {
        System.out.println("ID: " + ticket.getId());
        System.out.println("Created at: " + ticket.getTimeOfCreation());
        System.out.println("Last Update at: " + ticket.getTimeOfUpdate());
        System.out.println("Subject: " + ticket.getSubject());
        System.out.println("Description: " + ticket.getDescription());
        System.out.print("Tags: ");

        for (int i = 0; i < ticket.getTags().size(); i++) {
            System.out.print(ticket.getTags().get(i) + ", ");
        }
        System.out.println(ticket.getTags().get(ticket.getTags().size()-1) + "\n");
    }

    private void populateListOfTickets(String ticketsInfo) {
        JSONObject mainObject = new JSONObject(ticketsInfo);
        JSONArray arrayOfTickets = mainObject.getJSONArray("tickets");

        for (int i = 0; i < arrayOfTickets.length(); i++) {
            JSONObject ticketJson = arrayOfTickets.getJSONObject(i);
            int ticketID = ticketJson.optInt("id");
            String timeOfCreation = ticketJson.optString("created_at");
            String timeOfUpdate = ticketJson.optString("updated_at");
            String subject = ticketJson.optString("subject");
            String description = ticketJson.optString("description");

            String ticketInfo = ticketJson.toString();
            DocumentContext jsonContext = JsonPath.parse(ticketInfo);
            List<String> tags = jsonContext.read("$['tags']");

            zDeskTickets.add(new Ticket(ticketID, timeOfCreation, timeOfUpdate, subject, description, tags));
        }
    }

    public void displayInfoAboutSingleTicket() {
        if (zDeskTickets.size() > 0) {
            displayTicket(zDeskTickets.get(promptUserForValidID() - 1));
        } else {
            System.out.println("You first have to view at least 1 ticket, or if there are no tickets in your account, then add some first.\n");
            return;
        }
    }

    private int promptUserForValidID() {
        boolean validInput = false;
        int id = -1;
        System.out.print("Enter an id between 1 and " + String.valueOf(zDeskTickets.size()) + ": ");
        String choice = input.nextLine();

        while (!validInput) {
            try {
                id = Integer.parseInt(choice);
            } catch (NumberFormatException ex) {
                System.out.println("Sorry! The input can only be an integer.\n");
            }
            if (id >= 1 && id <= zDeskTickets.size()) {
                validInput = true;
                break;
            } else {
                System.out.println("Invalid input! Please try again.\n");
            }

            System.out.print("Enter an id between 1 and " + String.valueOf(zDeskTickets.size()) + ": ");
            choice = input.nextLine();
        }
        return id;
    }
}

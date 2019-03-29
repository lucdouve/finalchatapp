package nl.hva.chatapp;

public class User {
    String name = "User";

    void setUsername(ConnectionHandler connectionHandler) {
        boolean set = false;
        String newUsername;
        String agree;
        do {
            connectionHandler.sendMessage("SERVERINPUT#Please provide a username; ");

            newUsername = connectionHandler.readInput();

            connectionHandler.sendMessage("SERVERINPUT#Do you want to use this username?: " + newUsername + " [Y/N]");

            agree = connectionHandler.readInput();

            if (agree != null) {
                if (agree.equals("Y") || agree.equals("y")) {
                    set = true;
                }

                if (agree.equals("N") || agree.equals("n")) {
                    set = false;
                }
            }

        } while (!set);

        connectionHandler.sendMessage("SERVEROK#Username set!!");
        name = newUsername;
        connectionHandler.isLoggedin = true;
        System.out.println(connectionHandler.isLoggedin);

        for (ConnectionHandler connectionsHandler: Listener.connections) {
            if (connectionsHandler.isLoggedin) {
                connectionsHandler.sendMessage("[" + name + "] Joined the room!");
            }
        }
    }
}
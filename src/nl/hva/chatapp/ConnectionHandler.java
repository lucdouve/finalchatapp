package nl.hva.chatapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler extends User implements Runnable {
    private Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
    private Socket sockete;
    private int connectionID;
    private final InputStream input;
    private final BufferedWriter output;
    private boolean isLoggedin;

    public ConnectionHandler(Socket socket, int connectionID) throws IOException {

        LOGGER.log(Level.INFO, "Created new connectionHandler for connection" + connectionID);
        this.sockete = socket;
        this.connectionID = connectionID;

        this.output = new BufferedWriter(new OutputStreamWriter(sockete.getOutputStream()));
        this.input = this.sockete.getInputStream();
    }

    @Override
    public void run() {
        setUsername();

        String msg;

        while (true) {
            msg = readInput();

            if (msg != null) {
                if (msg.contains("SERVEROK#QUIT")) {
                    this.isLoggedin= false;
                    LOGGER.log(Level.INFO, "Client disconnected, connetion ID: " + this.connectionID);
                    for (ConnectionHandler connectionHandler: Listener.connections) {
                        if (connectionHandler.isLoggedin) {
                            connectionHandler.sendMessage("[" + name + "] Left the room");
                        }
                    }
                } else {
                    for (ConnectionHandler connectionHandler : Listener.connections) {
                        if (connectionHandler.isLoggedin && connectionHandler != this) {
                            connectionHandler.sendMessage("[" + name + "] " + msg);
                        }

                        if (connectionHandler.isLoggedin && connectionHandler == this) {
                            sendMessage("[You] " + msg);
                        }
                    }
                }
            }
        }
    }


    private String readInput() {
        int read;

        byte[] buffer = new byte[Main.BUFFER];

        try {
            if ((read = input.read(buffer)) != -1) {
                String msg = new String(buffer, 0, read);
                LOGGER.log(Level.INFO, "[" + name + "] " +msg);
                return msg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setUsername() {
        boolean set = false;
        String newUsername;
        String agree;
        do {
            sendMessage("SERVERINPUT#Please provide a username; ");

            newUsername = readInput();

            sendMessage("SERVERINPUT#Do you want to use this username?: " + newUsername + " [Y/N]");

            agree = readInput();

            if (agree != null) {
                if (agree.equals("Y") || agree.equals("y")) {
                    set = true;
                }

                if (agree.equals("N") || agree.equals("n")) {
                    set = false;
                }
            }

        } while (!set);

        sendMessage("SERVEROK#Username set!!");
        name = newUsername;
        this.isLoggedin = true;

        for (ConnectionHandler connectionHandler: Listener.connections) {
            if (connectionHandler.isLoggedin) {
                connectionHandler.sendMessage("[" + name + "] Joined the room!");
            }
        }
    }

    public void sendMessage(String msg) {
        try {
            output.write(msg);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
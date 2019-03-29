package nl.hva.chatapp;

import nl.hva.chatapp.exceptions.ServerPortException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static final int BUFFER = 1024;
    static final int PORT = 3000;

    public static void main(String[] args) {
	// write your code here
        LOGGER.log(Level.INFO, "Starting application");
        Listener listener = new Listener();

        try {
            listener.runServer();
        } catch (ServerPortException e) {
            e.printStackTrace();
        }
    }
}

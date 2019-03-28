package nl.hva.chatapp;


import nl.hva.chatapp.exceptions.ServerPortException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener {
    private  Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
    private Config config = new Config();
    private int connectionID = 0;

    static List<ConnectionHandler> connections = new Vector<>();

    public void runServer() throws IOException, ServerPortException {
        try (ServerSocket serverSocket = new ServerSocket(config.PORT)) {
            LOGGER.log(Level.INFO, String.format("Started on PORT: %s", config.PORT));

            Socket socketConnection;

            while (true) {
                socketConnection = serverSocket.accept();
                LOGGER.log(Level.INFO, String.format("Connected to %s ", socketConnection.getInetAddress()));

                ConnectionHandler newConnectionHandler = new ConnectionHandler(socketConnection, connectionID);

                Thread newConnectionThread = new Thread(newConnectionHandler);

                connections.add(newConnectionHandler);

                newConnectionThread.start();

                connectionID++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerPortException();
        }
    }

}

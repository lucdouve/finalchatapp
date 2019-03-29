package nl.hva.chatapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler extends User implements Runnable {
    private final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
    private int connectionID;
    private final InputStream input;
    private final BufferedWriter output;
    private Lock mutex = new ReentrantLock();
    public boolean isLoggedin;


    public ConnectionHandler(Socket socket, int connectionID) throws IOException {

        LOGGER.log(Level.INFO, "Created new connectionHandler for connection" + connectionID);
        this.connectionID = connectionID;

        this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.input = socket.getInputStream();
    }

    @Override
    public void run() {
        setUsername(this);

        String msg;

        while (isLoggedin) {
            msg = readInput();

            if (msg != null) {
                if (msg.contains("SERVEROK#QUIT")) {
                    this.isLoggedin= false;
                    LOGGER.log(Level.INFO, "Client disconnected, connetion ID: " + this.connectionID);
                    this.isLoggedin = false;
                    Listener.connections.remove(this);
                    mutex.lock();
                    Thread thread = Listener.threads.get(connectionID);
                    mutex.unlock();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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


    public String readInput() {
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

    public void sendMessage(String msg) {
        try {
            output.write(msg);
            output.flush();
            LOGGER.log(Level.INFO, "Sent message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package nl.hva.chatapp.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerPortException extends Throwable {
    public ServerPortException() {
        Logger.getLogger(ServerPortException.class.getName()).log(Level.INFO, "Server port exception");
        System.exit(-1);
    }

}

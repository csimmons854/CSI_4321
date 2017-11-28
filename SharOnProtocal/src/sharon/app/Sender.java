/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.Message;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Sender thread to send searches to other nodes
 */
class Sender implements Runnable {
    private Thread t;
    private Message msg;
    private Connection connection;
    private Logger log;

    Sender(Message msg, Connection newConnection, Logger logger) {
        this.msg = msg;
        connection = newConnection;
        log = logger;
    }

    public void run() {
        try {
            synchronized (connection.getOutData()) {
                msg.encode(connection.getOutData());
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }

    }
}

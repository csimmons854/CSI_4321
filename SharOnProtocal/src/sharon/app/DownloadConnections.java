/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;




/**
 * Listener thread that waits for incoming download connections
 */
class DownloadConnections implements Runnable {
    private Thread t;
    private ServerSocket downloadServer;
    private String dir;
    private ExecutorService downloadExecutor;
    private Logger log;



    DownloadConnections(ServerSocket newServer, String dir,
                        Logger logger) {

        downloadServer = newServer;
        this.dir = dir;
        log = logger;
        downloadExecutor = null;
    }

    public void run() {
        try {
            downloadExecutor = Executors.newFixedThreadPool(4);
            while(true)
            {
                Socket clientCon = downloadServer.accept();
                downloadExecutor.execute(new Download(clientCon,dir, log));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

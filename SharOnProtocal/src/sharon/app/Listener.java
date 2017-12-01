/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.*;

import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Logger;

import static sharon.serialization.Message.decode;

class Listener implements Runnable {
    private Thread t;
    private Message msg;
    private MessageInput inData;
    private MessageOutput outData;
    private HashMap<String, String> searchMap;
    private Connection connection;
    private int port;
    private String directory;
    private Logger log;

    Listener(Connection newConnection, HashMap<String, String> searchMap, String dir, int newPort,
             Logger logger) {
        inData = newConnection.getInData();
        outData = newConnection.getOutData();
        connection = newConnection;
        this.searchMap = searchMap;
        port = newPort;
        directory = dir;
        log = logger;

    }

    public void run() {
        List<Result> resultList;
        Boolean killThread = false;
        try {
            synchronized (inData) {
                while (!killThread) {
                    System.out.println("Listening");
                    msg = null;
                    try {
                        msg = decode(inData);

                        if (msg instanceof Search) {
                            Response outResponse = Utilities.createRsp((Search) msg,directory,port,log);

                            outResponse.encode(outData);
                        }
                        if (msg instanceof Response) {
                            System.out.println("Search Response for " +
                                    searchMap.get(Arrays.toString(msg.getID())));
                            System.out.println("Download host: " + ((Response) msg).getResponseHost());
                            resultList = ((Response) msg).getResultList();
                            for (int i = 0; i < resultList.size(); i++) {
                                System.out.println("\t" + resultList.get(i).getFileName()
                                        + ": ID " + resultList.get(i).getFileID()
                                        + " (" + resultList.get(i).getFileSize()
                                        + " bytes)");
                            }
                        }
                    }catch (SocketException e){
                        //System.err.println("Error at " + connection.getClientSocket().getInetAddress() +": " + e.getMessage());
                        //e.printStackTrace();
                        killThread = true;
                    }catch (BadAttributeValueException e){
                        System.err.println("Message was not sent");
                        log.warning(e.getMessage());
                    }
                }
            }
        }catch (IOException e) {
            try {
                connection.getClientSocket().close();
            } catch (IOException e1) {
                log.warning(e1.getMessage());
            }
            log.warning(e.getMessage());
        }
        System.out.println("Thread Killed");
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }

    }
}
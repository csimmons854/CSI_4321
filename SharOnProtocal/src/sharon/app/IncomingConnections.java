package sharon.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

class IncomingConnections implements Runnable {
    private Thread t;
    private ServerSocket server;
    private CopyOnWriteArrayList<Connection> connections;
    private HashMap<String,String> searchMap;
    private String dir;
    private int port;
    private Logger log;

    IncomingConnections(ServerSocket newServer, CopyOnWriteArrayList<Connection> newConnections,
                        HashMap<String,String> searchMap, String dir, int newPort, Logger logger) {
        server = newServer;
        connections = newConnections;
        this.searchMap = searchMap;
        this.dir = dir;
        port = newPort;
        log = logger;
    }

    public void run() {
            synchronized (connections) {
                while (true) {
                    try {
                        System.out.println("waiting for something");
                        Socket neighborNodeConnection = server.accept();
                        neighborNodeConnection.setSoTimeout(15000);
                        System.out.println("New Connection: " + neighborNodeConnection.getInetAddress() + ":" + neighborNodeConnection.getPort());
                        Connection newConnection = new Connection(neighborNodeConnection);
                        if (newConnection.getInData().getNodeResponse().equals("INIT SharOn/1.0\n\n")) {
                            newConnection.getOutData().writeByteArray("OK SharOn\n\n".getBytes("ASCII"));
                            connections.add(newConnection);
                            log.info("Connection Established too: " + newConnection.getClientSocket().getInetAddress());
                            Listener newListener = new Listener(newConnection, searchMap, dir, port, connections, log);
                            newListener.start();
                        } else {
                            newConnection.getOutData().writeByteArray("REJECT 300 Bad Handshake\n\n".getBytes());
                            log.warning("Rejected Connection to: " + newConnection.getClientSocket().getInetAddress() +
                                    " <Bad Handshake>");
                            newConnection.getClientSocket().close();
                            neighborNodeConnection.close();
                            connections.remove(newConnection);
                        }
                    }catch (IOException e){
                        System.out.println(e.getMessage());
                        log.warning(e.getMessage());
                    }
                }
            }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }

    }
}
package sharon.app;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class NodeAIO {
    private InetAddress mavenAddress;
    private int mavenPort;
    private int localPort;
    private int downloadPort;
    private static Logger log;
    final private int MAXMVNS = 5;
    final private int NGHBRS = 10;
    final private int RFSH = 10;
    private Set<AsynchronousSocketChannel> channelSet;


    public NodeAIO(InetAddress mavenAddress, int mavenPort, int localPort, int downloadPort) {
        this.mavenAddress = mavenAddress;
        this.mavenPort = mavenPort;
        this.localPort = localPort;
        this.downloadPort = downloadPort;
    }

    static public void main(String [] args) throws IOException {
        if ((args.length < 4) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Maven Address> <Maven Port> <Local Port>" +
            "<Download Port>");
        }

        InetAddress mavenAddress = InetAddress.getByName(args[0]);
        NodeAIO nodeAIO = new NodeAIO(mavenAddress,Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        nodeAIO.start();
    }

    public void start() throws IOException {
        POSI posi = new POSI(new InetSocketAddress(InetAddress.getLocalHost(),localPort));
        System.out.println("Created POSI");

        log = Logger.getLogger("SharOnProtocol.log");
        FileHandler fh;
        fh = new FileHandler("SharOnProtocol.log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);

        try {
            DownloadConnections downloadConnections = new DownloadConnections(new ServerSocket(downloadPort),
                  ".\\SharonFiles", log);
            downloadConnections.start();

        }catch (IOException e){
            System.err.println(e.getMessage());
        }

        try {
            posi.addMaven(new InetSocketAddress(mavenAddress, mavenPort));



        } catch (IOException e) {
            System.err.println("Failed to establish connection to maven");
            e.printStackTrace();
        }
        System.out.println(posi.getMavens());
        System.out.println(posi.getNodes());

        /*
        try (AsynchronousServerSocketChannel listenChannel = AsynchronousServerSocketChannel.open()) {
            // Bind local port
            listenChannel.bind(new InetSocketAddress(localPort));
            startListening(listenChannel);

            // Block until current thread dies
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Server Interrupted", e);
        }
        */
        AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();

        //InetSocketAddress addr = new InetSocketAddress("localhost",6969);

        for (InetSocketAddress addr: posi.getNodes()) {

        }
        clientChannel.connect(addr,null,new CompletionHandler<Void,Void>() {
                @Override
                public void completed(Void a, Void b) {
                    try {
                        handleConnect(clientChannel);
                    } catch (IOException e) {
                        failed(e, null);
                    }

                }

                @Override
                public void failed(Throwable e, Void attachment) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            });



        while(true){

        }



        //System.exit(0);
    }



    public static void handleConnect(final AsynchronousSocketChannel clientChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.wrap("INIT SharOn/1.0\n\n".getBytes());
        clientChannel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesWritten, ByteBuffer buf) {
                try {
                    handleHandshake(clientChannel);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buf) {
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });

    }

    public static void handleHandshake(final AsynchronousSocketChannel clntChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        clntChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try{
                    handleHandshakeRsp(clntChannel, buf, bytesRead);
                }catch (IOException e){
                    //do something
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    clntChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });
    }

    public static void handleHandshakeRsp(final AsynchronousSocketChannel clientChannel,
                                          ByteBuffer buf, Integer bytesRead) throws IOException{
        String rsp = "";
        if(bytesRead > 0){
            for(int i = 0; i < bytesRead; i++){
                rsp += (char)buf.get(i);
            }
        }
        if(rsp.equals("OK SharOn\n\n")){
            log.info("Connected " + clientChannel.getRemoteAddress());

        }

    }

    public static void startListening(AsynchronousServerSocketChannel listenChannel){
        // Create accept handler
        listenChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel clntChan, Void attachment) {
                listenChannel.accept(null, this);
                try {
                    handleAccept(clntChan);
                } catch (IOException e) {
                    failed(e, null);
                }
            }

            @Override
            public void failed(Throwable e, Void attachment) {
                log.log(Level.WARNING, "Close Failed", e);
            }
        });
    }

    /**
     * Called after each accept completion
     *
     * @param clntChan channel of new client
     * @throws IOException if I/O problem
     */
    public static void handleAccept(final AsynchronousSocketChannel clntChan) throws IOException {
        ByteBuffer buf = ByteBuffer.allocateDirect(65535);
        clntChan.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try {
                    handleRead(clntChan, buf, bytesRead);
                } catch (IOException e) {
                    log.log(Level.WARNING, "Handle Read Failed", e);
                }
            }

            public void failed(Throwable ex, ByteBuffer v) {
                try {
                    clntChan.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });
    }

    /**
     * Called after each read completion
     *
     * @param clntChan channel of new client
     * @param buf byte buffer used in read
     * @throws IOException if I/O problem
     */
    public static void handleRead(final AsynchronousSocketChannel clntChan, ByteBuffer buf, int bytesRead)
            throws IOException {
        if (bytesRead == -1) { // Did the other end close?
            clntChan.close();
        } else if (bytesRead > 0) {
            buf.flip(); // prepare to write
            for(int i = 0; i < bytesRead; i++){
                System.out.print((char)buf.get(i));
            }
            System.out.println();
            clntChan.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
                public void completed(Integer bytesWritten, ByteBuffer buf) {
                    try {
                        handleWrite(clntChan, buf);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Handle Write Failed", e);
                    }
                }

                public void failed(Throwable ex, ByteBuffer buf) {
                    try {
                        clntChan.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed", e);
                    }
                }
            });
        }
    }

    /**
     * Called after each write
     *
     * @param clntChan channel of new client
     * @param buf byte buffer used in write
     * @throws IOException if I/O problem
     */
    public static void handleWrite(final AsynchronousSocketChannel clntChan, ByteBuffer buf) throws IOException {
        if (buf.hasRemaining()) { // More to write

            clntChan.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
                public void completed(Integer bytesWritten, ByteBuffer buf) {
                    try {
                        handleWrite(clntChan, buf);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Handle Write Failed", e);
                    }
                }

                public void failed(Throwable ex, ByteBuffer buf) {
                    try {
                        clntChan.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed", e);
                    }
                }
            });
        } else { // Back to reading
            buf.clear();
            clntChan.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
                public void completed(Integer bytesRead, ByteBuffer buf) {
                    try {
                        handleRead(clntChan, buf, bytesRead);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Handle Read Failed", e);
                    }
                }

                public void failed(Throwable ex, ByteBuffer v) {
                    try {
                        clntChan.close();
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Close Failed", e);
                    }
                }
            });
        }
    }
}


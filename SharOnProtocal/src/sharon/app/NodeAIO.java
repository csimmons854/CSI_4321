package sharon.app;

import sharon.serialization.BadAttributeValueException;
import sharon.serialization.RoutingService;
import sharon.serialization.Search;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
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
    private CopyOnWriteArrayList<AsynchronousSocketChannel> channelSet = new CopyOnWriteArrayList<>();
    private Set<InetSocketAddress> connectedAddresses = new HashSet<>();
    final private Semaphore channelSetLock = new Semaphore(1);
    static private String directory;


    public NodeAIO(InetAddress mavenAddress, int mavenPort, int localPort, int downloadPort) {
        this.mavenAddress = mavenAddress;
        this.mavenPort = mavenPort;
        this.localPort = localPort;
        this.downloadPort = downloadPort;
    }

    static public void main(String [] args) throws IOException, InterruptedException {
        if ((args.length < 5) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Maven Address> <Maven Port> <Local Port>" +
            "<Download Port> <Download Directory>");
        }

        directory = args[4];
        InetAddress mavenAddress = InetAddress.getByName(args[0]);
        NodeAIO nodeAIO = new NodeAIO(mavenAddress,Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        nodeAIO.start();
    }

    public void start() throws IOException, InterruptedException {
        log = Logger.getLogger("SharOnProtocol.log");
        FileHandler fh;
        fh = new FileHandler("SharOnProtocol.log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);

        /*
        POSI posi = new POSI(new InetSocketAddress(InetAddress.getLocalHost(),localPort));
        System.out.println("Created POSI");

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
        System.out.println("Mavens" + posi.getMavens());
        System.out.println("Nodes" + posi.getNodes());

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

        ArrayList<InetSocketAddress> nodesList = new ArrayList<>();
        /*
        for(InetSocketAddress addr: posi.getNodes()){
            nodesList.add(addr);
        }
        */

            //int i = new Random().nextInt(posi.getNodes().size());
            InetSocketAddress addr = new InetSocketAddress("localhost",6969);
            if(!connectedAddresses.contains(addr)) {
                connectedAddresses.add(addr);
                AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
                clientChannel.connect(addr, null, new CompletionHandler<Void, Void>() {
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
                        System.out.println("Failed to Connect");
                    }
                });
                System.out.println("Search for everything");
                byte [] id = {0, 0, 28, 0, 0, 0, 57, 17, 0, 0, 0, 0, 87, 51, 0};
                byte [] src = {0,0,0,0,0};
                byte [] dest = {0,0,0,0,0};
                try {
                    Search testSearch = new Search(id,1, RoutingService.BREADTHFIRSTBROADCAST,src,dest,"");

                    Boolean sent  = false;
                    while(!sent){
                        if(channelSet.size() > 0){
                            new SenderAIO(channelSet.get(0),testSearch,log);
                            sent = true;
                        }
                    }

                } catch (BadAttributeValueException e) {
                    e.printStackTrace();
                }
            }

        System.out.println("");


        while(true){

        }




        //System.exit(0);
    }



    public void handleConnect(final AsynchronousSocketChannel clientChannel) throws IOException{
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

    public void handleHandshake(final AsynchronousSocketChannel clntChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        clntChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try{
                    handleHandshakeRsp(clntChannel, buf, bytesRead);
                }catch (IOException e){
                    //do something
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    public void handleHandshakeRsp(final AsynchronousSocketChannel clientChannel,
                                          ByteBuffer buf, Integer bytesRead) throws IOException, InterruptedException {
        String rsp = "";
        if(bytesRead > 0){
            for(int i = 0; i < bytesRead; i++){
                rsp += (char)buf.get(i);
            }
        }
        if(rsp.equals("OK SharOn\n\n")){
            //log.info("Connected " + clientChannel.getRemoteAddress());
            System.out.println("Connected to " + clientChannel.getRemoteAddress());
            channelSetLock.acquire();
            new ListenerAIO(clientChannel, log,downloadPort,directory);
            channelSet.add(clientChannel);
            channelSetLock.release();
        }else{
            System.out.println("Handshake rejected " + clientChannel.getRemoteAddress());
            throw new IOException("Handshake rejected");
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


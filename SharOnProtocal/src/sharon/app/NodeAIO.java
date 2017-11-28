package sharon.app;

import sharon.serialization.BadAttributeValueException;
import sharon.serialization.RoutingService;
import sharon.serialization.Search;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.*;
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
    private ArrayList<Byte> cache = new ArrayList<>();
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
        HashMap<String, String> searchMap = new HashMap<>();

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



        //ArrayList<InetSocketAddress> nodesList = new ArrayList<>();
        /*
        for(InetSocketAddress addr: posi.getNodes()){
            nodesList.add(addr);
        }
        */

        AsynchronousServerSocketChannel serverSocketChannel =
                AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(localPort));

        new IncomingConnectionsAIO(channelSet,serverSocketChannel,log,downloadPort,directory);

        Boolean exitFlag = false;
        while(!exitFlag) {
            System.out.println("Enter a Command");
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            String getLine = reader.nextLine();
            String command = "";
            StringTokenizer commandTokenizer = new StringTokenizer(getLine);

            if(!getLine.equals("")) {
                command = commandTokenizer.nextToken();
            }

            switch (command) {
                case "exit":
                    exitFlag = true;
                    break;
                case "connect":
                    if (commandTokenizer.countTokens() >= 2) {
                        InetSocketAddress address = new InetSocketAddress(commandTokenizer.nextToken(),
                                Integer.parseInt(commandTokenizer.nextToken()));
                        try {
                            initiateConnection(address);
                        } catch (IOException e) {
                            log.warning(e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid connect format connect " +
                                "<connector Node> <connector port>");
                    }
                    break;
                case "download":
                    if (commandTokenizer.countTokens() == 4) {
                        String outDownloadName = commandTokenizer.nextToken();
                        int outDownloadPort = Integer.parseInt(commandTokenizer.nextToken());
                        String fileID = commandTokenizer.nextToken();
                        String fileName = commandTokenizer.nextToken();
                        /*
                        if (!Utilities.checkForFile(fileName, directory)) {
                            try {
                                System.out.println("Download name: " + outDownloadName);
                                System.out.println("Download Port: " + outDownloadPort);

                                Connection newConnection = new Connection(new Socket(outDownloadName, outDownloadPort));
                                System.out.println("Download Connection established");
                                newConnection.writeMessage(fileID + "\n");
                                System.out.println("File ID: " + fileID);
                                StringBuilder rsp = new StringBuilder();
                                for (int i = 0; i < 4; i++) {
                                    rsp.append((char) newConnection.getInData().getByte());
                                }
                                if (rsp.toString().equals("OK\n\n")) {
                                    File newFile = new File(directory + "\\" + fileName);
                                    OutputStream out = new FileOutputStream(newFile);
                                    InputStream in = newConnection.getClientSocket().getInputStream();
                                    byte[] buffer = new byte[1024];
                                    int read;
                                    while ((read = in.read(buffer)) != -1) {
                                        out.write(buffer, 0, read);
                                    }
                                    out.close();
                                    in.close();
                                } else {
                                    InputStream in = newConnection.getClientSocket().getInputStream();
                                    int read;
                                    while ((read = in.read()) != -1) {
                                        rsp.append((char) read);
                                    }
                                    System.out.println(rsp);
                                }
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                                log.warning(e.getMessage());
                            }
                        } else {
                            System.out.println("File already exists in directory");
                        }
                        */
                    } else {
                        System.out.println("Invalid download format: download " +
                                "<download Node> <download port> <File ID> " +
                                "<File Name>");
                    }
                    break;
                default:
                    try {
                        byte[] sourceAddress = {0,0,0,0,0};
                        byte[] destinationAddress = {0,0,0,0,0};
                        Search srch = new Search(Utilities.randomID(), 1, RoutingService.BREADTHFIRSTBROADCAST,
                                sourceAddress, destinationAddress, command);
                        searchMap.put(Arrays.toString(srch.getID()), command);
                        System.out.println("Searching for: " + command);
                        for (AsynchronousSocketChannel channel : channelSet) {
                            if (channel.isOpen()) {
                                new SenderAIO(channel,srch,log);
                            } else {
                                channelSet.remove(channel);
                            }
                        }
                    } catch (BadAttributeValueException e) {
                        log.warning(e.getMessage());
                    }
                    break;
            }
        }




        //System.exit(0);
    }

    public AsynchronousSocketChannel initiateConnection(InetSocketAddress addr) throws IOException {
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
                    try {
                        clientChannel.close();
                    } catch (IOException e1) {
                        log.warning(e1.getMessage());
                    }
                    System.out.println("Failed to Connect");
                }
            });

        return clientChannel;
    }

    public void handleConnect(AsynchronousSocketChannel clientChannel) throws IOException{
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

    public void handleHandshake(AsynchronousSocketChannel clntChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        clntChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try{
                    handleHandshakeRsp(clntChannel, buf, bytesRead);
                }catch (IOException e){
                    failed(e,null);
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

    public void handleHandshakeRsp(AsynchronousSocketChannel clientChannel,
                                          ByteBuffer buf, Integer bytesRead) throws IOException, InterruptedException {
        for(int i = 0; i < bytesRead; i++ ){
            cache.add(buf.get(i));
        }
        if(cache.size() >= ("OK SharOn\n\n".getBytes().length)) {
            byte[] bytes = new byte[cache.size()];
            for (int i = 0; i < cache.size(); i++) {
                bytes[i] = cache.get(i);
            }
            String rsp = frameHandshake(ByteBuffer.wrap(bytes));
            if (rsp.equals("OK SharOn\n\n")) {
                log.info("Connected " + clientChannel.getRemoteAddress());
                System.out.println("Connected to " + clientChannel.getRemoteAddress());
                channelSetLock.acquire();
                channelSet.add(clientChannel);
                new ListenerAIO(clientChannel, log, downloadPort, directory,cache);
                channelSetLock.release();
            }
            else {
                System.out.println("Handshake rejected " + clientChannel.getRemoteAddress());
                clientChannel.close();
                throw new IOException("Handshake was rejected " + clientChannel.getRemoteAddress());
            }
        }else{
            handleHandshake(clientChannel);
        }

    }

    public String frameHandshake(ByteBuffer buffer){
        cache.clear();
        String  handshake = "";
        for(int i = 0; i < "OK SharOn\n\n".getBytes().length; i++){
            handshake += (char)buffer.get();
        }
        while(buffer.remaining() > 0){
            cache.add(buffer.get());
        }
        return handshake;
    }
}


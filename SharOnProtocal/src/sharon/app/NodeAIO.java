/*
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
*/
package sharon.app;

import sharon.app.GUI.GHooI;
import sharon.app.GUI.SearchResultsListener;
import sharon.app.GUI.SharOnListener;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.RoutingService;
import sharon.serialization.Search;

import java.io.*;
import java.net.*;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Main NodeAIO class
 */
public class NodeAIO {
    private InetAddress mavenAddress;
    private int mavenPort;
    private int localPort;
    private int downloadPort;
    private static Logger log;
    private ConcurrentHashMap<InetSocketAddress,AsynchronousSocketChannel> channelSet = new ConcurrentHashMap<>();
    private ArrayList<Byte> cache = new ArrayList<>();
    static private String directory;
    private POSI posi;
    private SearchResultsListener searchResultsListener;
    private static boolean none= true;


    /**
     * @param mavenAddress maven address
     * @param mavenPort maven port
     * @param localPort local port of accepting connections
     * @param downloadPort download port for accepting downloads
     */
    NodeAIO(InetAddress mavenAddress, int mavenPort, int localPort, int downloadPort) {
        this.mavenAddress = mavenAddress;
        this.mavenPort = mavenPort;
        this.localPort = localPort;
        this.downloadPort = downloadPort;

    }

    /**
     * @param args program arguments
     * @throws IOException for bad Input/Output
     */
    static public void main(String [] args) throws IOException {
        if ((args.length < 5) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Maven Address> <Maven Port> <Local Port>" +
            "<Download Port> <Download Directory>");
        }

        if(args.length != 6){
            none = false;
        }else if(!args[5].equals("-n")){
            throw new IllegalArgumentException("Parameter(s): <Maven Address> <Maven Port> <Local Port>" +
                    "<Download Port> <Download Directory>");
        }

        directory = args[4];
        InetAddress mavenAddress = InetAddress.getByName(args[0]);
        NodeAIO nodeAIO = new NodeAIO(mavenAddress,Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        nodeAIO.start();
    }

    /**
     * Executes NodeAIO
     * @throws IOException for bad input/output
     */
    public void start() throws IOException {
        log = Logger.getLogger("SharOnProtocol.log");
        FileHandler fh;
        fh = new FileHandler("SharOnProtocol.log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);
        HashMap<String, String> searchMap = new HashMap<>();
        InetSocketAddress localAddress = new InetSocketAddress(InetAddress.getLocalHost(),localPort);


        AsynchronousServerSocketChannel serverSocketChannel =
                AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(localAddress);
        new IncomingConnectionsAIO(channelSet,serverSocketChannel,log,downloadPort,directory,searchMap,searchResultsListener);

        posi = new POSI(localAddress,log,new InetSocketAddress(mavenAddress,mavenPort));
        //posi.start();
        System.out.println("Created POSI");

        try {
            DownloadConnections downloadConnections = new DownloadConnections(new ServerSocket(downloadPort),
                  ".\\SharonFiles", log);
            downloadConnections.start();

        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        /*channelSet.putAll(posi.getChannelSet());
        for(AsynchronousSocketChannel channel : channelSet.values()){
            new ListenerAIO(channel, log, downloadPort, directory,cache,searchMap,searchResultsListener);
        }
        */


        final GHooI gui = new GHooI();

        // Need a SearchResultsListener (from GUI) to make a SharonListener
        searchResultsListener = gui.getSearchResultsListener();

        // Create SharOn listener
        final SharOnListener sharOnListener = new SharOnListener() {

            @Override
            public void search(final String search, final byte[] id) {
                try {
                    searchCommand(search,id,searchMap);
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }

            @Override
            public void download(final long fileID, final InetSocketAddress downloadHost, final  String name) {
                try {
                    new Download(directory,log).startDownload(downloadHost.getHostName(),
                            downloadHost.getPort(),Long.toString(fileID),name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public byte[] getSearchID(final String srch) {
                return Utilities.randomID();
            }
        };
        if(!none) {
            //Set SharOn listener in GUI
            gui.setSharOnListener(sharOnListener);
            // Thread-safe GUI execution
            javax.swing.SwingUtilities.invokeLater(gui);

            Boolean exitFlag = false;
            while (!exitFlag) {
                System.out.println("Enter a Command");
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                String getLine = reader.nextLine();
                String command = "";
                StringTokenizer commandTokenizer = new StringTokenizer(getLine);

                if (!getLine.equals("")) {
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
                                ConnectionAIO newCon = new ConnectionAIO(log);
                                if(!channelSet.containsKey(address)) {
                                    newCon.initiateConnection(address);
                                    channelSet.put(address,newCon.getClientChannel());
                                    new ListenerAIO(newCon.getClientChannel(), log, downloadPort, directory, cache,
                                            searchMap, searchResultsListener);
                                }

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
                            String outDownloadName;
                            String fileID;
                            String fileName;
                            int outDownloadPort;
                            try {
                                outDownloadName = commandTokenizer.nextToken();
                                outDownloadPort = Integer.parseInt(commandTokenizer.nextToken());
                                fileID = commandTokenizer.nextToken();
                                fileName = commandTokenizer.nextToken();
                                new Download(directory, log).startDownload(outDownloadName,
                                        outDownloadPort, fileID, fileName);
                            }catch (NumberFormatException e){
                                System.out.println("Invalid format for download port, " +
                                        "port must be an integer < 65535 ");
                            }
                        } else {
                            System.out.println("Invalid download format: download " +
                                    "<download Node> <download port> <File ID> " +
                                    "<File Name>");
                        }
                        break;
                    default:
                        searchCommand(command,Utilities.randomID(),searchMap);
                        break;
                }
            }
        }
    }

    public void searchCommand(String searchStr, byte [] id, HashMap<String, String> searchMap) throws IOException {
        System.out.println("Searching for \"" + searchStr + "\"");
        try {
            if (channelSet.size() < posi.getNGHBRS()) {
                for (AsynchronousSocketChannel channel : posi.getChannelSet().values()) {
                    if (!channelSet.contains(channel)) {
                        channelSet.put((InetSocketAddress) channel.getRemoteAddress(),channel);
                        new ListenerAIO(channel, log, downloadPort, directory, cache,
                                searchMap, searchResultsListener);
                    }
                }
            }
            byte[] sourceAddress = {0, 0, 0, 0, 0};
            byte[] destinationAddress = {0, 0, 0, 0, 0};
            Search srch = new Search(id, 1, RoutingService.BREADTHFIRSTBROADCAST,
                    sourceAddress, destinationAddress, searchStr);
            searchMap.put(Arrays.toString(srch.getID()), searchStr);
            for (AsynchronousSocketChannel channel : channelSet.values()) {
                InetSocketAddress address = (InetSocketAddress)channel.getRemoteAddress();
                if (channel.isOpen()) {
                    new SenderAIO(channel, srch, log);
                } else {
                    channelSet.remove(address,channel);
                }
            }
        } catch (BadAttributeValueException e) {
            log.warning(e.getMessage());
        }

    }
}


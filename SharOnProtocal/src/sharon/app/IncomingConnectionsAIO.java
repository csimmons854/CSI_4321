/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.app.GUI.SearchResultsListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Waits for incoming connections
 */
public class IncomingConnectionsAIO {
    private ConcurrentHashMap<InetSocketAddress, AsynchronousSocketChannel> channels;
    private Logger log;
    private int downloadPort;
    private String directory;
    private HashMap<String,String> searchMap;
    private ArrayList<Byte> temp = new ArrayList<>();private SearchResultsListener searchResultsListener;


    public IncomingConnectionsAIO(ConcurrentHashMap<InetSocketAddress, AsynchronousSocketChannel> channels,
                                  AsynchronousServerSocketChannel serverChannel,
                                  Logger log, int downloadPort, String directory,
                                  HashMap<String,String> searchMap,
                                  SearchResultsListener searchResultsListener) {
        this.channels = channels;
        this.log = log;
        this.searchResultsListener = searchResultsListener;
        this.downloadPort = downloadPort;
        this.directory = directory;
        this.searchMap = searchMap;
        listen(serverChannel);
    }

    private void listen(AsynchronousServerSocketChannel serverChannel) {
        try {
            System.out.println(serverChannel.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {

                serverChannel.accept(null, this);
                try {
                    read(clientChannel);
                } catch (IOException e) {
                    failed(e, null);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.err.println("Something went really wrong");
            }
        });
    }


    private void read(final AsynchronousSocketChannel clntChan) throws IOException {
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

    private void handleRead(final AsynchronousSocketChannel clntChan, ByteBuffer buf, int bytesRead)
            throws IOException {
        for(int i = 0; i < bytesRead; i++ ){
            temp.add(buf.get(i));
        }
        if(temp.size() >= "INIT SharOn/1.0\n\n".getBytes().length){
            byte [] bytes = new byte[temp.size()];
            for(int i = 0; i < temp.size(); i++){
                bytes[i] = temp.get(i);
            }
            String rsp = frameHandshake(ByteBuffer.wrap(bytes));
            if(rsp.equals("INIT SharOn/1.0\n\n")){
                clntChan.write(ByteBuffer.wrap("OK SharOn\n\n".getBytes("ASCII")));
                log.info("Connection Established too: " + clntChan.getRemoteAddress());
                new ListenerAIO(clntChan,log,downloadPort,directory,temp,searchMap,searchResultsListener);
                channels.put((InetSocketAddress)clntChan.getRemoteAddress(),clntChan);
            }else{
                clntChan.write(ByteBuffer.wrap("REJECT 300 Bad Handshake\n\n".getBytes()));
                log.warning("Rejected Connection to: " + clntChan.getRemoteAddress() +
                        " <Bad Handshake>");
            }
        }else{
            read(clntChan);
        }

    }

    private String frameHandshake(ByteBuffer buffer){
        temp.clear();
        String handshake = "";
        for(int i = 0; i < "INIT SharOn/1.0\n\n".getBytes().length; i++){
            handshake += (char)buffer.get();
        }
        while(buffer.remaining() > 0){
            temp.add(buffer.get());
        }
        return handshake;
    }
}
package sharon.app;

import mvn.app.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Created by Chris on 11/27/2017.
 */
public class IncomingConnectionsAIO {
    private CopyOnWriteArrayList<AsynchronousSocketChannel> channels;
    private AsynchronousServerSocketChannel serverChannel;
    private Logger log;
    private int downloadPort;
    String directory;
    private ArrayList<Byte> temp = new ArrayList<>();

    public IncomingConnectionsAIO(CopyOnWriteArrayList<AsynchronousSocketChannel> channels,
                                  AsynchronousServerSocketChannel serverChannel,
                                  Logger log, int downloadPort, String directory) {
        this.channels = channels;
        this.serverChannel = serverChannel;
        this.log = log;
        this.downloadPort = downloadPort;
        this.directory = directory;
        listen(serverChannel);
    }

    public void listen(AsynchronousServerSocketChannel serverChannel) {
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

            }
        });
    }


    public void read(final AsynchronousSocketChannel clntChan) throws IOException {
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

    public void handleRead(final AsynchronousSocketChannel clntChan, ByteBuffer buf, int bytesRead)
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
                new ListenerAIO(clntChan,log,downloadPort,directory,temp);
                channels.add(clntChan);
            }else{
                clntChan.write(ByteBuffer.wrap("REJECT 300 Bad Handshake\n\n".getBytes()));
                log.warning("Rejected Connection to: " + clntChan.getRemoteAddress() +
                        " <Bad Handshake>");
            }
        }else{
            read(clntChan);
        }

    }

    public String frameHandshake(ByteBuffer buffer){
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
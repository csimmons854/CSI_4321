package sharon.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

//Download thread to be executed to send files
class Download implements Runnable {
    private Connection clientCon;
    private String dir;
    private Logger log;

    Download(Socket downloadSocket, String directory, Logger logger) throws IOException {
        clientCon = new Connection(downloadSocket);
        dir = directory;
        log = logger;
    }

    public void run() {
        try {
            String fileID = clientCon.getInData().getString();
            //ong fileID = Integer.parseInt(getID) & 0x00000000FFFFFFFFL;
            File newFile = Utilities.findFileByID(fileID,dir);
            FileInputStream fileInputStream;
            int data;
            if(newFile != null) {
                fileInputStream = new FileInputStream(newFile);
                clientCon.getOutData().writeByteArray("OK\n\n".getBytes());
                System.out.println("Sending: " + newFile.getName());
                log.info("Sending: " + newFile.getName());
                while ((data = fileInputStream.read()) >= 0) {
                    clientCon.getClientSocket().getOutputStream().write(data);
                }
                System.out.println(newFile.getName() + " Sent");
                log.info(newFile.getName() + "File Sent");
            }
            else
            {
                clientCon.getOutData().writeByteArray(("ERROR ID (" + fileID + ") not found").getBytes());
            }
            clientCon.getClientSocket().close();
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }
}

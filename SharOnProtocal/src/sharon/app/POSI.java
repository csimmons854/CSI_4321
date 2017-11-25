package sharon.app;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class POSI {
    final private int MAXMVNS = 5;
    final private int NGHBRS = 10;
    final private int RFSH = 10;

    private ArrayList<Socket> mavens = new ArrayList<>();
    private ArrayList<Socket> nodes = new ArrayList<>();

    private void addMaven(Socket newMaven) throws IOException {
        if(mavens.size() <= 5){
            mavens.add(newMaven);
        }
    }

}

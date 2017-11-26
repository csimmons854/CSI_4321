package mvn.app;

import java.io.IOException;

public class SpawnMavens {
    public static void main(String [] args) throws IOException {

        for(int i = 0; i < 5; i++){
            String[] arg = {Integer.toString(9000 + i)};
            Server.main(arg);
        }

    }
}

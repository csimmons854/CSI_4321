/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import java.net.InetAddress;

public class NodeBB {
    public static void main(String [] args) throws Exception {
        InetAddress mavenAddress = InetAddress.getByName(args[0]);
         new NodeAIO(mavenAddress,Integer.parseInt(args[1]),
                Integer.parseInt(args[2]),Integer.parseInt(args[3])).start();
         System.out.println("here");
        new NodeAIO(mavenAddress,Integer.parseInt(args[1]),
                Integer.parseInt(args[2]) + 2,Integer.parseInt(args[3]) + 10).start();

    }
}

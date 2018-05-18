package rcomp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author ANDRE MOREIRA (asc@isep.ipp.pt)
 */
public class HttpServerChat {

    static private String BASE_FOLDER;
    static private ServerSocket sock;
    private static int PORT;

    //UDP
    static private UdpServer udpServer;

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.out.println("Arguments number isn't 2.");
            System.exit(1);
        } else {
            try {
                PORT = Integer.parseInt(args[0]);
                if (PORT < 0 || PORT > 65535) {
                    throw new NumberFormatException();
                }
                BASE_FOLDER = args[1];
                File f = new File(BASE_FOLDER);
                if (f.exists() && f.isDirectory()) {
                    run();
                } else {
                    System.out.println("Second argument isn't a valid directory!");
                    System.exit(1);
                }
            } catch (NumberFormatException ex) {
                System.out.println("First argument isn't a valid port!");
                System.exit(1);
            }
        }
    }
    
    private static void run() throws IOException {
        try {
            sock = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.out.println("Server failed to open local port " + PORT);
            System.exit(1);
        }

        udpServer = new UdpServer(PORT);
        Thread t = new Thread(udpServer);
        t.start();

        System.out.println("Server ready, listening on port number " + PORT);
        Socket cliSock;
        while (true) {
            cliSock = sock.accept();
            HttpChatRequest req = new HttpChatRequest(cliSock, BASE_FOLDER);
            req.start();
        }
    }

    private static final HashMap<String, ArrayList<String>> WALL_LIST = new HashMap<>();

    public static ArrayList<String> getMsg(String wallName) {
        synchronized (WALL_LIST) {
//            System.out.println("\n\n\n" + wallName + "\n\n\n");
            for (Entry<String, ArrayList<String>> e : WALL_LIST.entrySet()) {
                if (e.getKey().equals(wallName)) {
                    if (e.getValue().isEmpty()) {
                        return null;
                    }
                    ArrayList<String> temp = new ArrayList<>(e.getValue());
                    ArrayList<String> returnable = insertCont(temp);
                    WALL_LIST.notifyAll();
                    return returnable;
                }
            }
            WALL_LIST.notifyAll();
            return null;
        }
    }

    private static ArrayList<String> insertCont(ArrayList<String> al) {
        ArrayList<String> returnable = new ArrayList<>();
        for (int i = 0; i < al.size(); i++) {
            returnable.add(String.format("%d - %s", i + 1, al.get(i)));
        }
        return returnable;
    }

    public static void addMsg(String wallName, String content) {
        synchronized (WALL_LIST) {
            for (Entry<String, ArrayList<String>> e : WALL_LIST.entrySet()) {
                if (e.getKey().equals(wallName)) {
                    e.getValue().add(content);
                    return;
                }
            }
            ArrayList<String> content_array = new ArrayList<>();
            content_array.add(content);
            WALL_LIST.put(wallName, content_array);
            WALL_LIST.notifyAll();
        }
    }

    public static boolean delWall(String wallName) {
        synchronized (WALL_LIST) {
            ArrayList<String> returnable = WALL_LIST.remove(wallName);
            WALL_LIST.notifyAll();
            return returnable != null;
        }
    }

    public static void delMsg(String wallName, int messageNumber) {
        synchronized (WALL_LIST) {
            for (Entry<String, ArrayList<String>> e : WALL_LIST.entrySet()) {
                if (e.getKey().equals(wallName)) {
                    if (messageNumber <= e.getValue().size() && messageNumber > 0) {
                        e.getValue().remove(messageNumber - 1);
                        WALL_LIST.notifyAll();
                        return;
                    } else {
                        break;
                    }
                }
            }
            WALL_LIST.notifyAll();
        }
    }

}

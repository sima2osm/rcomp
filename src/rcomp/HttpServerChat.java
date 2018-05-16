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

    static private final String BASE_FOLDER = "www";
    static private ServerSocket sock;
    private static final int PORT = 32107;

    public static void main(String args[]) throws Exception {
        Socket cliSock;

        if (args.length != 0) {
            System.out.println("Arguments number isn't 0.");
            System.exit(1);
        }
        try {
            sock = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.out.println("Server failed to open local port " + PORT);
            System.exit(1);
        }
        System.out.println("Server ready, listening on port number " + PORT);
        while (true) {
            cliSock = sock.accept();
            HttpChatRequest req = new HttpChatRequest(cliSock, BASE_FOLDER);
            req.start();
        }
    }

    // MESSAGES ARE ACCESSED BY THREADS - LOCKING REQUIRED
    private static int nextMsgNum = 0;
    private static final ArrayList<String> MSG_LIST = new ArrayList<>();
    private static final HashMap<String, ArrayList<String>> WALL_LIST = new HashMap<>();

    public static ArrayList<String> getMsgs(String wallName) {
        synchronized (WALL_LIST) {
            for (Entry<String, ArrayList<String>> e : WALL_LIST.entrySet()) {
                if (e.getKey().equals(wallName)) {
                    return e.getValue();
                }
            }
            WALL_LIST.notifyAll();
            return new ArrayList<>();
        }
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

    public static void delWall(String wallName) {
        synchronized (WALL_LIST) {
            WALL_LIST.remove(wallName);
            WALL_LIST.notifyAll();
        }
    }

    public static void delMsg(String wallName, int messageNumber) {
        synchronized (WALL_LIST) {
            for (Entry<String, ArrayList<String>> e : WALL_LIST.entrySet()) {
                if (e.getKey().equals(wallName)) {
                    for (String s : e.getValue()) {
                        String[] split = s.split(" - ");
                        if (Integer.parseInt(split[0]) == messageNumber) {
                            e.getValue().remove(s);
                            break;
                        }
                    }
                    break;
                }
            }
            WALL_LIST.notifyAll();
        }
    }

}

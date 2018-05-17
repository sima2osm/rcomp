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
    private static final int PORT = 32102;

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

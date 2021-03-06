package rcomp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author ANDRE MOREIRA (asc@isep.ipp.pt)
 */
public class HttpChatRequest extends Thread {

    String baseFolder;
    Socket sock;
    DataInputStream inS;
    DataOutputStream outS;

    public HttpChatRequest(Socket s, String f) {
        baseFolder = f;
        sock = s;
    }

    @Override
    public void run() {
        try {
            outS = new DataOutputStream(sock.getOutputStream());
            inS = new DataInputStream(sock.getInputStream());
        } catch (IOException ex) {
            System.out.println("Thread error on data streams creation");
        }
        try {
            HTTPmessage request = new HTTPmessage(inS);
            HTTPmessage response = new HTTPmessage();
            response.setResponseStatus("200 Ok");

            switch (request.getMethod()) {
                case "GET":
                    int numMsgs = 0;
                    if (request.getURI().startsWith("/walls/")) {
                        String wallName = request.getURI().substring(7);
                        if (wallName.equals("") || wallName.contains("/")) {
                            response.setContentFromString(
                                    "ERROR: 405 Method Not Allowed (Wall name cant't be empty)",
                                    "text/plain");
                            response.setResponseStatus("405 Method Not Allowed");
                        } else if (wallName.equals("undefined")) {
                            response.setContentFromString("No messages", "text/plain");
                            response.setResponseStatus("200 Ok");
                        } else {
                            ArrayList<String> msgs = HttpServerChat.getMsg(wallName);
                            // if msgNum doesn't yet exist, the getMsg() method waits
                            // until it does. So the HTTP request was received, but the
                            // HTTP response is sent only when there's a message
                            if (msgs != null) {
                                response.setContentFromStringArray(msgs, "text/plain");
                                response.setResponseStatus("200 Ok");
                            } else {
                                response.setContentFromString("No messages", "text/plain");
                                response.setResponseStatus("200 Ok");
                            }
                        }
                    } else { // NOT GET /walls/ , THEN IT MUST BE A FILE
                        String fullname = baseFolder + "/";
                        if (request.getURI().equals("/")) {
                            fullname = fullname + "index.html";
                        } else {
                            fullname = fullname + request.getURI();
                        }
                        if (!response.setContentFromFile(fullname)) {
                            response.setContentFromString(
                                    "<html><body><h1>404 File not found</h1></body></html>",
                                    "text/html");
                            response.setResponseStatus("404 Not Found");
                        }
                    }   break;
                case "POST":
                    // NOT GET, must be POST
                    if (request.getURI().startsWith("/walls/")) {
                        String wallName = request.getURI().substring(7);
                        if (wallName.equals("") || wallName.contains("/")) {
                            response.setContentFromString(
                                    "<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>",
                                    "text/html");
                            response.setResponseStatus("405 Method Not Allowed");
                        } else {
                            HttpServerChat.addMsg(wallName, request.getContentAsString());
                            response.setResponseStatus("200 Ok");
                        }
                    } else {
                        response.setContentFromString(
                                "<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>",
                                "text/html");
                        response.setResponseStatus("405 Method Not Allowed");
                    }   break;
                case "DELETE":
                    if (request.getURI().startsWith("/walls/")) {
                        String wallNameNumber = request.getURI().substring(7);
                        if (wallNameNumber.equals("")) {
                            response.setContentFromString(
                                    "<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>",
                                    "text/html");
                            response.setResponseStatus("405 Method Not Allowed");
                        } else {
                            if (wallNameNumber.contains("/")) {
                                String[] wallArray = wallNameNumber.split("/");
                                if (wallArray.length != 2) {
                                    response.setContentFromString(
                                            "<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>",
                                            "text/html");
                                    response.setResponseStatus("405 Method Not Allowed");
                                } else {
                                    try {
                                        int messageNumber = Integer.parseInt(wallArray[1]);
                                        HttpServerChat.delMsg(wallArray[0], messageNumber);
                                    } catch (NumberFormatException ex) {
                                        System.out.println("Parsing error!");
                                    }
                                }
                            } else {
                                if (!HttpServerChat.delWall(wallNameNumber)) {
                                    response.setContentFromString(
                                            "<html><body><h1>ERROR: 405 Method Not Allowed</h1></body></html>",
                                            "text/html");
                                    response.setResponseStatus("405 Method Not Allowed");
                                }
                            }
                        }
                    }   break;
                default:
                    break;
            }
            response.send(outS); // SEND THE HTTP RESPONSE
        } catch (IOException ex) {
            System.out.println("Thread I/O error on request/response");
        }
        try {
            sock.close();
        } catch (IOException ex) {
            System.out.println("CLOSE IOException");
        }
    }
}

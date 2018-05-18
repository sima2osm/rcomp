/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcomp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simao
 */
public class UdpServer extends Thread {

    private final int PORT;
    private static final int BUFFER = 1024;

    public UdpServer(int port) {
        PORT = port;
    }

    @Override
    public void start() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[BUFFER];
            byte[] sendData = new byte[BUFFER];
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                InetAddress ipAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String[] sentenceArray = sentence.split("/");
                HttpServerChat.addMsg(sentenceArray[0], sentenceArray[1]);
                String toSend = "Message posted successfully!";
                sendData = toSend.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
                serverSocket.send(sendPacket);
            }
        } catch (SocketException ex) {
            Logger.getLogger(UdpServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UdpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

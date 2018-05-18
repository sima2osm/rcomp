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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simao
 */
public class UdpClient {
    
    private static final int BUFFER = 1024;
    
    public static void main(String[] args) throws SocketException, UnknownHostException {
        if (args.length < 4) {
            System.out.println("Arguments number is less than 4.");
            System.exit(1);
        }
        
        DatagramSocket clientSocket = new DatagramSocket();
        try {
            InetAddress ipAddress = InetAddress.getByName(args[1]);
            try {
                int port = Integer.parseInt(args[0]);
                if (port < 0 || port > 65535) {
                    throw new NumberFormatException();
                }
                byte[] sendData = new byte[BUFFER];
                byte[] receiveData = new byte[BUFFER];
                String sentence = "";
                for (int i = 3; i < args.length; i++) {
                    sentence += args[i];
                }
                sendData = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String received = new String(receivePacket.getData());
                System.out.println(received);
                clientSocket.close();
            } catch (NumberFormatException ex) {
                System.out.println("First argument isn't a valid port!");
                System.exit(1);
            } catch (IOException ex) {
                Logger.getLogger(UdpClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnknownHostException ex) {
            System.out.println("Second argument isn't a valid IP address!");
            System.exit(1);
        }
    }
}

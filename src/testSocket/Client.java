package testSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(System.in)); // For at kunne modtage input fra tastaturet.

        Socket clientSocket = new Socket("localhost", 6789);

        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream()); // For at kunne skrive til serveren.
        System.out.println("Indtast et ord");

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // For at kunne læse fra serveren.

        while (!inFromClient.equals("stop")) {
            
        }

        clientSocket.close();



    }
}

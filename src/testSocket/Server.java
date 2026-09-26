package testSocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("testSocket.Server venter på klient...");

        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Ny klient forbundet til serveren");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);

        String clientSentence = in.readLine();
        System.out.println(clientSentence);


    }
}

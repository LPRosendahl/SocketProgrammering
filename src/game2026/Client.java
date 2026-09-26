package game2026;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GUI gui;

    public Client(String host, int port, GUI gui, String playerName) {
        this.gui = gui;
        try {
            Socket socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Send tilslutningsbesked med navnet
            out.writeObject("JOIN:" + playerName);
            out.flush();

            // Opgave 3: Lytter kontinuerligt på baggrundstråden
            new Thread(this::listen).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int deltaX, int deltaY, String direction) {
        try {
            out.writeObject("MOVE:" + deltaX + ":" + deltaY + ":" + direction);
            out.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void listen() {
        try {
            while (true) {
                // Modtag listen over spillere fra serveren
                List<Player> players = (List<Player>) in.readObject();

                // Opdater GUI på JavaFX-tråden
                Platform.runLater(() -> gui.updateBoard(players));
            }
        }catch (Exception e) {
            System.out.println("Mistede forbindelsen til serveren.");
        }
    }
}

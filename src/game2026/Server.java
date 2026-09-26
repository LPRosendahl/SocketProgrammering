package game2026;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<Player> players = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(6789)) {
            System.out.println("Server venter på klient...");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }


        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void movePlayer(Player player, int deltaX, int deltaY, String direction) {
        player.setDirection(direction);
        int newX = player.getXpos() + deltaX;
        int newY = player.getYpos() + deltaY;

        if (GUI.board[newY].charAt(newX) == 'w') {
            player.addPoints(-1);
        } else {
            Player other = getPlayerAt(newX, newY);
            // Hvis der står en anden spiller (sammenlign simpel tekst / navn
            if (other != null && !other.name.equals(player.name)) {
                player.addPoints(10);
                other.addPoints(-10);
            } else {
                player.addPoints(1);
                player.setXpos(newX);
                player.setYpos(newY);
            }
        }
        sendStateToAll();
    }

    private static Player getPlayerAt(int x, int y) {
        for (Player p : players) {
            if (p.getXpos() == x && p.getYpos() == y) return p;
        }
        return null;
    }

    public static void sendStateToAll() {
        for (ClientHandler c : clients) {
            c.sendState(players);
        }
    }

    private static int[] getRandomStartPosition() {
        Random r = new Random();
        while (true) {
            int x = r.nextInt(20);
            int y = r.nextInt(20);
            if (GUI.board[y].charAt(x) == ' ' && getPlayerAt(x, y) == null) {
                return new int[]{x, y};
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private Player player;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;

            try {
                this.out = new ObjectOutputStream(socket.getOutputStream());
                this.out.flush();
                this.in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                // Modtag første besked med spillernavnet
                String firstMessage = (String) in.readObject();
                if (firstMessage.startsWith("JOIN")) {
                    String name = firstMessage.split(":")[1];
                    int[] pos = getRandomStartPosition();
                    player = new Player(name, pos[0], pos[1], "up");

                    synchronized (Server.class) {
                        players.add(player);
                    }
                    sendStateToAll();
                }

                while (true) {
                    String message = (String) in.readObject();
                    if (message.startsWith("MOVE")) {
                        String[] parts = message.split(":");
                        int deltaX = Integer.parseInt(parts[1]);
                        int deltaY = Integer.parseInt(parts[2]);
                        String direction = parts[3];

                        Server.movePlayer(player, deltaX, deltaY, direction);
                    }
                }

            } catch (Exception e) {
                if (player != null) {
                    System.out.println(player.name + " forlod spillet.");
                } else {
                    System.out.println("En klient forlod spillet.");
                }
                synchronized (Server.class) {
                    clients.remove(this);
                    if (player != null) players.remove(player);
                }
                sendStateToAll();
            }
        }

        public void sendState(List<Player> platerList) {
            try {
                if (out != null) {
                    out.reset();
                    out.writeObject(new ArrayList<>(platerList));
                    out.flush();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final ServerSocket server;
    private static final Map<Client, PrintWriter> cliens = new HashMap<>();
    Utility utility = new Utility();
    int namderOfClient = 1;
    static String path = "src/main/java/org/example/dialog1.txt";
    File file = new File(path);
    LocalTime timeStart = LocalTime.now();

    public Server(int port) throws IOException {  // created new class ServerSocket; need int nunber of port;
        this.server = new ServerSocket(port);
    }

    public void startServer() {   // create a new thread for each client to allow the server to handle multiple clients;
        new Thread(serverController()).start();
    }

    private Runnable serverController() {
        return () -> {
            while (true) {
                try {
                    Socket socket = server.accept();        // wait for client...;

                    int port = socket.getPort();            // take the port of client who has connected;
                    timeStart = LocalTime.now();  // this current time of stard connection;

                    Client newClient = utility.addNewClient(port, namderOfClient, timeStart, socket);

                    cliens.put(newClient, new PrintWriter(socket.getOutputStream(), true));  // put to the collection

                    String response = newClient.getClientName() + " has successfully connected in " + timeStart + "; Port is " + port; // to create a memmage about new client;

                    utility.messageAll(response, cliens, newClient.getClientName()); // to send a memmage about new client to all clients;
                    System.out.println(response); // to print a memmage in server about new client to all clients;
                    clientConversation(socket, newClient.getClientName());  // start a caoversation with client
                    namderOfClient++; // to change id number of every new client

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
    }

    private void clientConversation(Socket socket, String name) {

        new Thread(() -> {
            try {
                BufferedReader bufferedReader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()));

                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream()));

                String word = "";
                while ((word = bufferedReader.readLine()) != null) {
                    utility.writeToFile(file, word, name);
                    System.out.println(name + ": " + word);
                    utility.messageAll(word, cliens, name);


                    if (word.equals("exit")) {
                        LocalTime timeEnd = LocalTime.now();
                        String exitResponce = (name + " left the session." + "time of dialog with client was: " +
                                timeStart.until(timeEnd, ChronoUnit.SECONDS) + "sec");
                        System.out.println(exitResponce);
                        utility.messageAll(exitResponce, cliens, name);
                        utility.writeToFile(file, word, name);
                        break;
                    }
                    if (word.equals("file")) {
                        try {
                            bufferedReader = utility.reader(path);
                            String stopWord = "file";
                            while ((bufferedReader.readLine()) != stopWord){
                                writer.write(word);
                                writer.newLine();
                                writer.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}




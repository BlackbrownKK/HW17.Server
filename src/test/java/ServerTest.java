import org.example.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

public class ServerTest {
    private Server server;

    @Before
    public void startServer() throws Exception {
        server = new Server(8085);
        server.startServer();

    }

    public void client1() throws IOException {
        Socket client1 = new Socket("127.0.0.1", 8085);  // created a new client to connect by Server

        BufferedWriter writer =
                new BufferedWriter(
                        new OutputStreamWriter(
                                client1.getOutputStream()));

        String message = "Odessa";
        writer.write(message);
        client1.close();
    }

    @Test
    public void client2() throws IOException {
        Socket client2 = new Socket("127.0.0.1", 8085);  // created a new client to connect by Server

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                client2.getInputStream()));

        boolean res = contains(String.valueOf(reader), "Odessa");
        Assert.assertFalse(res);
        client2.close();
    }

    public static boolean contains(String pattern, String content) {
        return content.matches(pattern);
    }
}
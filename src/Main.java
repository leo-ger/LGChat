import Client.Client;
import Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
PROTOCOL-SYNTAX IS: 4 bytes recipient, 4 bytes sender, ... bytes data
 */

public class Main {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> { Server server = new Server(4567, 10);
            server.run();});
        thread1.start();


        Client client1 = new Client("localhost", 4567, System.out);
        Client client2 = new Client("localhost", 4567, System.out);

        try {
            // TODO: Make client connect to server
            client1.connect();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.getCause().printStackTrace();
            return;
        }

        try {
            // TODO: Make client connect to server
            client2.connect();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.getCause().printStackTrace();
            return;
        }

        client1.startChat(2, 1, System.in);

        /* BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while(client2.isConnected()) {
            try {
                if(inputReader.ready()) {
                    String message = inputReader.readLine();
                    if(message.equals("exit")) {
                        client1.disconnect();
                    }
                    else {
                        try {
                            client2.send(1, 3, message);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            e.getCause().printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not read input: " + e.getMessage());
            }
        }
        */
        System.out.println("Chat ended");
    }
}

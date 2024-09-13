import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Magic8BallClient {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Magic8BallClient <host name> <port number> <question>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String question = args[2];

        try (
            Socket magicSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(magicSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(magicSocket.getInputStream()));
        ) {
            // Altough not required, i decided to send the question to the server.
            out.println(question);
            String response = in.readLine();
            System.out.println("Magic 8 Ball says: " + response);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
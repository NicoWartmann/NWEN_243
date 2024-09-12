package Project_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.management.ManagementFactory;
import java.util.Random;

public class Magic8BallServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Magic8BallServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        Random random = new Random();
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        long pid = Long.parseLong(jvmName.split("@")[0]);
        
        String[] answers = {
            // I made ChatGPT generate these.
            "It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes - definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Yes.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful."
        };
        

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            // This print does not completely match the output in the Documentation but I wanted it to include the portNumber.
            System.out.println("Magic 8 Ball Server is running on port " + portNumber + " with PID: " + pid);

            while (true) {
                try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    // Altough the documentation says that the question is optional, the client sends it anyway and I print it here because it seems reasonable.
                    String question = in.readLine(); 
                    System.out.println("Received question: " + question);
                    
                    String answer = answers[random.nextInt(answers.length)];
                    out.println(answer + " (" + InetAddress.getLocalHost().getHostAddress() +")");
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(1);
        }
    }
}

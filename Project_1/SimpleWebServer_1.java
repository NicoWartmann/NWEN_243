package Project_1;

import java.io.*;
import java.net.*;

public class SimpleWebServer_1 {
    public static void main(String[] args) {
        int port = 8080;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server running at http://localhost:" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        PrintWriter out = new PrintWriter(outputStream, true);

        String realClientIP = getHeader(clientSocket, "X-Real-IP");
        String location = getLocation(realClientIP);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello, my name is Nico Wartmann!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Hello, my name is Nico Wartmann!</h1>");
        out.println("<p>Your Public IP is: " + (realClientIP != null ? realClientIP : "Unavailable") + "</p>");
        out.println("<p>Your Location: " + (location != null ? location : "Could not determine location") + "</p>");
        out.println("</body>");
        out.println("</html>");

        out.close();
    }

    private static String getHeader(Socket clientSocket, String headerName) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith(headerName + ":")) {
                return line.substring(headerName.length() + 1).trim();
            }
            if (line.isEmpty()) {
                break;
            }
        }
        return null;
    }

    private static String getLocation(String ip) throws IOException {
        if (ip == null) return "IP Address Not Provided";
    
        try (Socket socket = new Socket(InetAddress.getByName("ipinfo.io"), 80)) {
            PrintWriter request = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
            request.println("GET /" + ip + "/json HTTP/1.1");
            request.println("Host: ipinfo.io");
            request.println("Connection: close");
            request.println();
    
            StringBuilder responseBody = new StringBuilder();
            boolean inContent = false;
            String line;
            while ((line = response.readLine()) != null) {
                if (line.isEmpty() && !inContent) {
                    inContent = true;
                } else if (inContent) {
                    responseBody.append(line);
                }
            }
        
            // I had a lot of issues with getting Regex and parsing to work so this is kinda wonky now, but it works
            // in this case I used some help of ChatGPT to resolve the problems (note to prevent plagiarism) this did ultimately not help and i figured it out with debugging outputs to the console
            String key = "\"city\"";
            int keyIndex = responseBody.indexOf(key);
            if (keyIndex != -1) {
                int colonIndex = responseBody.indexOf(":", keyIndex + key.length());
                if (colonIndex != -1) {
                    int startQuoteIndex = responseBody.indexOf("\"", colonIndex + 1);
                    if (startQuoteIndex != -1) {
                        int start = startQuoteIndex + 1;
                        int end = responseBody.indexOf("\"", start);
                        if (end != -1) {
                            return responseBody.substring(start, end);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching location";
        }
        return "Location data not found";
    }    
}

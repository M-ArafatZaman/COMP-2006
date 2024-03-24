/**
 * Using BetterServer.java as a basis
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static String output = "";
    private static String eor = "[EOR]"; // a code for end-of-response
    private static final String USERNAME = "Daisy";
    private static final String PASSWORD = "moo";
    private static final int MAX_ATTEMPTS = 5;
    private static Boolean loggedIn = false;
    private static int attempts = 0;
    
    // establishing a connection
    private static void setup() throws IOException {
        
        serverSocket = new ServerSocket(0);
        toConsole("Server port is " + serverSocket.getLocalPort());
        
        clientSocket = serverSocket.accept();

        // get the input stream and attach to a buffered reader
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // get the output stream and attach to a printwriter
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        toConsole("Accepted connection from "
                 + clientSocket.getInetAddress() + " at port "
                 + clientSocket.getPort());
            
        sendGreeting();
    }
    
    // the initial message sent from server to client
    private static void sendGreeting()
    {
        appendOutput("Welcome to MooNet!\n");
        //sendOutput();
    }
    
    // what happens while client and server are connected
    private static void talk() throws IOException {
        /* placing echo functionality into a separate private method allows it to be easily swapped for a different behaviour */
        //echoClient();
        login();
        disconnect();
    }

    /*
     * Repeatedly take inputs until client logs in
     */
    private static void login() throws IOException {
        String inputline;
        Boolean username = false;
        Boolean password = false;
        while (!loggedIn && attempts < 5) {
            // Send request to get username
            appendOutput("Enter username:");
            toConsole("Username requested");
            sendOutput();
            attempts++;
            inputline = in.readLine();
            // Received input
            toConsole("Name entered: "+inputline);
            if (inputline.equals(USERNAME)) {
                username = true;
            }
            

            if (!username) continue;

            // Send request to get password
            appendOutput("Enter password:");
            toConsole("Password requested");
            sendOutput();
            attempts++;
            inputline = in.readLine();
            // Received input
            toConsole("Name entered: "+inputline);
            if (inputline.equals(PASSWORD)) {
                password = true;
            }

            if (password && username) {
                loggedIn = true;
            } else {
                password = false;
                username = false;
            }
        }

        if (attempts >= 5 && !loggedIn) {
            appendOutput("Sorry, maximum attempt limit exceeded! Aborted session.");
            sendOutput();
            disconnect();
        }
    }
    
    // repeatedly take input from client and send back in upper case
    private static void echoClient() throws IOException
    {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            appendOutput(inputLine.toUpperCase());
            sendOutput();
            toConsole(inputLine);
        }
    }
    
    private static void disconnect() throws IOException {
        out.close();
        toConsole("Disconnected.");
        System.exit(0);
    }
    
    // add a line to the next message to be sent to the client
    private static void appendOutput(String line) {
        output += line + "\r";
    }
    
    // send next message to client
    private static void sendOutput() {
        out.println( output + "[EOR]");
        out.flush();
        output = "";
    }
    
    // because it makes life easier!
    private static void toConsole(String message) {
        System.out.println(message);
    }
    
    public static void main(String[] args) {
        try {
            setup();
            talk();
        }
        catch( IOException ioex ) {
            toConsole("Error: " + ioex );
        }
    }
}

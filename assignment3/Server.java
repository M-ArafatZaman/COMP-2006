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
    private static Boolean isGameOver = false; 
    private static char[] code = {'1', '0', '5', '8'};
    
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
        // Login is successful!
        appendOutput("Welcome Daisy!");
        appendOutput("Let's play Bulls and Cows!");
        appendOutput("Guess my 4 digit code.");
        playGuess();
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
            username = requestUsername();
            if (!username) continue;
            password = requestPassword();

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

    private static Boolean requestUsername() throws IOException {
        appendOutput("Enter username:");
        toConsole("Username requested");
        sendOutput();
        attempts++;
        String inputline = in.readLine();
        // Received input
        toConsole("Name entered: "+inputline);
        if (inputline.equals(USERNAME)) {
            return true;
        }
        return false;
    }

    private static Boolean requestPassword() throws IOException {
        // Send request to get password
        appendOutput("Enter password:");
        toConsole("Password requested");
        sendOutput();
        attempts++;
        String inputline = in.readLine();
        // Received input
        toConsole("Name entered: "+inputline);
        if (inputline.equals(PASSWORD)) {
            return true;
        }
        return false;
    }

    private static void playGuess() throws IOException {
        appendOutput("Enter 4 digits, separated by spaces:");
        sendOutput();
        String inputline;
        while (!isGameOver && (inputline = in.readLine()) != null) {
            inputline = inputline.trim();
            toConsole("Guessed: "+inputline);
            if (isGuessValid(inputline)) {
                int[] bullsCows = getBullsAndCows(inputline);
                if (bullsCows[0] == 4) {
                    toConsole("Correct!");
                    appendOutput("You got it - goodbye!");
                    sendOutput();
                    disconnect();
                } else {
                    toConsole("Incorrect!");
                    appendOutput(""+bullsCows[0]+" bull and "+bullsCows[1]+" cow.");
                    sendOutput();
                }
            } else {
                appendOutput("Oops! You need to enter 4 digits separated by spaces.");
                appendOutput("Try again:");
                sendOutput();
            }
        }
    }

    private static Boolean isGuessValid(String guess) {
        if (guess.length() != 7) return false;
        for (int i = 0; i < guess.length(); i++) {
            if (i % 2 == 1) {
                if (guess.charAt(i) != ' ') return false;
            } else {
                if ((guess.charAt(i) - '0') < 0 || (guess.charAt(i) - '0') > 9 ) return false;
            }
        }
        return true;
    }

    private static int[] getBullsAndCows(String guess) {
        // TODO
        int bulls = 0, cows = 0;
        char[] guessChars = new char[4];
        int curr = 0;
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == ' ') continue;
            guessChars[curr] = guess.charAt(i);
            curr++;
        }

        for (int i = 0; i < guessChars.length; i++) {
            if (code[i] == guessChars[i]) {
                guessChars[i] = '-';
                bulls++;
            }
        }

        for (int i = 0; i < guessChars.length; i++) {
            for (int j = 0; j < code.length; j++) {
                if (guessChars[i] == code[j]) {
                    guessChars[i] = '-';
                    cows++;
                    break;
                }
            }
        }

        int[] res = {bulls, cows}; 

        return res;
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

package src.utilities;
import src.exceptions.ProtocolException;

import java.io.*;

/**
 * Manages terminal input and output streams with diagnostic
 * features to assist with testing and debugging.  Multiple
 * instances are allowed.  Each client class can create its own
 * stream manager that will be labelled automatically with the
 * class name.
*/

public class TerminalStream
{
    private static boolean debug = true;     // on by default, see method setDebug
    private static int classWidth = 0;       // class name field width (0 = no padding)
    private static String separator = " : "; // between class field and message ("" = none)
    private static BufferedReader input = null; // input stream shared by all instances
    private static String stop = null;        // user enters this to signal end

    private String className;                // name of class using this instance


    /**
     * Create and initialize new test.TerminalStream.
     * @param className name of class using this instance (must not be null)
     * @throws ProtocolException in the event of an error
     */

    public TerminalStream(String className) throws ProtocolException
    {
        this.className = className;

        // Create input stream if not yet initialized
        // One input stream shared by all instances of terminal manager

        if (input == null) {
            try {
                input = new BufferedReader(new InputStreamReader(System.in));
            }
            catch (Exception e) {
                throw new ProtocolException("input stream constructor failed : " + e.getMessage());
            }
        }
    }

    public static void setDebug(boolean debug)
    {
        TerminalStream.debug = debug;
    }

    /**
     * Set field width for class name.
     * @param classWidth width (0 = don't pad the field)
     */

    public static void setClassWidth(int classWidth)
    {
        TerminalStream.classWidth = classWidth;
    }

    /**
     * Set stop string.
     * @param stop string used to signal end of input stream (null = disable)
     */

    public static void setStop(String stop)
    {
        TerminalStream.stop = stop;
    }

    /**
     * Output raw text to terminal and terminate line.
     * Characters are passed to terminal unchanged
     * @param text text to be output
     */

    public void printlnRaw(String text)
    {
        System.out.println(text);
    }

    /**
     * Output raw text to terminal without terminating line.
     * Characters are passed to terminal unchanged
     * @param text text to be output
     */

    public void printRaw(String text)
    {
        System.out.print(text);
        System.out.flush();  // force flush to ensure shown immediately
    }

    /**
     * Output diagnostic message to terminal - flexible version.
     * For internal use only
     * No action if debug mode disabled (unless force == true)
     * Non-printable characters will be expanded to make them visible
     * @param message message to be output
     * @param force true = output even if debug mode disabled
     * @param terminate true = terminate line (output \n at end)
     */

    private void printDiag(String message, boolean force, boolean terminate)
    {
        if (debug || force) {

            // Show name of class that output this message

            System.out.print(className);
            if (className.length() < classWidth)
                for (int i = className.length(); i < classWidth; i++)
                    System.out.print(" ");
            System.out.print(separator);

            // Output message

            for (int i = 0; i < message.length(); i++) {
                char ch = message.charAt(i);                // extract next char

                if (ch >= 32 && ch <= 255)                  // printable?
                    System.out.print(ch);                   // yes - output char
                else                                        // no - output code in hex
                    System.out.printf("\\u%04x", (int) ch);
            }

            // Terminate line
            // If no terminator, force flush to ensure text displayed immediately

            if (terminate)
                System.out.println();
            else
                System.out.flush();
        }
    }

    /**
     * Output diagnostic message and terminate line.
     * No action if debug mode disabled
     * Non-printable characters will be expanded to make them visible
     * @param message message to be displayed
     */

    public void printlnDiag(String message)
    {
        printDiag(message, false, true);
    }

    /**
     * Output line terminator for diagnostic messages.
     * The src.main purpose of this method is to output blank lines
     * No action if debug mode disabled
     */

    public void printlnDiag()
    {
        if (debug)
            System.out.println();
    }

    /**
     * Output diagnostic message without terminating line.
     * No action if debug mode disabled
     * Non-printable characters will be expanded to make them visible
     * @param message message to be displayed
     */

    public void printDiag(String message)
    {
        printDiag(message, false, false);
    }

    /**
     * Output error message and terminate line.
     * Force output even if debug mode disabled
     * Non-printable characters will be expanded to make them visible
     * @param message message to be displayed
     */

    public void printlnError(String message)
    {
        printDiag(message, true, true);
    }

    /**
     * Output either diagnostic or raw message depending on debug mode.
     * If debug mode enabled then output diagnostic message and expand
     * non-printable characters to make them visible.
     * If debug mode disabled then output raw message without expanding
     * non-printable characters.
     * In both cases terminate line.
     * @param diagMessage message to be displayed if debug mode enabled
     * @param rawMessage message to be displayed if debug mode disabled
     */

    public void printlnDiagOrRaw(String diagMessage, String rawMessage)
    {
        if (debug)
            printlnDiag(diagMessage);
        else
            printlnRaw(rawMessage);
    }

    /**
     * Read line from standard input.
     * End of input stream is detected when either the standard input
     * stream signals this or if the user enters a string that exactly
     * matches the stop string (unless latter set to null to disable it)
     * @return line or null if end of input stream reached
     * @throws ProtocolException in the event of an error
     */

    public String readLine() throws ProtocolException
    {
        // Read line from standard input and trap errors
        // in.readline returns null if end of input stream detected

        String line;

        try
        {
            line = input.readLine();
        }
        catch (Exception e)
        {
            throw new ProtocolException("readLine() failed : " + e.getMessage());
        }

        // Check for input matching the stop string
        // Return null to signal end of stream

        if (line != null && line.equals(stop))
            line = null;

        return line;
    }

} // end of class test.TerminalStream


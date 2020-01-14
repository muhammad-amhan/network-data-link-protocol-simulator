package src.main.datalink;

import src.main.resources.MessageReceiver;
import src.utilities.Usage;
import src.utilities.TerminalStream;
import java.util.regex.Matcher;

public class Receiver {


    private static final int defaultMtu = 20;   // default value for MTU
    private static final String stop = ".";     // user enters this to quit (null = disable)

    /**
     * Run test (version with all options)
     * @param mtu the maximum frame length permitted by the data link protocol
     * @param debug true = enable debug mode, false = disable
     * @throws Exception if unexpected error occurs
     */

    public static void run(int mtu, boolean debug) throws Exception
    {
        TerminalStream terminal = new TerminalStream("src.main.datalink.Receiver");
        TerminalStream.setDebug(debug);
        TerminalStream.setClassWidth("src.main.resources.MessageReceiver".length()); // Set field width to length of longest class name
        TerminalStream.setStop(stop);

        // Create data link layer message receiver (which also creates physical layer)

        terminal.printlnDiag("test rig starting (mtu = " + mtu + ", debug = " + debug + ")");
        MessageReceiver dataLinkLayer = new MessageReceiver(mtu);

        // Give instructions on how to stop the test

        terminal.printlnDiag("frame entry loop starting");
        terminal.printlnDiag("enter one frame per line (no \"quotes\" required)");
        terminal.printlnDiag("enter \"" + stop + "\" to stop test");

        // Repeats until end of input stream reached - that is "." entered by the user
        while (true) {

            terminal.printlnDiag();
            terminal.printlnDiag("calling receiveMessage...");
            String message;

            try
            {
                message = dataLinkLayer.receiveMessage();
            }
            catch (Exception e)
            {
                terminal.printlnError("receiveMessage threw an exception \"" + e.getMessage() + "\"");
                break;
            }

            // Break out of loop if end of stream reached
            if (message == null)
            {
                terminal.printlnDiag("end of input stream reached");
                break;
            }

            // If debug mode enabled then output full diagnostic message
            // If debug mode disabled then just output raw message string

            terminal.printlnDiagOrRaw("message received = \"" + message + "\"", message);

        }

        // Receiver ended normally
        terminal.printlnDiag();
        terminal.printlnDiag("test rig finished");

    }

    /**
     * Main method used when the program is executed from a command line.
     * @param args the command line arguments
     * @throws Exception if unexpected error occurs
     */

    public static void main(String[] args) throws Exception
    {

        int mtu = defaultMtu;              // maximum transfer unit (frame length limit)
        boolean debug = true;              // enable by default
        Matcher mtuMatcher;
        Matcher debugMatcher;

        // Parse command line options
        for (String arg : args) {

            mtuMatcher = Usage.COMPILED_MTU_COMMAND_LINE_ARG.matcher(arg);
            debugMatcher = Usage.COMPILED_DEBUG_COMMAND_LINE_ARG.matcher(arg);

            if (debugMatcher.find()) {
                try
                {
                    debug = Boolean.parseBoolean(debugMatcher.group("debug"));
                }
                catch (Exception e)
                {
                    Usage.usageErrorExit("Bad or missing 'debug' value on command line", defaultMtu);
                }
            }

            else if (mtuMatcher.find())
            {
                try
                {
                    mtu = Integer.parseInt(mtuMatcher.group("mtu"));
                }
                catch (Exception e)
                {
                    Usage.usageErrorExit("Bad or missing MTU value on command line", defaultMtu);
                }
            }

            // Abort program if unrecognised argument found
            else
            {
                Usage.usageErrorExit("Unrecognised command line option " + arg, defaultMtu);
            }
        }

        // Run Receiver with options specified
        run(mtu, debug);
    }
}

package src.main.datalink;

import src.main.resources.MessageSender;
import src.utilities.TerminalStream;
import src.utilities.Usage;
import java.util.regex.Matcher;

public class Sender {
    private static final int defaultMtu = 12;   // default value for MTU
    private static final String stop = ".";     // user enters this to quit (null = disable)

    /**
     * Run test (version with all options)
     * @param mtu the maximum frame length permitted by the data link protocol
     * @param debug true = enable debug mode, false = disable
     * @throws Exception if unexpected error occurs
     */

    public static void run(int mtu, boolean debug) throws Exception
    {
        TerminalStream terminal = new TerminalStream("src.main.datalink.Sender");
        TerminalStream.setDebug(debug);
        TerminalStream.setClassWidth("src.main.resources.MessageSender".length());
        TerminalStream.setStop(stop);

        terminal.printlnDiag("test rig starting (mtu = " + mtu + ", debug = " + debug + ")");
        MessageSender dataLinkLayer = new MessageSender(mtu);

        terminal.printlnDiag("message entry loop starting");
        terminal.printlnDiag("enter one message per line (no \"quotes\" required)");
        terminal.printlnDiag("enter \"" + stop + "\" to stop test");

        while (true) {

            terminal.printlnDiag();
            terminal.printDiag("enter message > ");
            String message = terminal.readLine();

            if (message == null) {
                terminal.printlnDiag("end of input stream reached");
                break;
            }

            // Pass message to data link message sender
            // Trap any exception so can report before terminating program

            try
            {
                terminal.printlnDiag("calling sendMessage...");
                dataLinkLayer.sendMessage(message);
                terminal.printlnDiag("sendMessage returned normally");
            }
            catch (Exception e)
            {
                terminal.printlnError("sendMessage threw an exception \"" + e.getMessage() + "\"");
                break;
            }

        }

        // Sender process ended normally
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

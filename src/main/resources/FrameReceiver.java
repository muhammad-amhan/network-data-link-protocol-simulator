package src.main.resources;

import src.utilities.TerminalStream;
import src.exceptions.ProtocolException;


public class FrameReceiver
{
    private TerminalStream terminal;

    /**
     * @throws ProtocolException if error detected
     */

    public FrameReceiver() throws ProtocolException
    {
        this.terminal = new TerminalStream("src.main.resources.FrameReceiver");
        terminal.printlnDiag("physical layer ready");
    }

    public String receiveFrame() throws ProtocolException
    {
        // Prompt for next frame
        terminal.printlnDiag("    receiveFrame starting");
        terminal.printDiag("    enter frame > ");

        // Read frame
        // (terminal.readLine handles stop string)

        String frame = terminal.readLine();

        // Report outcome and return frame
        // End of stream signalled by readLine returning null

        if (frame == null)
            terminal.printlnDiag("    receiveFrame returning null (end of input stream)");
        else
            terminal.printlnDiag("    receiveFrame returning \"" + frame + "\"");
        return frame;
    }

}

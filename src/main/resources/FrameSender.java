package src.main.resources;

import src.utilities.TerminalStream;
import src.exceptions.ProtocolException;


public class FrameSender {
    private final TerminalStream terminal;

    /**
     * @throws ProtocolException if error detected
     */
    public FrameSender() throws ProtocolException {
        this.terminal = new TerminalStream("src.main.resources.FrameSender");
        terminal.printlnDiag("physical layer ready");
    }

    /**
     * Send a single frame.
     * If a message is split across several frames this method must be
     * called separately for each frame in turn.
     *
     * @param frame the frame to be sent.  There should be no extraneous
     *              leading or trailing characters
     */
    public void sendFrame(String frame) {
        // If debug mode enabled then output full diagnostic message
        // If debug mode disabled then just output raw frame
        terminal.printlnDiagOrRaw("    sendFrame called (frame = \"" + frame + "\")", frame);
    }
}

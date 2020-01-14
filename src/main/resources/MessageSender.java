package src.main.resources;

import src.utilities.TerminalStream;
import java.util.ArrayList;
import src.exceptions.ProtocolException;


public class MessageSender
{

    private int mtu;                    // maximum transfer unit (frame length limit)
    private FrameSender physicalLayer;  // physical layer object
    private TerminalStream terminal;    // terminal stream manager

    private static final int MAXIMUM_DATA_LENGTH_ALLOWED = 99;
    private static final int FRAME_DELIMITERS_LENGTH = 10;

    /**
     * src.main.resources.MessageSender constructor - DO NOT ALTER ANY PART OF THIS
     * Create and initialize new src.main.resources.MessageSender.
     * @param mtu the maximum transfer unit (MTU)
     * (the length of a frame must not exceed the MTU)
     * @throws ProtocolException if error detected
     */

    public MessageSender(int mtu) throws ProtocolException
    {
        // Create physical layer and terminal stream manager

        this.mtu = mtu;
        this.physicalLayer = new FrameSender();
        this.terminal = new TerminalStream("src.main.resources.MessageSender");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }


    /**
     * @param message the message to be sent. The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if, and only if, the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol (including the MTU)
     */

    public void sendMessage(String message) throws ProtocolException
    {
        terminal.printlnDiag("  sendMessage starting (message = \"" + message + "\")");

        ArrayList<String> frames = constructFrames(message);

        for (String frame : frames) {
            physicalLayer.sendFrame(frame);
        }

        terminal.printlnDiag("  sendMessage finished");
    }

    private String dataLength(String message)
    {
        if (message.length() < 10) {
            return String.format("0%d", message.length());
        }
        return String.valueOf(message.length());
    }

    private String calculateChecksum(String frame)
    {
        int ascii = 0;

        for (int j = 0; j < frame.length(); j++)
        {
            char character = frame.charAt(j);
            ascii = ascii + (int) character;
        }

        int checksum = ascii % 100;
        if (checksum < 10) return String.format("0%d", checksum);

        return String.valueOf(checksum);
    }

    private ArrayList<String> splitMessage(String message) throws ProtocolException
    {
        if ((this.mtu < FRAME_DELIMITERS_LENGTH) || (this.mtu == FRAME_DELIMITERS_LENGTH && message.length() != 0))
        {
            throw new ProtocolException("MTU Value Error (\"" + this.mtu + "\"): MTU should be greater than 10 if it includes data, otherwise 10 is enough for empty frames.");
        }

        int dataLengthAllowed = mtu - FRAME_DELIMITERS_LENGTH;
        ArrayList<String> splitMessages = new ArrayList<>();

        if (this.mtu > MAXIMUM_DATA_LENGTH_ALLOWED + FRAME_DELIMITERS_LENGTH) {
            dataLengthAllowed = MAXIMUM_DATA_LENGTH_ALLOWED;
        }

        for (int i = 0; i < message.length(); i += dataLengthAllowed)
        {
            splitMessages.add(message.substring(i, Math.min(i + dataLengthAllowed, message.length())));
        }
        return splitMessages;
    }

    private ArrayList<String> constructFrames(String message) throws ProtocolException
    {
        ArrayList<String> frames;

        frames = splitMessage(message);
        if (frames.size() == 0) frames.add("");

        for (int i = 0; i < frames.size(); i++)
        {
            if (i == frames.size() - 1) {
                frames.set(i, String.format("%s-%s-%s-", 'E', dataLength(frames.get(i)), frames.get(i)));
            }
            else {
                frames.set(i, String.format("%s-%s-%s-", 'D', dataLength(frames.get(i)), frames.get(i)));
            }
        }

        for (int i = 0; i < frames.size(); i++)
        {
            frames.set(i, String.format("<%s%s>", frames.get(i), calculateChecksum(frames.get(i))));
        }
        return frames;
    }
}

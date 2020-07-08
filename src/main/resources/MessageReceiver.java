package src.main.resources;

import src.utilities.TerminalStream;
import src.exceptions.ProtocolException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageReceiver {
    private int mtu;                      // maximum transfer unit (frame length limit)
    private FrameReceiver physicalLayer;  // physical layer object
    private TerminalStream terminal;      // terminal stream manager

    private static final String FRAME_PATTERN = "(<((E|D)-(\\d\\d)-(.*)-)(\\d\\d)>)";
    private static final Pattern COMPILED_FRAME_PATTERN = Pattern.compile(FRAME_PATTERN, Pattern.DOTALL);

    public MessageReceiver(int mtu) throws ProtocolException {
        // Create physical layer and terminal stream manager
        this.mtu = mtu;
        this.physicalLayer = new FrameReceiver();
        this.terminal = new TerminalStream("src.main.resources.MessageReceiver");
        terminal.printlnDiag("data link layer ready (mtu = " + mtu + ")");
    }

    public String receiveMessage() throws ProtocolException {
        terminal.printlnDiag("  receiveMessage starting");

        String message;
        ArrayList<String> messages = new ArrayList<>();
        String clearedFrame;
        String frameType = "D";
        String noisyFrame;

        while (nextFrame(frameType)) {
            noisyFrame = physicalLayer.receiveFrame();

            if (noisyFrame == null) {
                messages = null;
                break;
            }

            Matcher frameMatcher = COMPILED_FRAME_PATTERN.matcher(noisyFrame);

            if (frameMatcher.find()) {
                clearedFrame = frameMatcher.group(1);
                Matcher filteredFrameMatcher = COMPILED_FRAME_PATTERN.matcher(clearedFrame);

                constructMessage(filteredFrameMatcher, clearedFrame, messages);
                frameType = filteredFrameMatcher.group(3);
            } else {
                throw new ProtocolException("No Frame Found \"" + noisyFrame + "\": a frame should match <[E or D]-[data length value (two digits)]-[data (can be empty)]-[checksum value (two digits)]> e.g. \"<E-02-Hi-79>\"");
            }
        }

        message = concatenateMessages(messages);

        if (message == null)
            terminal.printlnDiag("  receiveMessage returning null (end of input stream)");
        else
            terminal.printlnDiag("  receiveMessage returning \"" + message + "\"");
        return message;

    }

    private boolean verifyMtuCompliance(String frame) throws ProtocolException {
        if (frame.length() > this.mtu) {
            throw new ProtocolException("MTU mismatch detected.");
        }
        return true;
    }

    private boolean verifyFrameChecksum(String filteredFrameForChecksum, String capturedFrameChecksum) throws ProtocolException {
        int ascii = 0;

        for (int i = 0; i < filteredFrameForChecksum.length(); i++) {
            char character = filteredFrameForChecksum.charAt(i);
            ascii = ascii + (int) character;
        }

        int actualFrameChecksum = ascii % 100;

        if (actualFrameChecksum < 10) {
            if (!String.format("0%d", actualFrameChecksum).equals(capturedFrameChecksum)) {
                throw new ProtocolException("checksum mismatch detected.");
            }
        } else if (!String.format("%d", actualFrameChecksum).equals(capturedFrameChecksum)) {
            throw new ProtocolException("checksum mismatch detected.");
        }
        return true;
    }

    private boolean verifyMessageLength(String capturedMessage, String capturedMessageLength) throws ProtocolException {
        int actualMessageLength = capturedMessage.length();

        if (actualMessageLength > 99) {
            throw new ProtocolException("data segment length cannot be greater than 99.");
        }
        if (actualMessageLength < 10) {
            if (!String.format("0%d", actualMessageLength).equals(capturedMessageLength)) {
                throw new ProtocolException("data segment length mismatch detected.");
            }

        } else if (!String.valueOf(actualMessageLength).equals(capturedMessageLength)) {
            throw new ProtocolException("data segment length mismatch detected.");
        }
        return true;
    }

    private boolean nextFrame(String frameType) {
        return frameType.equals("D");
    }

    private String concatenateMessages(ArrayList<String> messages) {
        if (messages == null) return null;
        StringBuilder fullMessage = new StringBuilder();

        for (String message : messages) {
            fullMessage.append(message);
        }
        return fullMessage.toString();
    }

    private void constructMessage(Matcher filteredFrameMatcher, String frame, ArrayList<String> messages) throws ProtocolException {
        if (filteredFrameMatcher.find()) {
            String filteredFrameForChecksum = filteredFrameMatcher.group(2);
            String capturedMessageLength = filteredFrameMatcher.group(4);
            String capturedMessage = filteredFrameMatcher.group(5);
            String capturedFrameChecksum = filteredFrameMatcher.group(6);

            if (
                    verifyMtuCompliance(frame) &&
                            verifyFrameChecksum(filteredFrameForChecksum, capturedFrameChecksum) &&
                            verifyMessageLength(capturedMessage, capturedMessageLength)
            )
                messages.add(capturedMessage);
        }
    }
}

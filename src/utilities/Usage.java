package src.utilities;

import java.util.regex.Pattern;

public class Usage {

    /**
     * Handle program usage errors.
     * Forces exit.
     */
    public static final String MTU_ARG_PATTERN = "[-m|\\-\\-mtu]=(?<mtu>[0-9 ]+)";
    public static final String DEBUG_ARG_PATTERN = "[-d|\\-\\-debug]=(?<debug>true|false)";

    public static final Pattern COMPILED_MTU_COMMAND_LINE_ARG = Pattern.compile(MTU_ARG_PATTERN, Pattern.DOTALL);
    public static final Pattern COMPILED_DEBUG_COMMAND_LINE_ARG = Pattern.compile(DEBUG_ARG_PATTERN, Pattern.DOTALL);

    public static void usageErrorExit(String errorMessage, int defaultMtu) {
        // Give command line usage info
        System.err.println(errorMessage);
        System.err.println("Usage  : Running Receiver/Sender");
        System.err.println("Options: -m=<length>, --mtu=<length>               set MTU value (default is " + defaultMtu + ") i.e. --mtu=55");
        System.err.println("         -d=<true, false>, --debug=<true, false>   enable debug mode (default is true) i.e. --debug=true");
        System.exit(1);
    }
}

public class OutputHelper {
    static public boolean hasError = false;
    static public int lastLine = 0;
    static ErrorType lastErrorType = null;
    static public void printSemanticError(ErrorType errorType, int line, String msg) {
        hasError = true;
        if (lastLine != line || lastErrorType == errorType) {
            if (line == lastLine && errorType == ErrorType.TYPEERR_OP) {
            } else {
                System.err.println("Error type " + errorType.ordinal() + " at Line " + line + ": " + msg);
            }
            }
        lastLine = line;
        lastErrorType = errorType;
    }
}

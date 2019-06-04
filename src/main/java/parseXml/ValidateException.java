package parseXml;

public class ValidateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public String getMessage() {
        return super.getMessage();
    }

    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }

    public void printStackTrace() {
        super.printStackTrace();
    }

    public ValidateException(String message) {
        super(message);
    }
}

package jfh.jawstestapp1a.Messages;


public class ErrorMessage extends BaseMessage {

    public static final String ERROR_KEY = "error";

    private Error error = null;

    public Error getError() {
        if (error==null)
            error = new Error();
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Error {
        private String message;
        private int errorCode;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

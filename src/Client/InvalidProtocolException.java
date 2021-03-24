package Client;

public class InvalidProtocolException extends Exception{
    public InvalidProtocolException() {
        super();
    }

    public InvalidProtocolException(String errorMessage) {
        super(errorMessage);
    }
}

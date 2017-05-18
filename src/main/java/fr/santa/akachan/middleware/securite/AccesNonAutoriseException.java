package fr.santa.akachan.middleware.securite;

public class AccesNonAutoriseException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccesNonAutoriseException() {
		super();
	}

	public AccesNonAutoriseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccesNonAutoriseException(String message) {
		super(message);
	}

	public AccesNonAutoriseException(Throwable cause) {
		super(cause);
	}
	
	
	
	
}

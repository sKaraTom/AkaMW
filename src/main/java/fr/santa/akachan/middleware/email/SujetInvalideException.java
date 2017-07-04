package fr.santa.akachan.middleware.email;

public class SujetInvalideException extends Exception {

	private static final long serialVersionUID = 1L;

	public SujetInvalideException() {
		super();
	}

	public SujetInvalideException(String message, Throwable cause) {
		super(message, cause);
	}

	public SujetInvalideException(String message) {
		super(message);
	}

	public SujetInvalideException(Throwable cause) {
		super(cause);
	}
	
	
	
}

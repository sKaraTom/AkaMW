package fr.santa.akachan.middleware.objetmetier.compte;

public class CompteDejaExistantException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompteDejaExistantException() {
		super();
	}

	public CompteDejaExistantException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompteDejaExistantException(String message) {
		super(message);
	}

	public CompteDejaExistantException(Throwable cause) {
		super(cause);
	}

	
	
	
}

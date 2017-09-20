package fr.santa.akachan.middleware.objetmetier.compte;

public class CompteNonAdminException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompteNonAdminException() {
		super();
	}

	public CompteNonAdminException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompteNonAdminException(String message) {
		super(message);
	}

	public CompteNonAdminException(Throwable cause) {
		super(cause);
	}
	
	

}

package fr.santa.akachan.middleware.authentification;

/**
 * un simple objet qui contient la clef secrète des tokens créés.
 *
 */
public class ClefSecrete {
	
	String secret = "%^$lsf#&asfgva120" ;
	
	public ClefSecrete() {
		super();
	}

	public String getSecret() {
		return secret;
	}
	
	/**
	 * le mutateur est private pour explicitement
	 * interdire le changement de la valeur de la clef.
	 * 
	 * @param secret
	 */
	private void setSecret(String secret) {
		this.secret = secret;
	}
	
	
	
	
	
	
	
	
}

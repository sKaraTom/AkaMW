package fr.santa.akachan.middleware.authentification;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * un objet Jeton qui sera envoyé côté ihm
 * contient l'id client, son prénom et un token. 
 *
 */
@XmlRootElement
public class Jeton implements Serializable {

	private String id;
	
	private String prenom;
	
	private String token;
	
	
	public Jeton() {
		super();
	}

	public Jeton(String id, String prenom, String token) {
		super();
		this.id = id;
		this.prenom = prenom;
		this.token = token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	

	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.id)
				.append(this.prenom)
				.append(this.token)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {
		
		if (candidat == this)
			return true;
		
		if (candidat == null)
			return false;
		
		if (!(candidat instanceof Jeton))
			return false;
		
		final Jeton autre = (Jeton) candidat; 
		
		return new EqualsBuilder()
				.append(this.id, autre.id)
				.append(this.prenom, autre.prenom)
				.append(this.token, autre.token)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Id", this.id)
				.append("Prenom", this.prenom)
				.append("Token", this.token)
				.build();
	}
	
	
}

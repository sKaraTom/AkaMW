package fr.santa.akachan.middleware.objetmetier.compte;

import java.io.Serializable;

import javax.mail.internet.InternetAddress;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.santa.akachan.middleware.objetmetier.client.Client;


@XmlRootElement
@Entity
@Table(name = "T_COMPTE")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="email")
public class Compte implements Serializable {

	
    private String email;

    private String password;

    private Client client;
    
	public Compte() {
		super();
	}

	public Compte(String pseudo, String email, String password, Boolean connecte, Client client) {
		super();
		this.email = email;
		this.password = password;
		this.client = client;
	}

	
	@Id
	@Column(name = "COM_EMAIL")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "COM_PASSWORD")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
	@OneToOne(cascade = CascadeType.ALL,mappedBy="compte")
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder()
				.append(this.password)
				.append(this.email)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {

		if (candidat == this)
			return true;

		if (candidat == null)
			return false;

		if (!(candidat instanceof Compte))
			return false;

		final Compte autre = (Compte) candidat;

		return new EqualsBuilder()
				.append(this.password, autre.password)
				.append(this.email, autre.email)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Password", this.password)
				.append("email", this.email)
				.build();
	}
	
}

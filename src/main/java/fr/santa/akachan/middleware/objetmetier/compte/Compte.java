package fr.santa.akachan.middleware.objetmetier.compte;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    
    private Calendar dateDeCreation;
    
    private String role;
    
    private Client client;
    
	public Compte() {
		super();
		this.dateDeCreation = Calendar.getInstance();
		this.role = "client";
	}

	public Compte(String email, String password, Client client) {
		super();
		this.email = email;
		this.password = password;
		this.dateDeCreation = Calendar.getInstance();
		this.role = "client";
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
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "COM_DATECREATION",columnDefinition= "TIMESTAMP WITH TIME ZONE")
	public Calendar getDateDeCreation() {
		return dateDeCreation;
	}

	public void setDateDeCreation(Calendar dateDeCreation) {
		this.dateDeCreation = dateDeCreation;
	}
	
	@Column(name = "COM_ROLE")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@OneToOne(cascade = CascadeType.ALL,orphanRemoval = true,mappedBy="compte")
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
				.append(this.dateDeCreation)
				.append(this.role)
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
				.append(this.dateDeCreation,autre.dateDeCreation)
				.append(this.role, autre.role)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Password", this.password)
				.append("email", this.email)
				.append("Date de création", this.dateDeCreation)
				.append("rôle", this.role)
				.build();
	}
	
}

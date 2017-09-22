package fr.santa.akachan.middleware.objetmetier.client;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;

@XmlRootElement
@Entity
@NamedQueries({
	@NamedQuery(name = "Client.obtenirNbreClients", query = "SELECT COUNT(c.prenom) FROM Client c WHERE c.compte.role != 'admin'"),
	@NamedQuery(name = "Client.obtenirNbreClientsParSexe", 
		query = "SELECT COUNT(c.prenom) FROM Client c WHERE c.sexe=:sexe AND c.compte.role != 'admin'"),
	@NamedQuery(name = "Client.obtenirClientSansDonneesSensibles", 
		query = "SELECT new Client(c.uuid,c.prenom,c.sexe,c.compte.email) FROM Client c WHERE c.uuid=:uuid")
	})
@Table(name = "T_CLIENT")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid") // pour éviter boucles infinies en json côté WS
public class Client implements Serializable{

	private UUID uuid;
	private String prenom;
	private String sexe;
	
	private Compte compte;
	
	
	public Client() {
		super();
	}

	public Client(UUID uuid, String prenom, String sexe, Compte compte) {
		super();
		this.uuid = uuid;
		this.prenom = prenom;
		this.sexe = sexe;
		this.compte = compte;
	}
	
	// constructeur pour la requête obtenirClient sans données sensibles.
	public Client(UUID uuid, String prenom, String sexe, String email) {
		super();
		this.uuid = uuid;
		this.prenom = prenom;
		this.sexe = sexe;
		this.compte = new Compte();
		this.compte.setEmail(email);
	}
	
	@Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name ="uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="CLI_UUID")
	public UUID getUuid() {
		return uuid;
	}


	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Column(name = "CLI_PRENOM")
	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	@Column(name = "CLI_SEXE")
	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="compte_email")
	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}
	
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.sexe)
				.append(this.prenom)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {
		
		if (candidat == this)
			return true;
		
		if (candidat == null)
			return false;
		
		if (!(candidat instanceof Client))
			return false;
		
		final Client autre = (Client) candidat; 
		
		return new EqualsBuilder()
				.append(this.sexe, autre.sexe)
				.append(this.prenom, autre.prenom)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("UUID", this.uuid)
				.append("Sexe", this.sexe)
				.append("Prenom", this.prenom)
				.build();
	}
	
	
}

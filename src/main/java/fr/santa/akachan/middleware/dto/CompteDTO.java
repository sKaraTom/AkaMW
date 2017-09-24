package fr.santa.akachan.middleware.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * objet Compte de transfert pour la partie administration.
 * Ne transférer que les champs voulus et lier le nombre d'estimations du client.
 * 
 * @author Thomas
 *
 */
public class CompteDTO implements Serializable {
	
	
	String email;
	
	Calendar dateDeCreation;
	
	String role;
	
	UUID uuid;
	
	String prenom;
	
	String sexe;
	
	Long nombreEstimations;
	
	
	public CompteDTO() {
		super();
	}

	public CompteDTO(String email, Calendar dateDeCreation, String role, UUID uuid, String prenom, String sexe,
			Long nombreEstimations) {
		super();
		this.email = email;
		this.dateDeCreation = dateDeCreation;
		this.role = role;
		this.uuid = uuid;
		this.prenom = prenom;
		this.sexe = sexe;
		this.nombreEstimations = nombreEstimations;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getDateDeCreation() {
		return dateDeCreation;
	}

	public void setDateDeCreation(Calendar dateDeCreation) {
		this.dateDeCreation = dateDeCreation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public Long getNombreEstimations() {
		return nombreEstimations;
	}

	public void setNombreEstimations(Long nombreEstimations) {
		this.nombreEstimations = nombreEstimations;
	}
	
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.email)
				.append(this.dateDeCreation)
				.append(this.role)
				.append(this.prenom)
				.append(this.sexe)
				.append(this.nombreEstimations)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {
		
		if (candidat == this)
			return true;
		if (candidat == null)
			return false;
		if (!(candidat instanceof CompteDTO))
			return false;
		
		final CompteDTO autre = (CompteDTO) candidat;
		
		return new EqualsBuilder()
				.append(this.email, autre.email)
				.append(this.dateDeCreation, autre.dateDeCreation)
				.append(this.role, autre.role)
				.append(this.prenom, autre.prenom)
				.append(this.sexe, autre.sexe)
				.append(this.nombreEstimations, autre.nombreEstimations)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Email", this.email)
				.append("Date de création", this.dateDeCreation)
				.append("Rôle", this.role)
				.append("Prénom", this.prenom)
				.append("Sexe", this.sexe)
				.append("Nombre d'estimations", this.nombreEstimations)
				.build();
	}
	
	
	
}

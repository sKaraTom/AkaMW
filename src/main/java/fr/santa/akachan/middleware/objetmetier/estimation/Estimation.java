package fr.santa.akachan.middleware.objetmetier.estimation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import fr.santa.akachan.middleware.objetmetier.client.Client;

@XmlRootElement
@Entity
@NamedQueries({
	@NamedQuery(name = "Estimation.obtenirListeEstimations", 
		query = "SELECT e FROM Estimation e WHERE e.refClient=:refclient AND e.akachan=:akachan"),
	@NamedQuery(name = "Estimation.obtenirListeFavoris", query = "SELECT e FROM Estimation e WHERE e.refClient=:refclient AND e.favori='1'"),
	@NamedQuery(name = "Estimation.obtenirNbreTotal", query = "SELECT count(*) FROM Estimation"),
	@NamedQuery(name = "Estimation.obtenirNbreTotalParSexe", query = "SELECT count(*) FROM Estimation e WHERE e.sexe=:sex"),
	@NamedQuery(name = "Estimation.obtenirNbEstimClient", query = "SELECT count(*) FROM Estimation e WHERE e.refClient=:refclient"),
	@NamedQuery(name = "Estimation.obtenirNbEstimClientParSexe", 
		query = "SELECT count(*) FROM Estimation e WHERE e.refClient=:refclient AND e.sexe=:sex"),
	@NamedQuery(name = "Estimation.obtenirTopPrenomsEstimes", 
		query = "SELECT e.prenom FROM Estimation e WHERE e.sexe=:sex AND e.akachan='true' GROUP BY e.prenom ORDER BY COUNT(*) DESC"),
	@NamedQuery(name = "Estimation.supprimerToutesEstimationsClient", query = "DELETE FROM Estimation e WHERE e.refClient=:refclient")
	})
@Table(name = "T_ESTIMATION")
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid") 
public class Estimation implements Serializable {

	private UUID uuid;
	private String prenom;
	private String sexe;
	private Boolean favori;

	private UUID refClient;

	private String akachan;
	
	private Calendar dateEstimation;
	
	public Estimation() {
		super();
		this.dateEstimation = Calendar.getInstance();
	}

	public Estimation(UUID uuid, String prenom, String sexe, UUID refClient, String akachan, Calendar dateEstimation) {
		super();
		this.uuid = uuid;
		this.prenom = prenom;
		this.sexe = sexe;
		this.favori = false;
		this.refClient = refClient;
		this.akachan = akachan;
		this.dateEstimation = Calendar.getInstance();
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "EST_UUID")
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Column(name = "EST_PRENOM")
	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	@Column(name = "EST_SEXE")
	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	
	@Column(name = "EST_FAVORI")
	public Boolean getFavori() {
		return favori;
	}

	public void setFavori(Boolean favori) {
		this.favori = favori;
	}
	

	@Column(name = "EST_REFCLIENT")
	public UUID getRefClient() {
		return refClient;
	}

	public void setRefClient(UUID refClient) {
		this.refClient = refClient;
	}

	@Column(name = "EST_AKACHAN")
	public String getAkachan() {
		return akachan;
	}

	public void setAkachan(String akachan) {
		this.akachan = akachan;
	}

	@Column(name = "EST_DATE",columnDefinition= "TIMESTAMP WITH TIME ZONE")
	public Calendar getDateEstimation() {
		return dateEstimation;
	}

	public void setDateEstimation(Calendar dateEstimation) {
		this.dateEstimation = dateEstimation;
	}
	

	@Override
	public int hashCode() {

		return new HashCodeBuilder()
				.append(this.prenom)
				.append(this.sexe)
				.append(this.favori)
				.append(this.refClient)
				.append(this.dateEstimation)
				.append(this.akachan)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {

		if (candidat == this)
			return true;

		if (candidat == null)
			return false;

		if (!(candidat instanceof Estimation))
			return false;

		final Estimation autre = (Estimation) candidat;

		return new EqualsBuilder()
				.append(this.prenom, autre.prenom)
				.append(this.sexe, autre.sexe)
				.append(this.favori, autre.favori)
				.append(this.refClient, autre.refClient)
				.append(this.dateEstimation, autre.dateEstimation)
				.append(this.akachan, autre.akachan)
				.build();
	}

	@Override
	public String toString() {

		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Prenom", this.prenom)
				.append("Sexe", this.sexe)
				.append("favori", this.favori)
				.append("Client", this.refClient)
				.append("Date d'estimation", this.dateEstimation)
				.append("Akachan", this.akachan)
				.build();
	}

}

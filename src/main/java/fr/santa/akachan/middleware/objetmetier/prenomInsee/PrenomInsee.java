package fr.santa.akachan.middleware.objetmetier.prenomInsee;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement
@Entity
@NamedNativeQueries({
@NamedNativeQuery(name = "PrenomInsee.chercherPrenomEtEstimationExistante", 
query = "SELECT DISTINCT pre_label, est_akachan FROM t_prenom LEFT OUTER JOIN t_estimation ON pre_label=est_prenom AND pre_sexe=est_sexe AND est_refclient=:refClient WHERE pre_label LIKE :recherche AND pre_sexe=:sex ORDER BY pre_label ASC")
})
@Table(name = "T_INSEE")
public class PrenomInsee implements Serializable {
	
	private Integer reference;
	
	private String sexe;
	
	private String label;
	
	private Integer annee;
	
	private Integer nombreNaissance;

	
	public PrenomInsee() {
		super();
	}

	public PrenomInsee(Integer reference, String sexe, String label, Integer annee, Integer nombre) {
		super();
		this.reference = reference;
		this.sexe = sexe;
		this.label = label;
		this.annee = annee;
		this.nombreNaissance = nombre;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INS_REFERENCE")
	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	
	@Column(name = "INS_SEXE")
	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	@Column(name = "INS_LABEL")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Column(name = "INS_ANNEE")
	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}
	
	@Column(name = "INS_NOMBRE")
	public Integer getNombreNaissance() {
		return nombreNaissance;
	}

	public void setNombreNaissance(Integer nombre) {
		this.nombreNaissance = nombre;
	}
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.sexe)
				.append(this.label)
				.append(this.annee)
				.append(nombreNaissance)
				.build();
	}

	@Override
	public boolean equals(final Object candidat) {
		
		if (candidat == this)
			return true;
		
		if (candidat == null)
			return false;
		
		if (!(candidat instanceof PrenomInsee))
			return false;
		
		final PrenomInsee autre = (PrenomInsee) candidat; 
		
		return new EqualsBuilder()
				.append(this.label, autre.label)
				.append(this.sexe, autre.sexe)
				.append(this.annee, autre.annee)
				.append(this.nombreNaissance, autre.nombreNaissance)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Sexe", this.sexe)
				.append("Label", this.label)
				.append("annee", this.annee)
				.append("nombre", this.nombreNaissance)
				.build();
	}
	
	
	
	
	
}

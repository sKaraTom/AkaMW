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
@NamedQueries({
@NamedQuery(name = "PrenomInsee.obtenirStatsPrenom", 
query = "SELECT p FROM PrenomInsee p WHERE p.label=:lab AND  p.sexe=:sex"),
@NamedQuery(name = "PrenomInsee.obtenirTotalNaissancesPourUnPrenom", 
query = "SELECT SUM(p.nombreNaissances) FROM PrenomInsee p WHERE p.label=:lab AND  p.sexe=:sexe"),
@NamedQuery(name = "PrenomInsee.obtenirAnneeMaxNaissancesPourUnPrenom", 
query = "SELECT p FROM PrenomInsee p WHERE p.label=:lab AND p.sexe=:sexe AND p.nombreNaissances=(SELECT MAX(i.nombreNaissances) FROM PrenomInsee i WHERE i.label=:lab AND i.sexe=:sexe)")
})
@Table(name = "T_INSEE")
public class PrenomInsee implements Serializable {
	
	private Integer reference;
	
	private String sexe;
	
	private String label;
	
	private Integer annee;
	
	private Integer nombreNaissances;

	
	public PrenomInsee() {
		super();
	}

	public PrenomInsee(Integer reference, String sexe, String label, Integer annee, Integer nombre) {
		super();
		this.reference = reference;
		this.sexe = sexe;
		this.label = label;
		this.annee = annee;
		this.nombreNaissances = nombre;
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
	public Integer getNombreNaissances() {
		return nombreNaissances;
	}

	public void setNombreNaissances(Integer nombre) {
		this.nombreNaissances = nombre;
	}
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.sexe)
				.append(this.label)
				.append(this.annee)
				.append(nombreNaissances)
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
				.append(this.nombreNaissances, autre.nombreNaissances)
				.build();
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("Sexe", this.sexe)
				.append("Label", this.label)
				.append("annee", this.annee)
				.append("nombre de naissances :", this.nombreNaissances)
				.build();
	}
	
	
	
	
	
}

package fr.santa.akachan.middleware.objetmetier.prenom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
@Entity
@NamedNativeQueries({
@NamedNativeQuery(name = "Prenom.chercherPrenomEtEstimationExistante", 
query = "SELECT pre_label, est_akachan FROM t_prenom LEFT OUTER JOIN t_estimation ON pre_label=est_prenom AND pre_sexe=est_sexe AND est_refclient=:refClient WHERE pre_label LIKE :recherche AND pre_sexe=:sex ORDER BY pre_label ASC")
})
@Table(name = "T_PRENOM")
public class Prenom implements Serializable {

	private Integer id;
	
	private String label;
	
	private String sexe;
	
	private String tendance;

	public Prenom() {
		super();
	}

	public Prenom(Integer id, String sexe, String label, String tendance) {
		super();
		this.id = id;
		this.sexe = sexe;
		this.label = label;
		this.tendance = tendance;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRE_REFERENCE")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "PRE_LABEL")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Column(name = "PRE_SEXE")
	public String getSexe() {
		return sexe;
	}
	
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	
	@Column(name = "PRE_TENDANCE")
	public String getTendance() {
		return tendance;
	}

	public void setTendance(String tendance) {
		this.tendance = tendance;
	}
	
	@Override
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.sexe)
				.append(this.label)
				.append(this.tendance)
				.build();
	}
	
	@Override
	public boolean equals(final Object candidat) {
		
		if (candidat == this)
			return true;
		
		if (candidat == null)
			return false;
		
		if (!(candidat instanceof Prenom))
			return false;
		
		final Prenom autre = (Prenom) candidat; 
		
		return new EqualsBuilder()
				.append(this.label, autre.label)
				.append(this.sexe, autre.sexe)
				.append(this.tendance, autre.tendance)
				.build();
	}
	
	
	
	
}

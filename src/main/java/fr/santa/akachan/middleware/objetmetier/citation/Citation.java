package fr.santa.akachan.middleware.objetmetier.citation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
	@NamedQuery(name = "Citation.obtenirNbreCitations", query = "SELECT COUNT(c.id) FROM Citation c"),
	@NamedQuery(name = "Citation.obtenirIdMax", query = "SELECT MAX(c.id) FROM Citation c")
	})
@Table(name = "T_CITATION")
public class Citation {

	private Integer id;
	private String auteur;
	private String contenu;
	
	public Citation() {
		super();
	}

	public Citation(Integer id, String auteur, String contenu) {
		super();
		this.id = id;
		this.auteur = auteur;
		this.contenu = contenu;
	}
	
	@Id
	@Column(name = "CIT_ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "CIT_AUTEUR")
	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	@Column(name = "CIT_CONTENU")
	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder()
				.append(this.id)
				.append(this.auteur)
				.append(this.contenu)
				.build();
	}

	@Override
	public boolean equals(Object candidat) {
		if (candidat == this)
			return true;
		
		if (candidat == null)
			return false;
		
		if (!(candidat instanceof Citation))
			return false;
		
		final Citation autre = (Citation) candidat; 
		
		return new EqualsBuilder()
				.append(this.id, autre.id)
				.append(this.auteur, autre.auteur)
				.append(this.contenu, autre.contenu)
				.build();
	}
	
	@Override
	public String toString() {
		
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", this.id)
				.append("auteur", this.auteur)
				.append("contenu", this.contenu)
				.build();
	}
	
	
	
	
	
}

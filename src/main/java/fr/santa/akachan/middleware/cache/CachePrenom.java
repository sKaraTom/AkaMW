package fr.santa.akachan.middleware.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;

@Stateful
@StatefulTimeout(unit = TimeUnit.MINUTES, value = 20)
public class CachePrenom implements Serializable {
	
	private List<String> listePrenoms = new ArrayList<String>();

	public CachePrenom() {
		super();
	}

	public List<String> getListePrenoms() {
		return listePrenoms;
	}
	
	public void setListePrenoms(List<String> listePrenoms) {
		this.listePrenoms = listePrenoms;
	}
	
	public String getPrenom(Integer index) {
		String prenomARetourner = this.listePrenoms.get(index);
		return prenomARetourner;
	}
	
	public Integer getTailleListe() {
		return this.listePrenoms.size();
	}
	
	public void supprimerPrenom(String prenomASupprimer) {
		this.listePrenoms.remove(prenomASupprimer);
	}
	
	public Boolean estVide() {
		if (this.listePrenoms.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@PrePassivate
	@Remove
	public void viderListe() {
		this.listePrenoms.clear();
	}
	
	
	
}

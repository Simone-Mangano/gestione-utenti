package org.exi.demo.service.definition;

import java.util.List;

import org.exi.demo.model.Utente;

public interface UtenteService {
	
	List<Utente> findAll();
	Utente getById(Long id);
	Utente create(Utente utente);
	Utente update(Long id, Utente utente);
	List<Utente> search(String q);
	Utente searchExact(String cf);
	void delete(Long id);
	
}

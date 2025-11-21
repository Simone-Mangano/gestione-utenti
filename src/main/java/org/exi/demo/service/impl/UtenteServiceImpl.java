package org.exi.demo.service.impl;

import java.util.List;

import org.exi.demo.exception.DuplicationErrorException;
import org.exi.demo.exception.UtenteNotFoundException;
import org.exi.demo.model.Utente;
import org.exi.demo.repository.UtenteRepo;
import org.exi.demo.service.definition.UtenteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepo utenteRepo;

    public UtenteServiceImpl(UtenteRepo utenteRepo) {
        this.utenteRepo = utenteRepo;
    }

    
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Utente> findAll() {
    	List<Utente> utenti = utenteRepo.findAll();
    	
    	if(utenti.isEmpty()) throw new UtenteNotFoundException("Nessun utente trovato");
    	
    	return utenti;
    }
    
    
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Utente getById(Long id) {
        return utenteRepo.findById(id)
                .orElseThrow(() -> new UtenteNotFoundException("Utente non trovato"));
    }

    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Utente create(Utente utente){

        if (utenteRepo.existsByCodiceFiscale(utente.getCodiceFiscale())) {
            throw new DuplicationErrorException("Codice fiscale già esistente");
        }
        utente.setId(null);

        return utenteRepo.save(utente);
    }

    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Utente update(Long id, Utente utente) {
        Utente check = utenteRepo.findById(id)
                .orElseThrow(() -> new UtenteNotFoundException(id));

        if (!check.getCodiceFiscale().equals(utente.getCodiceFiscale())) {
        	
            boolean cfUsato = utenteRepo.existsByCodiceFiscaleAndIdNot(utente.getCodiceFiscale(), id);
            
            if (cfUsato) {
            	
            	throw new DuplicationErrorException("Codice fiscale già usato da un altro utente");
            }
        }
        check.setNome(utente.getNome());
        check.setCognome(utente.getCognome());
        check.setCodiceFiscale(utente.getCodiceFiscale());
        check.setDataDiNascita(utente.getDataDiNascita());

        return utenteRepo.save(check);
    }

    
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        if (!utenteRepo.existsById(id)) {
        	throw new UtenteNotFoundException(id);
        }
        utenteRepo.deleteById(id);
    }

    
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Utente> search(String q) {

        if (q == null || q.isBlank()) {
            return findAll();
        }

        return utenteRepo.findAll()
                .stream()
                .filter(u ->
                        u.getNome().toLowerCase().contains(q.toLowerCase()) ||
                        u.getCognome().toLowerCase().contains(q.toLowerCase()) ||
                        u.getCodiceFiscale().toLowerCase().contains(q.toLowerCase())
                )
                .toList();
    }

    
	@Override
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public Utente searchExact(String cf) {
		
		Utente utente = utenteRepo.findByCodiceFiscaleIgnoreCase(cf)
				.orElseThrow(() -> new UtenteNotFoundException("Nessun utente trovato con questo codice fiscale"));
									
		return utente;
	}
}
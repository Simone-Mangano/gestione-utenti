package org.exi.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.exi.demo.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepo extends JpaRepository<Utente,Long>	{

	List<Utente> findByNomeContainingIgnoreCase(String nome);
	List<Utente> findByCognomeContainingIgnoreCase(String cognome);
	List<Utente> findByNomeAndCognomeContainingIgnoreCase(String nome, String cognome);
	Optional<Utente> findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCaseAndDataDiNascita(String nome, String cognome, LocalDate dataDiNascita);
	List<Utente> findByDataDiNascita(LocalDate dataDiNascita);
	Optional<Utente> findByCodiceFiscaleIgnoreCase(String codiceFiscale);
	boolean existsByCodiceFiscale(String codiceFiscale);
	boolean existsByCodiceFiscaleAndIdNot(String codiceFiscale, Long id);
}

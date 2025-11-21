package org.exi.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.exi.demo.exception.DuplicationErrorException;
import org.exi.demo.exception.UtenteNotFoundException;
import org.exi.demo.model.Utente;
import org.exi.demo.repository.UtenteRepo;
import org.exi.demo.service.definition.UtenteService;
import org.exi.demo.service.impl.UtenteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-h2.yml")
@Import(UtenteServiceImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UtenteServiceImplTest {

    @Autowired
    private UtenteRepo utenteRepo;

    @Autowired
    private UtenteService utenteService;

    @BeforeEach
    void cleanDb() {
        // Pulisce la tabella prima di ogni test,
        // così i test non si "sporcano" tra di loro
        utenteRepo.deleteAll();
    }

    private Utente nuovoUtente(String cf) {
        Utente u = new Utente();
        u.setNome("Simone");
        u.setCognome("Mangano");
        u.setCodiceFiscale(cf);
        u.setDataDiNascita(LocalDate.of(2000, 8, 30));
        return u;
    }

    @Test
    void create() {

        // preparazione
        Utente u = nuovoUtente("MRARSS90A01H501X");

        // azione
        Utente saved = utenteService.create(u);

        // risultato
        assertNotNull(saved.getId());
        assertEquals("MRARSS90A01H501X", saved.getCodiceFiscale());
    }

    @Test
    void createCfDuplicate() {

        Utente u1 = nuovoUtente("DPLCFX90A01H501X");
        utenteService.create(u1);

        Utente u2 = nuovoUtente("DPLCFX90A01H501X");

        assertThatThrownBy(() -> utenteService.create(u2))
                .isInstanceOf(DuplicationErrorException.class)
                .hasMessageContaining("Codice fiscale già esistente");
    }

    @Test
    void searchExactOk() {

        Utente u = nuovoUtente("SRCFND90A01H501X");
        utenteService.create(u);

        Utente found = utenteService.searchExact("SRCFND90A01H501X");

        assertThat(found).isNotNull();
        assertThat(found.getNome()).isEqualTo("Simone");
    }

    @Test
    void searchExactError() {

        assertThatThrownBy(() -> utenteService.searchExact("INEXISTENTCF0000"))
                .isInstanceOf(UtenteNotFoundException.class)
                .hasMessageContaining("Nessun utente trovato");
    }

    @Test
    void deleteOk() {

        Utente u = utenteService.create(nuovoUtente("DLTCF190A01H501X"));
        Long id = u.getId();

        utenteService.delete(id);

        assertThat(utenteRepo.findById(id)).isEmpty();
    }

    @Test
    void deleteUtenteNotFound() {

        assertThatThrownBy(() -> utenteService.delete(999L))
                .isInstanceOf(UtenteNotFoundException.class);
    }

    @Test
    void searchOkFilterResault() {

        utenteService.create(nuovoUtente("AAA11111AAA11111"));
        Utente u2 = nuovoUtente("BBB22222BBB22222");
        u2.setNome("Giulia");
        u2.setCognome("Verdi");
        utenteService.create(u2);

        List<Utente> result = utenteService.search("giul");

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(Utente::getNome)
                .isEqualTo("Giulia");
    }
}
package org.exi.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.exi.demo.model.Utente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-h2.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UtenteRepoTest {

    @Autowired
    private UtenteRepo utenteRepo;

    @Test
    void saveAndFindById_shouldPersistUtente() {

        Utente u = new Utente();
        u.setNome("Simone");
        u.setCognome("Mangano");
        u.setCodiceFiscale("SMNMGN00A01H501X");
        u.setDataDiNascita(LocalDate.of(2000, 1, 1));

        Utente saved = utenteRepo.save(u);
        Utente found = utenteRepo.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getNome()).isEqualTo("Simone");
        assertThat(found.getCodiceFiscale()).isEqualTo("SMNMGN00A01H501X");
    }
}
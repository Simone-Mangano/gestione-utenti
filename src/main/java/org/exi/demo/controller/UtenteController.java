package org.exi.demo.controller;

import java.util.List;

import org.exi.demo.dto.mapper.UtenteMapper;
import org.exi.demo.dto.request.CreateUtenteDto;
import org.exi.demo.dto.request.UpdateUtenteDto;
import org.exi.demo.dto.response.UtenteDto;
import org.exi.demo.model.Utente;
import org.exi.demo.service.definition.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Utenti", description = "Operazioni di gestione utenti")
@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService service;
    private final UtenteMapper mapper;

    @Autowired
    public UtenteController(UtenteService service, UtenteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // get all
    @Operation(
    	    summary = "Ottieni tutti gli utenti",
    	    description = "Restituisce la lista completa degli utenti registrati"
    	)
    @GetMapping
    public List<UtenteDto> findAll() {
        return service.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // get by id
    @Operation(
    	    summary = "Trova un utente per ID",
    	    description = "Restituisce l'utente corrispondente all'ID fornito"
    	)
    @GetMapping("/{id}")
    public UtenteDto findById(@PathVariable Long id) {
        Utente u = service.getById(id);
        return mapper.toDto(u);
    }

    // search
    @Operation(
    	    summary = "Ricerca utenti",
    	    description = "Cerca utenti basandosi su nome, cognome o altri campi"
    	)
    @GetMapping("/search")
    public List<UtenteDto> search(@RequestParam String q) {
        return service.search(q)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // search exact
    @Operation(
    	    summary = "Ricerca esatta per codice fiscale",
    	    description = "Restituisce l’utente con il codice fiscale fornito"
    	)
    @GetMapping("/searchExact")
    public UtenteDto searchExact(@RequestParam String cf) {
        return mapper.toDto(service.searchExact(cf));
    }

    // create
    @Operation(
    	    summary = "Crea un nuovo utente",
    	    description = "Registra un nuovo utente nel sistema"
    	)
    @PostMapping
    public UtenteDto create(@Valid @RequestBody CreateUtenteDto dto) {
        Utente entity = mapper.toEntity(dto);
        Utente saved = service.create(entity);
        return mapper.toDto(saved);
    }

    // update
    @Operation(
    	    summary = "Aggiorna un utente",
    	    description = "Aggiorna i dati di un utente esistente"
    	)
    @PutMapping("/{id}")
    public UtenteDto update(@PathVariable Long id, @Valid @RequestBody UpdateUtenteDto dto) {
        
        Utente existing = service.getById(id);
        mapper.updateEntity(existing, dto);
        Utente saved = service.update(id, existing);

        return mapper.toDto(saved);
    }

    // delete
    @Operation(
    	    summary = "Elimina un utente",
    	    description = "Rimuove l’utente con l’ID specificato"
    	)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
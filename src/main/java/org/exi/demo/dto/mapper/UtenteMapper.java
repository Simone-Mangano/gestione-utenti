package org.exi.demo.dto.mapper;

import org.exi.demo.dto.request.CreateUtenteDto;
import org.exi.demo.dto.request.UpdateUtenteDto;
import org.exi.demo.dto.response.UtenteDto;
import org.exi.demo.model.Utente;
import org.springframework.stereotype.Component;

@Component
public class UtenteMapper {

	public UtenteDto toDto(Utente utente) {
        if (utente == null) return null;

        UtenteDto dto = new UtenteDto();
        dto.setId(utente.getId());
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setCodiceFiscale(utente.getCodiceFiscale());
        dto.setDataDiNascita(utente.getDataDiNascita());
        return dto;
    }

    public Utente toEntity(CreateUtenteDto dto) {
        if (dto == null) return null;

        Utente u = new Utente();
        u.setNome(dto.getNome());
        u.setCognome(dto.getCognome());
        u.setCodiceFiscale(dto.getCodiceFiscale());
        u.setDataDiNascita(dto.getDataDiNascita());
        return u;
    }

    public void updateEntity(Utente utente, UpdateUtenteDto dto) {
        if (utente == null || dto == null) return;

        utente.setNome(dto.getNome());
        utente.setCognome(dto.getCognome());
        utente.setCodiceFiscale(dto.getCodiceFiscale());
        utente.setDataDiNascita(dto.getDataDiNascita());
    }
}

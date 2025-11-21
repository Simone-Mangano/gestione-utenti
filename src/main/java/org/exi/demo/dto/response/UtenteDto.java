package org.exi.demo.dto.response;

import java.time.LocalDate;

import lombok.Data;


@Data
public class UtenteDto {
	
	private Long id;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private LocalDate dataDiNascita;
	
}

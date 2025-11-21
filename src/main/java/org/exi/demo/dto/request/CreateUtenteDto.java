package org.exi.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUtenteDto {
	
	@NotBlank(message = "Devi riempire il campo")
	private String nome;
	
	@NotBlank(message = "Devi riempire il campo")
	private String cognome;
	
	@NotBlank(message = "Devi riempire il campo")
	@Size(min = 16, max = 16)
	private String codiceFiscale;
	
	@NotNull(message = "Devi riempire il campo")
	@Past(message = "La data inserita non è valida")
	private LocalDate dataDiNascita;
}

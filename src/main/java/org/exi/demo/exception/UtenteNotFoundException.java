package org.exi.demo.exception;

public class UtenteNotFoundException extends RuntimeException{

	public UtenteNotFoundException(Long id) {
        super("Utente con id " + id + " non trovato");
    }

    public UtenteNotFoundException(String message) {
        super(message);
    }
}

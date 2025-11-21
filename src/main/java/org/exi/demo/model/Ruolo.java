package org.exi.demo.model;

public enum Ruolo {
    ADMIN("ADMIN"),
    USER("USER");

    private final String nome;

    Ruolo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
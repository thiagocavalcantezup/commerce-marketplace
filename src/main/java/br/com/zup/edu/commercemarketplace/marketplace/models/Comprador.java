package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.time.LocalDate;

import javax.persistence.Embeddable;

@Embeddable
public class Comprador {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String endereco;
    private LocalDate dataNascimento;

    public Comprador() {}

    public Comprador(Long id, String nome, String cpf, String email, String endereco,
                     LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

}

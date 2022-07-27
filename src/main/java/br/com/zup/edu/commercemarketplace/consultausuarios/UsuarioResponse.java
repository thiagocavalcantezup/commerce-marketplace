package br.com.zup.edu.commercemarketplace.consultausuarios;

import java.time.LocalDate;

import br.com.zup.edu.commercemarketplace.marketplace.models.Comprador;

public class UsuarioResponse {

    private String nome;
    private String cpf;
    private String email;
    private String endereco;
    private LocalDate dataNascimento;

    public UsuarioResponse() {}

    public UsuarioResponse(String nome, String cpf, String email, String endereco, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }

    public Comprador toModel(Long id) {
        return new Comprador(id, nome, cpf, email, endereco, dataNascimento);
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

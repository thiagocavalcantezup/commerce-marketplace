package br.com.zup.edu.commercemarketplace.marketplace.events;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.zup.edu.commercemarketplace.marketplace.models.Comprador;

public class CompradorVendaEvent {

    private String nome;
    private String cpf;
    private String email;
    private String endereco;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    public CompradorVendaEvent() {}

    public CompradorVendaEvent(Comprador comprador) {
        this.nome = comprador.getNome();
        this.cpf = comprador.getCpf();
        this.email = comprador.getEmail();
        this.endereco = comprador.getEndereco();
        this.dataNascimento = comprador.getDataNascimento();
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

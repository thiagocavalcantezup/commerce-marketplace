package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoQuantidade {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private LocalDateTime criadoEm;
    private Long quantidade;

    public ProdutoQuantidade() {
    }

    public ProdutoQuantidade(Long id, String nome, BigDecimal preco, LocalDateTime criadoEm, Long quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.criadoEm = criadoEm;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getTotal() {
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }

}

package br.com.zup.edu.commercemarketplace.catalogoprodutos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.zup.edu.commercemarketplace.marketplace.models.ProdutoQuantidade;

public class ProdutoResponse {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private LocalDateTime criadoEm;

    public ProdutoResponse() {}

    public ProdutoResponse(Long id, String nome, BigDecimal preco, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.criadoEm = criadoEm;
    }

    public ProdutoQuantidade toModel(Long quantidade) {
        return new ProdutoQuantidade(id, nome, preco, criadoEm, quantidade);
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

}

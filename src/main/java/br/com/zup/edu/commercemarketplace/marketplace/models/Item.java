package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Item {

    @Column(name = "id_item", nullable = false)
    private Long id;

    @Column(name = "quantidade_item", nullable = false)
    private Long quantidade;

    @Column(name = "preco_item", nullable = false)
    private BigDecimal preco;

    public Item() {
    }

    public Item(Long id, Long quantidade, BigDecimal preco) {
        this.id = id;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPreco() {
        return preco;
    }

}

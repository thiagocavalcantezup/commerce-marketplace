package br.com.zup.edu.commercemarketplace.marketplace.events;

import java.math.BigDecimal;

import br.com.zup.edu.commercemarketplace.marketplace.models.ProdutoQuantidade;

public class ItemVendaEvent {

    private Long id;
    private String nome;
    private Long quantidade;
    private BigDecimal valor;

    public ItemVendaEvent() {
    }

    public ItemVendaEvent(ProdutoQuantidade produtoQuantidade) {
        this.id = produtoQuantidade.getId();
        this.nome = produtoQuantidade.getNome();
        this.quantidade = produtoQuantidade.getQuantidade();
        this.valor = produtoQuantidade.getPreco();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

}

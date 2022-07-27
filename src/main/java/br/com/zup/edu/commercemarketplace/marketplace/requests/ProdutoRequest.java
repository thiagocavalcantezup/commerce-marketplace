package br.com.zup.edu.commercemarketplace.marketplace.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ProdutoRequest {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Positive
    private Long quantidade;

    public ProdutoRequest() {}

    public ProdutoRequest(@NotNull Long id, @NotNull Long quantidade) {
        this.id = id;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public Long getQuantidade() {
        return quantidade;
    }

}

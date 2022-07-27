package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Pagamento {

    @Column(columnDefinition = "BINARY(16)", name = "id_pagamento", nullable = false)
    private UUID id;

    @Column(name = "forma_pagamento", nullable = false)
    private String forma;

    @Column(name = "status_pagamento", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    public Pagamento() {}

    public Pagamento(UUID id, StatusPagamento status) {
        this.id = id;
        this.forma = "Cartão de Crédito";
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getForma() {
        return forma;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public boolean foiAprovado() {
        return status.equals(StatusPagamento.APROVADO);
    }

}

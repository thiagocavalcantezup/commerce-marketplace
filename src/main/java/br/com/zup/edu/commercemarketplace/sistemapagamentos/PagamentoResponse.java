package br.com.zup.edu.commercemarketplace.sistemapagamentos;

import java.util.UUID;

public class PagamentoResponse {

    private UUID id;
    private StatusPagamentoResponse status;

    public PagamentoResponse() {
    }

    public PagamentoResponse(UUID id, StatusPagamentoResponse status) {
        this.id = id;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public StatusPagamentoResponse getStatus() {
        return status;
    }

}

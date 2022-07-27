package br.com.zup.edu.commercemarketplace.sistemapagamentos;

import java.util.UUID;

import br.com.zup.edu.commercemarketplace.marketplace.models.Pagamento;
import br.com.zup.edu.commercemarketplace.marketplace.models.StatusPagamento;

public class PagamentoResponse {

    private UUID id;
    private StatusPagamentoResponse status;

    public PagamentoResponse() {}

    public PagamentoResponse(UUID id, StatusPagamentoResponse status) {
        this.id = id;
        this.status = status;
    }

    public Pagamento toModel() {
        return new Pagamento(id, StatusPagamento.valueOf(status.name()));
    }

    public UUID getId() {
        return id;
    }

    public StatusPagamentoResponse getStatus() {
        return status;
    }

}

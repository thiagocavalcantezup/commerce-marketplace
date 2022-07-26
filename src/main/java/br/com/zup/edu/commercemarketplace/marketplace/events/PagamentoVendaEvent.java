package br.com.zup.edu.commercemarketplace.marketplace.events;

import java.util.UUID;

import br.com.zup.edu.commercemarketplace.marketplace.models.Pagamento;

public class PagamentoVendaEvent {

    private UUID id;
    private String forma;
    private StatusPagamentoEvent status;

    public PagamentoVendaEvent() {
    }

    public PagamentoVendaEvent(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.forma = "Cartão de Crédito";
        this.status = StatusPagamentoEvent.valueOf(pagamento.getStatus().name());
    }

    public UUID getId() {
        return id;
    }

    public String getForma() {
        return forma;
    }

    public StatusPagamentoEvent getStatus() {
        return status;
    }
}

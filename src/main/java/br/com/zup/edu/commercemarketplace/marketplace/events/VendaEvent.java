package br.com.zup.edu.commercemarketplace.marketplace.events;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.zup.edu.commercemarketplace.marketplace.models.Comprador;
import br.com.zup.edu.commercemarketplace.marketplace.models.ProdutoQuantidade;
import br.com.zup.edu.commercemarketplace.marketplace.models.Venda;

public class VendaEvent {

    private UUID codigoPedido;
    private CompradorVendaEvent comprador;
    private List<ItemVendaEvent> itens;
    private PagamentoVendaEvent pagamento;

    public VendaEvent() {}

    public VendaEvent(Venda venda, Comprador comprador, List<ProdutoQuantidade> produtosQuantidades) {
        this.codigoPedido = venda.getCodigoPedido();
        this.comprador = new CompradorVendaEvent(comprador);
        this.itens = produtosQuantidades.stream().map(ItemVendaEvent::new).collect(Collectors.toList());
        this.pagamento = new PagamentoVendaEvent(venda.getPagamento());
    }

    public UUID getCodigoPedido() {
        return codigoPedido;
    }

    public CompradorVendaEvent getComprador() {
        return comprador;
    }

    public List<ItemVendaEvent> getItens() {
        return itens;
    }

    public PagamentoVendaEvent getPagamento() {
        return pagamento;
    }

}

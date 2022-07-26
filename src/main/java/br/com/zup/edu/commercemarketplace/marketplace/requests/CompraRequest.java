package br.com.zup.edu.commercemarketplace.marketplace.requests;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CompraRequest {

    @NotNull
    @Positive
    private Long idUsuario;

    @NotEmpty
    @Valid
    private List<ProdutoRequest> produtos;

    @NotNull
    @Valid
    private InformacoesPagamentoRequest pagamento;

    public CompraRequest() {
    }

    public CompraRequest(@NotNull @Positive Long idUsuario, @NotEmpty @Valid List<ProdutoRequest> produtos,
            @NotNull @Valid InformacoesPagamentoRequest pagamento) {
        this.idUsuario = idUsuario;
        this.produtos = produtos;
        this.pagamento = pagamento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public List<ProdutoRequest> getProdutos() {
        return produtos;
    }

    public InformacoesPagamentoRequest getPagamento() {
        return pagamento;
    }

}

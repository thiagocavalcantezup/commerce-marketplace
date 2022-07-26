package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID codigoPedido;

    @NotNull
    @Column(nullable = false)
    private Long idUsuario;

    @NotEmpty
    @ElementCollection
    private List<Item> itens;

    @Embedded
    @NotNull
    private Pagamento pagamento;

    /**
     * @deprecated Construtor de uso exclusivo do Hibernate
     */
    @Deprecated
    public Venda() {
    }

    public Venda(@NotNull Long idUsuario, @NotEmpty List<ProdutoQuantidade> produtoQuantidades,
            @NotNull Pagamento pagamento) {
        this.idUsuario = idUsuario;
        this.itens = produtoQuantidades.stream().map(
                produtoQuantidade -> {
                    return new Item(produtoQuantidade.getId(), produtoQuantidade.getQuantidade(),
                            produtoQuantidade.getPreco());
                }).collect(Collectors.toList());
        this.pagamento = pagamento;
    }

    public UUID getCodigoPedido() {
        return codigoPedido;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public List<Item> getItens() {
        return itens;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

}

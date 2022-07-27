package br.com.zup.edu.commercemarketplace.sistemapagamentos;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.zup.edu.commercemarketplace.marketplace.models.InformacoesPagamento;

public class PagamentoRequest {

    private String titular;
    private String numero;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth validoAte;

    private String codigoSeguranca;
    private BigDecimal valorCompra;

    public PagamentoRequest() {}

    public PagamentoRequest(InformacoesPagamento informacoesPagamento, BigDecimal valorCompra) {
        this.titular = informacoesPagamento.getTitular();
        this.numero = informacoesPagamento.getNumero();
        this.validoAte = informacoesPagamento.getValidoAte();
        this.codigoSeguranca = informacoesPagamento.getCodigoSeguranca();
        this.valorCompra = valorCompra;
    }

    public String getTitular() {
        return titular;
    }

    public String getNumero() {
        return numero;
    }

    public YearMonth getValidoAte() {
        return validoAte;
    }

    public String getCodigoSeguranca() {
        return codigoSeguranca;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

}

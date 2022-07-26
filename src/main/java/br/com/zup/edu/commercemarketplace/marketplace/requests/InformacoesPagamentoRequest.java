package br.com.zup.edu.commercemarketplace.marketplace.requests;

import java.time.YearMonth;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.zup.edu.commercemarketplace.marketplace.models.InformacoesPagamento;

public class InformacoesPagamentoRequest {

    @NotBlank
    private String titular;

    @NotBlank
    @Digits(integer = 16, fraction = 0)
    private String numero;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth validoAte;

    @NotBlank
    @Digits(integer = 3, fraction = 0)
    private Integer codigoSeguranca;

    public InformacoesPagamentoRequest() {
    }

    public InformacoesPagamentoRequest(@NotBlank String titular,
            @NotBlank @Digits(integer = 16, fraction = 0) String numero, @NotBlank YearMonth validoAte,
            @NotBlank @Digits(integer = 3, fraction = 0) Integer codigoSeguranca) {
        this.titular = titular;
        this.numero = numero;
        this.validoAte = validoAte;
        this.codigoSeguranca = codigoSeguranca;
    }

    public InformacoesPagamento toModel() {
        return new InformacoesPagamento(titular, numero, validoAte, codigoSeguranca);
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

    public Integer getCodigoSeguranca() {
        return codigoSeguranca;
    }

}

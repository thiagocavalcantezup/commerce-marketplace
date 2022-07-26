package br.com.zup.edu.commercemarketplace.sistemapagamentos;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PagamentoRequest {

    @NotBlank
    private String titular;

    @NotBlank
    private String numero;

    @NotBlank
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM")
    private LocalDate validoAte;

    @NotBlank
    @Digits(integer = 3, fraction = 0)
    private Integer codigoSeguranca;

    @NotBlank
    @Positive
    private BigDecimal valorCompra;

    public PagamentoRequest() {
    }

    public PagamentoRequest(@NotBlank String titular, @NotBlank String numero,
            @NotBlank @FutureOrPresent LocalDate validoAte,
            @NotBlank @Digits(integer = 3, fraction = 0) Integer codigoSeguranca,
            @NotBlank @Positive BigDecimal valorCompra) {
        this.titular = titular;
        this.numero = numero;
        this.validoAte = validoAte;
        this.codigoSeguranca = codigoSeguranca;
        this.valorCompra = valorCompra;
    }

    public String getTitular() {
        return titular;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getValidoAte() {
        return validoAte;
    }

    public Integer getCodigoSeguranca() {
        return codigoSeguranca;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

}

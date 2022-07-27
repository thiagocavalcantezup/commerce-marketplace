package br.com.zup.edu.commercemarketplace.marketplace.requests;

import java.time.YearMonth;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.zup.edu.commercemarketplace.marketplace.models.InformacoesPagamento;

public class InformacoesPagamentoRequest {

    private final static String MSG_NUMERO = "deve ser composto por exatamente 16 dígitos numéricos";
    private final static String MSG_CODIGO = "deve ser composto por exatamente 3 dígitos numéricos";

    @NotBlank
    private String titular;

    @NotBlank
    @Pattern(regexp = "^\\d{16}$", message = MSG_NUMERO)
    private String numero;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM")
    @FutureOrPresent
    private YearMonth validoAte;

    @NotBlank
    @Pattern(regexp = "^\\d{3}$", message = MSG_CODIGO)
    private String codigoSeguranca;

    public InformacoesPagamentoRequest() {}

    public InformacoesPagamentoRequest(@NotBlank String titular,
                                       @NotBlank @Pattern(regexp = "^\\d{16}$", message = MSG_NUMERO) String numero,
                                       @NotNull @FutureOrPresent YearMonth validoAte,
                                       @NotBlank @Pattern(regexp = "^\\d{3}$", message = MSG_CODIGO) String codigoSeguranca) {
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

    public String getCodigoSeguranca() {
        return codigoSeguranca;
    }

}

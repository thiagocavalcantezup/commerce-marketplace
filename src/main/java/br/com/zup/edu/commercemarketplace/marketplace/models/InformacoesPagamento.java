package br.com.zup.edu.commercemarketplace.marketplace.models;

import java.time.YearMonth;

public class InformacoesPagamento {

    private String titular;
    private String numero;
    private YearMonth validoAte;
    private String codigoSeguranca;

    public InformacoesPagamento() {}

    public InformacoesPagamento(String titular, String numero, YearMonth validoAte, String codigoSeguranca) {
        this.titular = titular;
        this.numero = numero;
        this.validoAte = validoAte;
        this.codigoSeguranca = codigoSeguranca;
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

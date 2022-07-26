package br.com.zup.edu.commercemarketplace.sistemapagamentos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sistemaPagamentos", url = "http://localhost:8081")
public interface SistemaPagamentosClient {

    @PostMapping("/pagamentos/credito")
    public PagamentoResponse realiza(@RequestBody PagamentoRequest request);

}

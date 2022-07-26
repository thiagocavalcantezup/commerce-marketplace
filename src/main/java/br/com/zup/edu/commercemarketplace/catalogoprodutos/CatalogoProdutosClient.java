package br.com.zup.edu.commercemarketplace.catalogoprodutos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalogoProdutos", url = "http://localhost:8082")
public interface CatalogoProdutosClient {

    @GetMapping("/produtos/{id}")
    public ProdutoResponse consulta(@PathVariable("id") Long id);

}

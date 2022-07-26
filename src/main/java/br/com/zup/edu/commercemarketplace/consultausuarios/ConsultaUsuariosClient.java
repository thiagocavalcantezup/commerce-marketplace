package br.com.zup.edu.commercemarketplace.consultausuarios;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "consultaUsuarios", url = "http://localhost:9090")
public interface ConsultaUsuariosClient {

    @GetMapping("/usuarios/{id}")
    public UsuarioResponse consulta(@PathVariable("id") Long id);

}

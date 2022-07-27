package br.com.zup.edu.commercemarketplace.consultausuarios;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "consultaUsuarios", url = "http://localhost:9090")
public interface ConsultaUsuariosClient {

    @GetMapping("/usuarios/{id}")
    public Optional<UsuarioResponse> consulta(@PathVariable("id") Long id);

}

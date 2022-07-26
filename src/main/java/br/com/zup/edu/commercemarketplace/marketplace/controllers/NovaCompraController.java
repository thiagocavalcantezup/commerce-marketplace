package br.com.zup.edu.commercemarketplace.marketplace.controllers;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zup.edu.commercemarketplace.catalogoprodutos.CatalogoProdutosClient;
import br.com.zup.edu.commercemarketplace.catalogoprodutos.ProdutoResponse;
import br.com.zup.edu.commercemarketplace.consultausuarios.ConsultaUsuariosClient;
import br.com.zup.edu.commercemarketplace.consultausuarios.UsuarioResponse;
import br.com.zup.edu.commercemarketplace.marketplace.events.VendaEvent;
import br.com.zup.edu.commercemarketplace.marketplace.models.Comprador;
import br.com.zup.edu.commercemarketplace.marketplace.models.InformacoesPagamento;
import br.com.zup.edu.commercemarketplace.marketplace.models.Pagamento;
import br.com.zup.edu.commercemarketplace.marketplace.models.ProdutoQuantidade;
import br.com.zup.edu.commercemarketplace.marketplace.models.Venda;
import br.com.zup.edu.commercemarketplace.marketplace.repositories.VendaRepository;
import br.com.zup.edu.commercemarketplace.marketplace.requests.CompraRequest;
import br.com.zup.edu.commercemarketplace.marketplace.requests.InformacoesPagamentoRequest;
import br.com.zup.edu.commercemarketplace.marketplace.requests.ProdutoRequest;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.PagamentoRequest;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.PagamentoResponse;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.SistemaPagamentosClient;
import feign.FeignException;

@RestController
public class NovaCompraController {

    private final static Logger LOGGER = LoggerFactory.getLogger(NovaCompraController.class);

    private final ConsultaUsuariosClient consultaUsuariosClient;
    private final CatalogoProdutosClient catalogoProdutosClient;
    private final SistemaPagamentosClient sistemaPagamentosClient;
    private final VendaRepository vendaRepository;
    private final KafkaTemplate<String, VendaEvent> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;

    public NovaCompraController(ConsultaUsuariosClient consultaUsuariosClient,
            CatalogoProdutosClient catalogoProdutosClient, SistemaPagamentosClient sistemaPagamentosClient,
            VendaRepository vendaRepository, KafkaTemplate<String, VendaEvent> kafkaTemplate,
            TransactionTemplate transactionTemplate) {
        this.consultaUsuariosClient = consultaUsuariosClient;
        this.catalogoProdutosClient = catalogoProdutosClient;
        this.sistemaPagamentosClient = sistemaPagamentosClient;
        this.vendaRepository = vendaRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @PostMapping("/compras")
    public ResponseEntity<?> novaCompra(@RequestBody @Valid CompraRequest compraRequest, UriComponentsBuilder ucb) {
        // Informações do comprador
        Long idUsuario = compraRequest.getIdUsuario();
        UsuarioResponse usuarioResponse = consultaUsuariosClient.consulta(idUsuario)
                .orElseThrow(() -> {
                    LOGGER.warn("Usuário com id " + idUsuario + " não encontrado");
                    return new ResponseStatusException(NOT_FOUND, "Usuário não encontrado");
                });
        Comprador comprador = usuarioResponse.toModel(idUsuario);

        // Informações dos produtos
        List<ProdutoRequest> produtoRequests = compraRequest.getProdutos();
        List<ProdutoQuantidade> produtosQuantidades = produtoRequests.stream()
                .map(produtoRequest -> {
                    Long idProduto = produtoRequest.getId();
                    ProdutoResponse produtoResponse = catalogoProdutosClient.consulta(idProduto)
                            .orElseThrow(() -> {
                                LOGGER.warn("Produto com id " + idProduto + " não encontrado");
                                return new ResponseStatusException(NOT_FOUND, "Produto não encontrado");
                            });
                    return produtoResponse.toModel(produtoRequest.getQuantidade());
                })
                .collect(Collectors.toList());

        // Valor total da compra
        BigDecimal valorTotal = produtosQuantidades.stream()
                .map(ProdutoQuantidade::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Informações de pagamento
        InformacoesPagamentoRequest informacoesPagamentoRequest = compraRequest.getPagamento();
        InformacoesPagamento informacoesPagamento = informacoesPagamentoRequest.toModel();
        PagamentoRequest pagamentoRequest = new PagamentoRequest(informacoesPagamento, valorTotal);

        PagamentoResponse pagamentoResponse;

        try {
            pagamentoResponse = sistemaPagamentosClient.realiza(pagamentoRequest);
        } catch (FeignException.BadRequest e) {
            throw new ResponseStatusException(BAD_REQUEST, "Erro na validação do pagamento");
        }

        Pagamento pagamento = pagamentoResponse.toModel();

        // Se houver um erro no banco de dados na hora de salvar a venda, o método será
        // interrompido e a mensagem não será enviada ao tópico. Por outro lado, se
        // acontecer uma
        // exceção do Kafka, será disparado um rollback do salvamento da venda. Dessa
        // forma, com a
        // transação ativa, o salvamento e a publicação no tópico sempre vão ser
        // executados juntos.
        Venda venda = transactionTemplate.execute((status) -> {
            // Venda
            //
            // A venda é salva independentemente do status do pagamento
            Venda vendaTransaction = new Venda(idUsuario, produtosQuantidades, pagamento);
            LOGGER.info("Nova venda com código " + vendaTransaction.getCodigoPedido() + " salva com sucesso.");
            vendaRepository.save(vendaTransaction);

            // Evento
            if (pagamento.foiAprovado()) {
                VendaEvent vendaEvent = new VendaEvent(vendaTransaction, comprador, produtosQuantidades);
                LOGGER.info("Pagamento com id " + pagamento.getId() + " aprovado");
                LOGGER.info("Novo evento de venda para o código " + vendaEvent.getCodigoPedido());
                kafkaTemplate.send("vendas", vendaEvent);
            } else {
                LOGGER.warn("Pagamento com id " + pagamento.getId() + " reprovado");
            }

            return vendaTransaction;
        });

        URI location = ucb.path("/vendas/{id}").buildAndExpand(venda.getCodigoPedido()).toUri();
        return ResponseEntity.created(location).build();
    }
}

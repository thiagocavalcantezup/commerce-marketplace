package br.com.zup.edu.commercemarketplace.marketplace.controllers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.edu.commercemarketplace.catalogoprodutos.CatalogoProdutosClient;
import br.com.zup.edu.commercemarketplace.catalogoprodutos.ProdutoResponse;
import br.com.zup.edu.commercemarketplace.consultausuarios.ConsultaUsuariosClient;
import br.com.zup.edu.commercemarketplace.consultausuarios.UsuarioResponse;
import br.com.zup.edu.commercemarketplace.exceptions.ErroPadrao;
import br.com.zup.edu.commercemarketplace.marketplace.events.VendaEvent;
import br.com.zup.edu.commercemarketplace.marketplace.models.Venda;
import br.com.zup.edu.commercemarketplace.marketplace.repositories.VendaRepository;
import br.com.zup.edu.commercemarketplace.marketplace.requests.CompraRequest;
import br.com.zup.edu.commercemarketplace.marketplace.requests.InformacoesPagamentoRequest;
import br.com.zup.edu.commercemarketplace.marketplace.requests.ProdutoRequest;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.PagamentoRequest;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.PagamentoResponse;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.SistemaPagamentosClient;
import br.com.zup.edu.commercemarketplace.sistemapagamentos.StatusPagamentoResponse;
import feign.FeignException;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class NovaCompraControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VendaRepository vendaRepository;

    @MockBean
    private ConsultaUsuariosClient consultaUsuariosClient;

    @MockBean
    private CatalogoProdutosClient catalogoProdutosClient;

    @MockBean
    private SistemaPagamentosClient sistemaPagamentosClient;

    @MockBean
    private KafkaTemplate<String, VendaEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        vendaRepository.deleteAll();
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComDadosNulos() throws Exception {
        // given
        CompraRequest compraRequest = new CompraRequest(null, null, null);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(3)
                             .contains(
                                 "idUsuario: não deve ser nulo", "produtos: não deve estar vazio",
                                 "pagamento: não deve ser nulo"
                             );
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComIdUsuarioInvalido() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(-1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(1).contains("idUsuario: deve ser maior que 0");
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComInformacoesDePagamentoNulas() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            null, null, null, null
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(4)
                             .contains(
                                 "pagamento.titular: não deve estar em branco",
                                 "pagamento.numero: não deve estar em branco",
                                 "pagamento.validoAte: não deve ser nulo",
                                 "pagamento.codigoSeguranca: não deve estar em branco"
                             );
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComInformacoesDePagamentoInvalidas() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "123456789", YearMonth.now().minusMonths(1), "1234"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(
            mensagens
        ).hasSize(3)
         .contains(
             "pagamento.numero: deve ser composto por exatamente 16 dígitos numéricos",
             "pagamento.validoAte: deve ser uma data no presente ou no futuro",
             "pagamento.codigoSeguranca: deve ser composto por exatamente 3 dígitos numéricos"
         );
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComInformacoesDeProdutoNulas() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(null, null), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(2)
                             .contains(
                                 "produtos[1].id: não deve ser nulo",
                                 "produtos[1].quantidade: não deve ser nulo"
                             );
    }

    @Test
    void naoDeveRealizarUmaNovaCompraComInformacoesDeProdutoInvalidas() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(-1L, -1L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(2)
                             .contains(
                                 "produtos[2].id: deve ser maior que 0",
                                 "produtos[2].quantidade: deve ser maior que 0"
                             );
    }

    @Test
    void naoDeveRealizarUmaNovaCompraCasoUsuarioNaoEstejaCadastrado() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        when(consultaUsuariosClient.consulta(compraRequest.getIdUsuario())).thenReturn(Optional.empty());

        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isNotFound())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(1).contains("Usuário não encontrado");
    }

    @Test
    void naoDeveRealizarUmaNovaCompraCasoProdutoNaoEstejaCadastrado() throws Exception {
        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        when(consultaUsuariosClient.consulta(compraRequest.getIdUsuario())).thenReturn(
            Optional.of(
                new UsuarioResponse(
                    "Teste", "12345678909", "test@example.com", "Rua X, Bairro Y",
                    LocalDate.now().minusYears(30)
                )
            )
        );
        when(catalogoProdutosClient.consulta(compraRequest.getProdutos().get(0).getId())).thenReturn(
            Optional.empty()
        );

        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isNotFound())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(1).contains("Produto não encontrado");
    }

    @Test
    void naoDeveRealizarUmaNovaCompraCasoPagamentoEstejaInvalido() throws Exception {
        BigDecimal valorUnitario = new BigDecimal("10.00");
        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(1L + 2L + 3L));

        // given
        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        when(consultaUsuariosClient.consulta(compraRequest.getIdUsuario())).thenReturn(
            Optional.of(
                new UsuarioResponse(
                    "Teste", "12345678909", "test@example.com", "Rua X, Bairro Y",
                    LocalDate.now().minusYears(30)
                )
            )
        );

        for (Long l = 1L; l <= 3L; l++) {
            when(catalogoProdutosClient.consulta(l)).thenReturn(
                Optional.of(new ProdutoResponse(l, "Teste", valorUnitario, LocalDateTime.now()))
            );
        }

        PagamentoRequest pagamentoRequest = new PagamentoRequest(
            informacoesPagamentoRequest.toModel(), valorTotal
        );
        when(sistemaPagamentosClient.realiza(refEq(pagamentoRequest))).thenThrow(
            FeignException.BadRequest.class
        );

        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        ErroPadrao erroPadrao = objectMapper.readValue(responsePayload, ErroPadrao.class);
        List<String> mensagens = erroPadrao.getMensagens();

        // then
        assertThat(mensagens).hasSize(1).contains("Erro na validação do pagamento");
    }

    @Test
    void deveRealizarUmaNovaCompraComPagamentoAprovado() throws Exception {
        // given
        BigDecimal valorUnitario = new BigDecimal("10.00");
        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(1L + 2L + 3L));
        LocalDateTime now = LocalDateTime.now();

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        UsuarioResponse usuarioResponse = new UsuarioResponse(
            "Teste", "12345678909", "test@example.com", "Rua X, Bairro Y", LocalDate.now().minusYears(30)
        );
        when(consultaUsuariosClient.consulta(compraRequest.getIdUsuario())).thenReturn(
            Optional.of(usuarioResponse)
        );

        for (Long l = 1L; l <= 3L; l++) {
            ProdutoResponse produtoResponse = new ProdutoResponse(l, "Teste", valorUnitario, now);
            when(catalogoProdutosClient.consulta(l)).thenReturn(Optional.of(produtoResponse));
        }

        PagamentoRequest pagamentoRequest = new PagamentoRequest(
            informacoesPagamentoRequest.toModel(), valorTotal
        );
        PagamentoResponse pagamentoResponse = new PagamentoResponse(
            UUID.randomUUID(), StatusPagamentoResponse.APROVADO
        );
        when(sistemaPagamentosClient.realiza(refEq(pagamentoRequest))).thenReturn(pagamentoResponse);

        mockMvc.perform(requestBuilder)
               .andExpect(status().isCreated())
               .andExpect(redirectedUrlPattern(baseUrl + "/vendas/*"));

        List<Venda> vendas = vendaRepository.findAll();

        // then
        verify(kafkaTemplate).send(eq("vendas"), any(VendaEvent.class));
        assertEquals(1, vendas.size());
    }

    @Test
    void deveRealizarUmaNovaCompraComPagamentoReprovado() throws Exception {
        // given
        BigDecimal valorUnitario = new BigDecimal("10.00");
        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(1L + 2L + 3L));
        LocalDateTime now = LocalDateTime.now();

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        List<ProdutoRequest> produtos = List.of(
            new ProdutoRequest(1L, 1L), new ProdutoRequest(2L, 2L), new ProdutoRequest(3L, 3L)
        );
        InformacoesPagamentoRequest informacoesPagamentoRequest = new InformacoesPagamentoRequest(
            "Teste", "1234567887654321", YearMonth.now().plusYears(1), "123"
        );
        CompraRequest compraRequest = new CompraRequest(1L, produtos, informacoesPagamentoRequest);
        String requestPayload = objectMapper.writeValueAsString(compraRequest);

        MockHttpServletRequestBuilder requestBuilder = post("/compras").contentType(APPLICATION_JSON)
                                                                       .content(requestPayload)
                                                                       .header("Accept-Language", "pt-br");

        // when
        UsuarioResponse usuarioResponse = new UsuarioResponse(
            "Teste", "12345678909", "test@example.com", "Rua X, Bairro Y", LocalDate.now().minusYears(30)
        );
        when(consultaUsuariosClient.consulta(compraRequest.getIdUsuario())).thenReturn(
            Optional.of(usuarioResponse)
        );

        for (Long l = 1L; l <= 3L; l++) {
            ProdutoResponse produtoResponse = new ProdutoResponse(l, "Teste", valorUnitario, now);
            when(catalogoProdutosClient.consulta(l)).thenReturn(Optional.of(produtoResponse));
        }

        PagamentoRequest pagamentoRequest = new PagamentoRequest(
            informacoesPagamentoRequest.toModel(), valorTotal
        );
        PagamentoResponse pagamentoResponse = new PagamentoResponse(
            UUID.randomUUID(), StatusPagamentoResponse.REPROVADO
        );
        when(sistemaPagamentosClient.realiza(refEq(pagamentoRequest))).thenReturn(pagamentoResponse);

        mockMvc.perform(requestBuilder)
               .andExpect(status().isCreated())
               .andExpect(redirectedUrlPattern(baseUrl + "/vendas/*"));

        List<Venda> vendas = vendaRepository.findAll();

        // then
        verify(kafkaTemplate, never()).send(eq("vendas"), any(VendaEvent.class));
        assertEquals(1, vendas.size());
    }

}

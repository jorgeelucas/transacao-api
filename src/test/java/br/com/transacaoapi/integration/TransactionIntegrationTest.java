package br.com.transacaoapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.request.TransactionPatchAmountRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.entity.TransactionEntity;
import br.com.transacaoapi.repository.impl.TransactionRepositoryInMemory;
import br.com.transacaoapi.util.TypeEnum;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TransactionRepositoryInMemory repository;

    @Autowired
    private MockMvc mockMvc;

//    @TestConfiguration
//    static class Config {
//        @Bean
//        public RestTemplateBuilder restTemplateBuilder() {
//            return new RestTemplateBuilder().basicAuthentication("admin", "admin");
//        }
//    }

    @BeforeEach
    void inicializaMocks() {
        TransactionEntity obj1 = new TransactionEntity();
        obj1.setType(TypeEnum.CREDITO);
        obj1.setDate(LocalDateTime.of(2022, 02, 10, 10, 10));

        TransactionEntity obj2 = new TransactionEntity();
        obj2.setType(TypeEnum.PIX);
        obj2.setDate(LocalDateTime.of(2022, 02, 10, 10, 10));

        TransactionEntity obj3 = new TransactionEntity();
        obj3.setType(TypeEnum.PIX);
        obj3.setDate(LocalDateTime.of(2022, 02, 10, 10, 10));

        TransactionEntity obj4 = new TransactionEntity();
        obj4.setType(TypeEnum.DEBITO);
        obj4.setDate(LocalDateTime.of(2022, 02, 9, 10, 10));

        TransactionEntity obj5 = new TransactionEntity();
        obj5.setType(TypeEnum.PIX);
        obj5.setDate(LocalDateTime.of(2022, 02, 9, 10, 10));

        TransactionEntity transacaoParaRetorno = new TransactionEntity();

        BigDecimal valorAntigo = BigDecimal.valueOf(10);

        TransactionEntity entity = new TransactionEntity();
        entity.setId(1L);
        entity.setAmount(valorAntigo);

        Mockito.when(repository.getById(Mockito.any())).thenReturn(entity);
        Mockito.doNothing().when(repository).update(Mockito.any());
        Mockito.when(repository.save(Mockito.any())).thenReturn(transacaoParaRetorno);
        Mockito.when(repository.getAll()).thenReturn(Arrays.asList(obj1, obj2,obj3,obj4, obj5));

    }

    @Test
    void testaListarTodasAsTransacoes() {
//        restTemplate.exchange("/v1/transacoes", HttpMethod.GET, HttpEntity.EMPTY, TransactionResponseDTO[].class);

        ResponseEntity<TransactionResponseDTO[]> responseEntity = restTemplate.getForEntity("/v1/transacoes",
            TransactionResponseDTO[].class);

        int statusCodeRetornado = responseEntity.getStatusCodeValue();
        TransactionResponseDTO[] dtos = responseEntity.getBody();
        int quantidade = dtos.length;

        assertEquals(statusCodeRetornado, 200);
        assertEquals(quantidade, 5);

    }

    @Test
    void testarAddTransacao() {

        AddTransactionRequestDTO requestDTO = new AddTransactionRequestDTO();
        requestDTO.setAmount(BigDecimal.valueOf(10));
        requestDTO.setType(TypeEnum.CREDITO);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/v1/transacoes", requestDTO, Void.class);

        int statusCodeRetornado = responseEntity.getStatusCodeValue();

        assertEquals(statusCodeRetornado, 201);
    }

    @Test
    void testarAlterarAmount() throws Exception {

        BigDecimal novoValor = BigDecimal.valueOf(50);
        TransactionPatchAmountRequestDTO requestDTO = new TransactionPatchAmountRequestDTO();
        requestDTO.setAmount(novoValor);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestDTOAsString = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(patch("/v1/transacoes/" + 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestDTOAsString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(novoValor))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
    }

    @Test
    void testarObterTransacoesFiltradasPeloDia() throws Exception {
        mockMvc.perform(get("/v1/transacoes")
            .param("day", "9"))
                .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(2));
    }

    @Test
    void testarObterTransacoesFiltradasPeloTipoPix() throws Exception {
        mockMvc.perform(get("/v1/transacoes?type={type}", "PIX"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(3));
    }

    @Test
    void testarObterTransacoesFiltradasPeloTipoCredito() throws Exception {
        mockMvc.perform(get("/v1/transacoes?type={type}", "CREDITO"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(1));
    }

    @Test
    void testarObterTransacoesFiltradasPeloTipoDebito() throws Exception {
        mockMvc.perform(get("/v1/transacoes?type={type}", "DEBITO"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(1));
    }

    @Test
    void testarObterTransacoesFiltradasPeloTipoEDia() throws Exception {
        mockMvc.perform(get("/v1/transacoes")
                .param("day", "9")
                .param("type", "PIX"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(1));

        mockMvc.perform(get("/v1/transacoes")
                .param("day", "10")
                .param("type", "CREDITO"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(1));

        mockMvc.perform(get("/v1/transacoes")
                .param("day", "11")
                .param("type", "DEBITO"))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("length()").value(0));
    }
}

package br.com.transacaoapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.util.Arrays;

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
import br.com.transacaoapi.repository.TransactionRepository;
import br.com.transacaoapi.util.TypeEnum;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TransactionRepository repository;

    @Autowired
    private MockMvc mockMvc;

//    @TestConfiguration
//    static class Config {
//        @Bean
//        public RestTemplateBuilder restTemplateBuilder() {
//            return new RestTemplateBuilder().basicAuthentication("admin", "admin");
//        }
//    }

    @Test
    void testaListarTodasAsTransacoes() {
//        restTemplate.exchange("/v1/transacoes", HttpMethod.GET, HttpEntity.EMPTY, TransactionResponseDTO[].class);

        TransactionEntity obj1 = new TransactionEntity();
        TransactionEntity obj2 = new TransactionEntity();
        TransactionEntity obj3 = new TransactionEntity();
        TransactionEntity obj4 = new TransactionEntity();
        Mockito.when(repository.getAll()).thenReturn(Arrays.asList(obj1, obj2,obj3,obj4));

        ResponseEntity<TransactionResponseDTO[]> responseEntity = restTemplate.getForEntity("/v1/transacoes",
            TransactionResponseDTO[].class);

        int statusCodeRetornado = responseEntity.getStatusCodeValue();
        TransactionResponseDTO[] dtos = responseEntity.getBody();
        int quantidade = dtos.length;

        assertEquals(statusCodeRetornado, 200);
        assertEquals(quantidade, 4);

    }

    @Test
    void testarAddTransacao() {
        TransactionEntity transacaoParaRetorno = new TransactionEntity();

        Mockito.when(repository.save(Mockito.any())).thenReturn(transacaoParaRetorno);

        AddTransactionRequestDTO requestDTO = new AddTransactionRequestDTO();
        requestDTO.setAmount(BigDecimal.valueOf(10));
        requestDTO.setType(TypeEnum.CREDITO);

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/v1/transacoes", requestDTO, Void.class);

        int statusCodeRetornado = responseEntity.getStatusCodeValue();

        assertEquals(statusCodeRetornado, 201);
    }

    @Test
    void testarAlterarAmount() throws Exception {
        BigDecimal valorAntigo = BigDecimal.valueOf(10);

        TransactionEntity entity = new TransactionEntity();
        entity.setId(1L);
        entity.setAmount(valorAntigo);
        // fazer mock do repository
        Mockito.when(repository.getById(Mockito.any())).thenReturn(entity);
        Mockito.doNothing().when(repository).update(Mockito.any());

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
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
    }
}

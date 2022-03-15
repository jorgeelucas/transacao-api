package br.com.transacaoapi;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.entity.TransactionEntity;
import br.com.transacaoapi.repository.impl.TransactionRepositoryInMemory;
import br.com.transacaoapi.service.TransactionService;
import br.com.transacaoapi.util.TypeEnum;
//import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService service;

    @Mock
    private TransactionRepositoryInMemory repository;

//    Mockito.mock(TransactionRepository.class);
//    mock(TransactionRepository.class);

    @BeforeEach
    void initUseCase() {
//        repository = new TransactionRepository();
//        service = new TransactionService(repository);
        TransactionEntity entity = new TransactionEntity();
        entity.setId(10L);
        Mockito.when(repository.save(Mockito.any(TransactionEntity.class))).thenReturn(entity);
    }

    @Test
    public void testaAddNovaTransacao() {
        AddTransactionRequestDTO dto = new AddTransactionRequestDTO();
        dto.setAmount(BigDecimal.valueOf(33));
        dto.setType(TypeEnum.CREDITO);

        TransactionResponseDTO saved = service.add(dto);

//        assertThat(saved.getId()).isNotNull();
        System.out.println(saved.getId());
//        assertThat(saved.getId()).isEqualTo(10L);

        Assertions.assertEquals(saved.getId(), 10L);
    }

//    @Test
//    public void testaAddNovaTransacaoComValorNegativo() {
//        AddTransactionRequestDTO dto = new AddTransactionRequestDTO();
//        dto.setAmount(BigDecimal.valueOf(-33));
//        dto.setType(TypeEnum.CREDITO);
//
//        TransactionResponseDTO saved = service.add(dto);
//
//        assertThat(saved).isNull();
//    }
}

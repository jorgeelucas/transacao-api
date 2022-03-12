package br.com.transacaoapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.transacaoapi.util.TypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionEntity {
    private Long id;
    private LocalDateTime date;
    private String orderId;
    private BigDecimal amount;
    private TypeEnum type;
}

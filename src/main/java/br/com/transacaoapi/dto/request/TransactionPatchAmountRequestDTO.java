package br.com.transacaoapi.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionPatchAmountRequestDTO {
    private BigDecimal amount;
}

package br.com.transacaoapi.util;

import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.entity.TransactionEntity;

public class ConverterUtil {

    public static TransactionResponseDTO toDTO(TransactionEntity entity) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setDate(entity.getDate());
        dto.setOrderId(entity.getOrderId());
        dto.setType(entity.getType());
        return dto;
    }

    public static TransactionEntity toEntity(AddTransactionRequestDTO dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setAmount(dto.getAmount());
        entity.setType(dto.getType());
        return entity;
    }



}

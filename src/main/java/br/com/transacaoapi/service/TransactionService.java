package br.com.transacaoapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.com.transacaoapi.dto.FilterDTO;
import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.request.TransactionPatchAmountRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.entity.TransactionEntity;
import br.com.transacaoapi.repository.TransactionRepository;
import br.com.transacaoapi.util.ConverterUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponseDTO add(AddTransactionRequestDTO addTransactionRequestDTO) {
        TransactionEntity entity = ConverterUtil.toEntity(addTransactionRequestDTO);
        entity.setOrderId(UUID.randomUUID().toString());
        entity.setDate(LocalDateTime.now());

        TransactionEntity savedEntity = transactionRepository.save(entity);

        TransactionResponseDTO dto = ConverterUtil.toDTO(savedEntity);

        return dto;
    }

    public List<TransactionResponseDTO> getAll() {
        return getAllStream()
            .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getAllFiltered(FilterDTO filter) {
        return getAllStream().filter(transaction -> {
            boolean filterType = filter.getType() == null ||
                (filter.getType() != null && transaction.getType().equals(filter.getType()));

            boolean filterDay = filter.getDay() == null ||
                (filter.getDay() != null && (filter.getDay().equals(transaction.getDate().getDayOfMonth())));

            return filterType && filterDay;
        }).collect(Collectors.toList());
    }

    public Stream<TransactionResponseDTO> getAllStream() {
        return transactionRepository.getAll().stream()
            .map(ConverterUtil::toDTO);
    }

    public TransactionResponseDTO update(Long id, TransactionPatchAmountRequestDTO dto) {

        TransactionEntity entity = transactionRepository.getById(id);
        entity.setAmount(dto.getAmount());

        transactionRepository.update(entity);

        TransactionResponseDTO responseDTO = ConverterUtil.toDTO(entity);

        return responseDTO;
    }
}

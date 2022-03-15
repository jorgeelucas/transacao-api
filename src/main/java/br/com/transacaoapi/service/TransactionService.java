package br.com.transacaoapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.transacaoapi.dto.FilterDTO;
import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.request.TransactionPatchAmountRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.entity.TransactionEntity;
import br.com.transacaoapi.repository.impl.TransacaoRepositoryH2;
import br.com.transacaoapi.util.ConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransacaoRepositoryH2 transactionRepository;

    public TransactionResponseDTO add(AddTransactionRequestDTO addTransactionRequestDTO) {
        log.info("Salvando transferencia: {}", addTransactionRequestDTO);
        TransactionEntity entity = ConverterUtil.toEntity(addTransactionRequestDTO);
        entity.setOrderId(UUID.randomUUID().toString());
        entity.setDate(LocalDateTime.now());

        TransactionEntity savedEntity = transactionRepository.save(entity);

        TransactionResponseDTO dto = ConverterUtil.toDTO(savedEntity);
        log.info("transferencia com id: {}", savedEntity.getId());
        return dto;
    }

    public List<TransactionResponseDTO> getAll() {
        return getAllStream()
            .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getAllFiltered(FilterDTO filter) {
        log.info("obter todas as transacoes filtradas: {}", filter);
        return getAllStream().filter(transaction -> {
            boolean filterType = filter.getType() == null ||
                (filter.getType() != null && transaction.getType().equals(filter.getType()));

            boolean filterDay = filter.getDay() == null ||
                (filter.getDay() != null && (filter.getDay().equals(transaction.getDate().getDayOfMonth())));

            log.info("filtrando por day e type");
            return filterType && filterDay;
        }).collect(Collectors.toList());
    }

    public Stream<TransactionResponseDTO> getAllStream() {
        return transactionRepository.findAll().stream()
            .map(ConverterUtil::toDTO);
    }

    @CacheEvict(value = "transacaoPorIdCache", key = "#id")
    public TransactionResponseDTO update(Long id, TransactionPatchAmountRequestDTO dto) {
        log.info("alterar transacao de id = {}", id);
        TransactionEntity entity = transactionRepository.getById(id);
        entity.setAmount(dto.getAmount());

        transactionRepository.save(entity);

        TransactionResponseDTO responseDTO = ConverterUtil.toDTO(entity);
        log.info("transacao de id = {} alterada com sucesso!", id);
        return responseDTO;
    }

    @Cacheable(value = "transacaoPorIdCache", key = "#id")
    public TransactionResponseDTO getById(Long id) {
        log.info("obter transacao de id = {}", id);
        TransactionEntity entity = transactionRepository.getById(id);

        TransactionResponseDTO responseDTO = ConverterUtil.toDTO(entity);

        return responseDTO;
    }
}

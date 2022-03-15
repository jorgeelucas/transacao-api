package br.com.transacaoapi.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.transacaoapi.entity.TransactionEntity;

@Component
public interface TransacaoRepository {

    TransactionEntity save(TransactionEntity transaction);
    TransactionEntity getById(Long id);
    List<TransactionEntity> getAll();

}

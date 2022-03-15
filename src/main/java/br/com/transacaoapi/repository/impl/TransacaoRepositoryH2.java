package br.com.transacaoapi.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.transacaoapi.entity.TransactionEntity;

@Repository
public interface TransacaoRepositoryH2 extends JpaRepository<TransactionEntity, Long> {
}

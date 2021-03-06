package br.com.transacaoapi.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import br.com.transacaoapi.entity.TransactionEntity;
import br.com.transacaoapi.repository.TransacaoRepository;

public class TransactionRepositoryInMemory {

    private static List<TransactionEntity> list = new ArrayList<>();
    private static Long sequence = 1L;

//    static {
//        TransactionEntity entity = new TransactionEntity();
//        entity.setDate(LocalDateTime.now().plusDays(2));
//        entity.setOrderId(UUID.randomUUID().toString());
//        entity.setAmount(BigDecimal.valueOf(21));
//        entity.setType(TypeEnum.DEBITO);
//        entity.setId(sequence++);
//
//        TransactionEntity entity1 = new TransactionEntity();
//        entity1.setDate(LocalDateTime.now().plusDays(2));
//        entity1.setOrderId(UUID.randomUUID().toString());
//        entity1.setAmount(BigDecimal.valueOf(33));
//        entity1.setType(TypeEnum.CREDITO);
//        entity1.setId(sequence++);
//
//        TransactionEntity entity2 = new TransactionEntity();
//        entity2.setDate(LocalDateTime.now());
//        entity2.setOrderId(UUID.randomUUID().toString());
//        entity2.setAmount(BigDecimal.valueOf(322));
//        entity2.setType(TypeEnum.CREDITO);
//        entity2.setId(sequence++);
//
//        TransactionEntity entity3 = new TransactionEntity();
//        entity3.setDate(LocalDateTime.now());
//        entity3.setOrderId(UUID.randomUUID().toString());
//        entity3.setAmount(BigDecimal.valueOf(33));
//        entity3.setType(TypeEnum.PIX);
//        entity3.setId(sequence++);
//
//        list.addAll(Arrays.asList(entity, entity1, entity2, entity3));
//    }

    public TransactionEntity save(TransactionEntity entity) {
        entity.setId(sequence++);
        list.add(entity);
        return entity;
    }

    public List<TransactionEntity> getAll() {
        return list;
    }

    @Cacheable(value = "transacaoEntityPorIdCache", key = "#id")
    public TransactionEntity getById(Long id) {
        System.out.println("BUSCANDO NO BANCO DE DADOS...");
        simularLatenciaDeBanco();
        return list.stream()
            .filter(transacao -> transacao.getId().equals(id))
            .findFirst()
            .get();
    }

    private void simularLatenciaDeBanco() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(TransactionEntity entity) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(entity.getId())) {
                list.get(i).setAmount(entity.getAmount());
                return;
            }
        }
    }
}

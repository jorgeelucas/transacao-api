package br.com.transacaoapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.transacaoapi.dto.FilterDTO;
import br.com.transacaoapi.dto.request.AddTransactionRequestDTO;
import br.com.transacaoapi.dto.request.TransactionPatchAmountRequestDTO;
import br.com.transacaoapi.dto.response.TransactionResponseDTO;
import br.com.transacaoapi.service.TransactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Void> addTransaction(@RequestBody AddTransactionRequestDTO addTransactionRequestDTO) {
        TransactionResponseDTO response = transactionService.add(addTransactionRequestDTO);
        return ResponseEntity
            .created(URI.create("http://localhost:8080/v1/transacoes/" + response.getId()))
            .build();
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactionsFiltered(FilterDTO filter) {
        List<TransactionResponseDTO> responseList = transactionService.getAllFiltered(filter);
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> patchTransactionAmount(
        @PathVariable Long id,
        @RequestBody TransactionPatchAmountRequestDTO transactionPatchAmountRequestDTO) {

        TransactionResponseDTO responseDTO = transactionService.update(id, transactionPatchAmountRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }

}

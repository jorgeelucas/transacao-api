package br.com.transacaoapi.dto.request;

import java.math.BigDecimal;

import br.com.transacaoapi.util.TypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTransactionRequestDTO {

    private BigDecimal amount;
    private TypeEnum type;

}

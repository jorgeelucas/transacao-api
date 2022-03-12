package br.com.transacaoapi.dto;

import br.com.transacaoapi.util.TypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterDTO {
    private Integer day;
    private TypeEnum type;
}

package br.com.transacaoapi.dto;

import br.com.transacaoapi.util.TypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FilterDTO {
    private Integer day;
    private TypeEnum type;
}

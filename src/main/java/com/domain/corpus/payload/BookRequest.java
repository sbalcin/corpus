package com.domain.corpus.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {

    @NotBlank
    @Size(min = 2)
    private String name;

    private String author;

    private Date publish;

    private BigDecimal price;

}

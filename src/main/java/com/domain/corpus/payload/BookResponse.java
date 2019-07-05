package com.domain.corpus.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookResponse {

    private Long id;
    private String name;
    private String author;
    private Date publish;
    private BigDecimal price;

}

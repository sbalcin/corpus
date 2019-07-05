package com.domain.corpus.model.book;

import com.domain.corpus.model.audit.UserDateAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "name", "author ", "publish "
        })
})
public class Book extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(name = "author")
    private String author;

    @Column(name = "publish")
    private Date publish;

    @Column(name = "price")
    private BigDecimal price;

    public Book(@NotBlank String name, String author, Date publish, BigDecimal price) {
        this.name = name;
        this.author = author;
        this.publish = publish;
        this.price = price;
    }
}

package com.domain.corpus.repository;

import com.domain.corpus.model.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    Optional<Book> findByName(String name);

    long countById(Long Id);

    Page<Book> findByAuthorIsContaining(String author, Pageable pageable);

    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

}

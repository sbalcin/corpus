package com.domain.corpus.service;

import com.domain.corpus.CorpusApplication;
import com.domain.corpus.model.book.Book;
import com.domain.corpus.repository.BookRepository;
import com.domain.corpus.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CorpusApplication.class)
@AutoConfigureMockMvc
public class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;


    @Before
    public void setup() throws Exception {

    }

    private Book createBook() {
        Book book = new Book();
        book.setName("Practical Java Machine Learning");
        book.setAuthor("Mark Wickham");
        book.setPublish(DateUtil.addDayToDate(new Date(), -10));
        book.setPrice(new BigDecimal(7.99));
        return book;
    }


    @Test
    public void shouldInsertBook() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);
        Optional<Book> retrieved = bookRepository.findByName(savedBook.getName());
        assertThat(retrieved.get()).isEqualTo(savedBook);
    }

    @Test
    public void shouldFindBookById() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);
        Optional<Book> saved = bookRepository.findById(savedBook.getId());
        assertThat(saved.get().getName()).isEqualTo(book.getName());
        assertThat(saved.get().getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    public void shouldFindBookByAuthor() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);

        Pageable pageable = PageRequest.of(0, 30, Sort.Direction.DESC, "createdAt");

        Page<Book> retrieved = bookRepository.findByAuthorIsContaining(savedBook.getAuthor(), pageable);
        for (Book item : retrieved.getContent()) {
            assertThat(item.getName()).isEqualTo(book.getName());
            assertThat(item.getAuthor()).isEqualTo(book.getAuthor());
        }
    }

    @Test
    public void shouldFindBookByPriceRangeAuthor() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);

        Pageable pageable = PageRequest.of(0, 30, Sort.Direction.DESC, "createdAt");

        Page<Book> retrieved = bookRepository.findByPriceBetween(new BigDecimal(5), new BigDecimal(10), pageable);
        for (Book item : retrieved.getContent()) {
            assertThat(item.getName()).isEqualTo(book.getName());
            assertThat(item.getAuthor()).isEqualTo(book.getAuthor());
        }
    }

    @Test
    public void shouldUpdateBook() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);
        Optional<Book> retrieved = bookRepository.findById(savedBook.getId());

        String name = savedBook.getName() + " LTD";
        retrieved.get().setName(name);

        bookRepository.save(retrieved.get());
        retrieved = bookRepository.findById(retrieved.get().getId());
        assertThat(retrieved.get().getName()).isEqualTo(name);
    }

    @Test
    public void shouldDeleteBook() {
        Book book = createBook();
        Book savedBook = bookRepository.save(book);
        bookRepository.delete(savedBook);

        long rowCount = bookRepository.countById(savedBook.getId());

        assertThat(rowCount).isEqualTo(0);
    }
}

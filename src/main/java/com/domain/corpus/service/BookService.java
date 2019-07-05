package com.domain.corpus.service;

import com.domain.corpus.exception.ResourceNotFoundException;
import com.domain.corpus.model.book.Book;
import com.domain.corpus.payload.ApiResponse;
import com.domain.corpus.payload.BookRequest;
import com.domain.corpus.payload.BookResponse;
import com.domain.corpus.payload.PagedResponse;
import com.domain.corpus.repository.BookRepository;
import com.domain.corpus.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class BookService extends BaseService {

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    public PagedResponse<BookResponse> getAllBooks(int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Book> items = bookRepository.findAll(pageable);

        List<BookResponse> responses = new ArrayList<>();
        for (Book book : items.getContent()) {
            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
            responses.add(bookResponse);
        }

        if (items.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
        }
        return new PagedResponse<>(responses, items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
    }

    public ResponseEntity<?> getBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    public PagedResponse<BookResponse> getBookByAuthor(String author, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Book> items = bookRepository.findByAuthorIsContaining(author, pageable);

        List<BookResponse> responses = new ArrayList<>();
        for (Book book : items.getContent()) {
            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
            responses.add(bookResponse);
        }

        if (items.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
        }
        return new PagedResponse<>(responses, items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
    }

    public PagedResponse<BookResponse> getBookByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Book> items = bookRepository.findByPriceBetween(minPrice, maxPrice, pageable);

        List<BookResponse> responses = new ArrayList<>();
        for (Book book : items.getContent()) {
            BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
            responses.add(bookResponse);
        }

        if (items.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
        }
        return new PagedResponse<>(responses, items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
    }

    @Transactional
    public ResponseEntity<?> updateBook(Long id, BookRequest request, UserPrincipal currentUser) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        book.setName(request.getName());
        book.setAuthor(request.getAuthor());
        book.setPublish(request.getPublish());
        book.setPrice(request.getPrice());
        Book updatedBook = bookRepository.save(book);
        BookResponse bookResponse = modelMapper.map(updatedBook, BookResponse.class);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addBook(BookRequest request, UserPrincipal currentUser) {
        Book book = modelMapper.map(request, Book.class);
        Book newBook = bookRepository.save(book);
        BookResponse bookResponse = modelMapper.map(newBook, BookResponse.class);
        return new ResponseEntity<>(bookResponse, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteBook(Long id, UserPrincipal currentUser) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.deleteById(id);
        return new ResponseEntity<>(new ApiResponse(true, "Book deleted successfully"), HttpStatus.OK);
    }
}

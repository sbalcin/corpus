package com.domain.corpus.controller;

import com.domain.corpus.payload.BookResponse;
import com.domain.corpus.payload.PagedResponse;
import com.domain.corpus.util.AppConstants;
import com.domain.corpus.payload.BookRequest;
import com.domain.corpus.security.CurrentUser;
import com.domain.corpus.security.UserPrincipal;
import com.domain.corpus.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public PagedResponse<BookResponse> getAllBooks(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return bookService.getAllBooks(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id) {
        return bookService.getBook(id);
    }

    @GetMapping("/author")
    public PagedResponse<BookResponse> getBookByAuthor(@RequestParam(name = "author") String author,
                                             @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                             @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return bookService.getBookByAuthor(author, page, size);
    }

    @GetMapping("/price")
    public PagedResponse<BookResponse> getBookByPrice(@RequestParam(name = "minPrice") BigDecimal minPrice, @RequestParam(name = "maxPrice") BigDecimal maxPrice,
                                                      @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                      @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return bookService.getBookByPrice(minPrice, maxPrice, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addBook(@Valid @RequestBody BookRequest request, @CurrentUser UserPrincipal currentUser) {
        return bookService.addBook(request, currentUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable(name = "id") Long id, @Valid @RequestBody BookRequest request, @CurrentUser UserPrincipal currentUser) {
        return bookService.updateBook(id, request, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        return bookService.deleteBook(id, currentUser);
    }
}

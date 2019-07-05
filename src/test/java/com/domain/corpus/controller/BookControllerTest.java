package com.domain.corpus.controller;

import com.domain.corpus.CorpusApplication;
import com.domain.corpus.model.book.Book;
import com.domain.corpus.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CorpusApplication.class)
@AutoConfigureMockMvc
public class BookControllerTest extends BaseTest {

	private Book book = null;

	private Book createBook() {
		Book book = new Book();
		book.setName("Practical Java Machine Learning");
		book.setAuthor("Mark Wickham");
		book.setPublish(DateUtil.addDayToDate(new Date(), -10));
		book.setPrice(new BigDecimal(7.99));
		return book;
	}

	@Test
	public void add_book_is_ok() throws Exception {
		book = createBook();

		mockMvc.perform(post("/api/v1/books")
				.content(mapper.writeValueAsString(book))
				.contentType(contentType)
				.headers(getAuthHeader(token)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is(book.getName())))
				.andExpect(jsonPath("$.author", is(book.getAuthor())))
				.andExpect(jsonPath("$.price", is(book.getPrice())));
	}

	@Test
	public void find_book_by_id_not_found() throws Exception {
		mockMvc.perform(get("/api/v1/books/404")).andExpect(status().isNotFound());
	}

	@Test
	public void find_book_by_id_is_ok() throws Exception {
		book = createBook();
		Book saved = bookRepository.save(book);

		mockMvc.perform(get("/api/v1/books/" + saved.getId()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(book.getName())))
				.andExpect(jsonPath("$.author", is(book.getAuthor())));
	}

	@Test
	public void find_book_by_author_is_ok() throws Exception {
		book = createBook();
		book.setAuthor("John");
		Book saved = bookRepository.save(book);

		mockMvc.perform(get("/api/v1/books/author?author=" + saved.getAuthor()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				//.andExpect(jsonPath("$.*", hasSize(1)))
				.andExpect(status().isOk());
	}

	@Test
	public void find_book_by_price_range_is_ok() throws Exception {
		book = createBook();
		book.setPrice(new BigDecimal(100));
		Book saved = bookRepository.save(book);

		mockMvc.perform(get("/api/v1/books/price?minPrice=99&maxPrice=100"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				//.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(status().isOk());
	}

	@Test
	public void find_all_books_is_ok() throws Exception {
		book = createBook();
		bookRepository.save(book);

		mockMvc.perform(get("/api/v1/books"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void update_book_is_ok() throws Exception {
		book = createBook();
		Book saved = bookRepository.save(book);

		mockMvc.perform(put("/api/v1/books/" + saved.getId()).content(mapper.writeValueAsString(book))
				.content(mapper.writeValueAsString(book))
				.contentType(contentType)
				.headers(getAuthHeader(token)))
				.andDo(print())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(book.getName())))
				.andExpect(jsonPath("$.author", is(book.getAuthor())))
				.andExpect(jsonPath("$.price", is(book.getPrice())));
	}

	@Test
	public void delete_book_is_ok() throws Exception {
		book = createBook();
		Book saved = bookRepository.save(book);

		mockMvc.perform(delete("/api/v1/books/" + saved.getId())
				.contentType(contentType)
				.headers(getAuthHeader(token)))
                .andDo(print())
				.andExpect(status().isOk());

	}

}
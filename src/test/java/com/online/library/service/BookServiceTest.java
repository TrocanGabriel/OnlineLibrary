package com.online.library.service;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.Book;
import com.online.library.model.dto.BookDTO;
import com.online.library.repository.BookRepository;
import com.online.library.utils.PopulatedValidBook;
import com.online.library.utils.PopulatedValidBookDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
public class BookServiceTest {

    private final Book book = new PopulatedValidBook();

    private final BookDTO bookDTO = new PopulatedValidBookDTO();

    @InjectMocks
    private final BookService bookService = new BookService();

    @MockBean
    private BookRepository bookRepository;

    @Before
    public void before(){
        initMocks(this);
    }

    @Test
    public void whenGettingBookByIdExpectResult() {
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        BookDTO newBook = bookService.get(book.getId());
        assertThat(newBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(newBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(newBook.getId()).isEqualTo(book.getId());
        verify(bookRepository).findById(book.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void whenGettingBookByIdExpectNull() {
        Long id = 1L;
        when(bookRepository.getById(id)).thenReturn(null);
        bookService.get(id);
    }

    @Test
    public void whenGettingBooksExpectResult() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        List<BookDTO> newBooks = bookService.getBooks();
        assertThat(newBooks.size()).isEqualTo(1);
        assertThat(newBooks.get(0).getId()).isEqualTo(book.getId());
        assertThat(newBooks.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(newBooks.get(0).getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    public void whenGettingBooksExpectEmpty() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        List<BookDTO> newBooks = bookService.getBooks();
        assertThat(newBooks).isEmpty();
    }

    @Test
    public void whenCreateBookExpectResult() {
        bookDTO.setId(null);
        when(bookRepository.save(any())).thenReturn(book);
        BookDTO result = bookService.create(bookDTO);
        assertThat(result.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(result.getAuthor()).isEqualTo(bookDTO.getAuthor());
        assertThat(result.getId()).isEqualTo(book.getId());
    }

    @Test
    public void whenUpdateBookExpectResult() {
        when(bookRepository.existsById(any())).thenReturn(true);
        when(bookRepository.save(any())).thenReturn(book);
        when(bookRepository.getById(any())).thenReturn(book);
        BookDTO result = bookService.update(bookDTO);
        assertThat(result.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(result.getAuthor()).isEqualTo(bookDTO.getAuthor());
        assertThat(result.getId()).isEqualTo(bookDTO.getId());
    }

    @Test
    public void whenUpdateBookExpectNull() {
        when(bookRepository.existsById(any())).thenReturn(false);
        BookDTO result = bookService.update(bookDTO);
        assertThat(result).isNull();
    }

    @Test
    public void whenDeleteBookExpectResult() {
        bookService.delete(book.getId());
        verify(bookRepository).deleteById(book.getId());
    }

    @Test
    public void whenIncrementBookCopyExpectResult() {
        book.setNumberOfCopies(book.getNumberOfCopies()+1);
        when(bookRepository.findById(bookDTO.getId())).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).incrementBookCopies(bookDTO.getId());
        BookDTO result = bookService.addBookCopy(bookDTO);
        assertThat(result.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(result.getAuthor()).isEqualTo(bookDTO.getAuthor());
        assertThat(result.getId()).isEqualTo(bookDTO.getId());
        assertThat(result.getNumberOfCopies()).isGreaterThan(bookDTO.getNumberOfCopies());
        verify(bookRepository).incrementBookCopies(any());
    }

    @Test
    public void whenDecrementBookCopyExpectResult() {
        Book resultBook = book;
        resultBook.setNumberOfCopies(book.getNumberOfCopies()-1);
        when(bookRepository.getById(bookDTO.getId())).thenReturn(book);
        when(bookRepository.findById(bookDTO.getId())).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).decrementBookCopies(bookDTO.getId());
        BookDTO result = bookService.loanBook(bookDTO.getId());
        assertThat(result.getTitle()).isEqualTo(bookDTO.getTitle());
        assertThat(result.getAuthor()).isEqualTo(bookDTO.getAuthor());
        assertThat(result.getId()).isEqualTo(bookDTO.getId());
        assertThat(result.getNumberOfCopies()).isLessThan(bookDTO.getNumberOfCopies());
        verify(bookRepository).decrementBookCopies(any());
    }

}

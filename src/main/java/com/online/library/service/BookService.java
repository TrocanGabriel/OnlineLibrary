package com.online.library.service;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.Book;
import com.online.library.model.dto.BookDTO;
import com.online.library.repository.BookRepository;
import com.online.library.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookDTO get(Long bookId) {
        log.debug("Finding book by id {}!", bookId);
        return bookRepository.findById(bookId)
                .map(this::toBookDTO)
                .orElseThrow(() -> new RecordNotFoundException(
                        Constants.RECORD_NOT_FOUND_EXCEPTION));
    }

    private <U> BookDTO toBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        BeanUtils.copyProperties(book,bookDTO);
        return bookDTO;
    }


    public List<BookDTO> getBooks() {
        log.debug("Getting all books!");
        List<Book> books = bookRepository.findAll();
        List<BookDTO> bookDTOS = new ArrayList();
        for (Book book : books) {
            BookDTO bookDTO = new BookDTO();
            BeanUtils.copyProperties(book, bookDTO);
            bookDTOS.add(bookDTO);
        }
        return bookDTOS;
    }

    public BookDTO create(BookDTO bookDTO) {
        log.debug("Add new book with title {} from {}!", bookDTO.getTitle(), bookDTO.getAuthor());
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        BeanUtils.copyProperties(bookRepository.save(book), bookDTO);
        return bookDTO;
    }


    public BookDTO findDuplicate(BookDTO bookDTO) {
        log.debug("Check if customer to be added already exists!");
        Optional<Book> duplicateBook = bookRepository.findBookByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor());
        if(duplicateBook.isPresent()) {
            BeanUtils.copyProperties(duplicateBook.get(), bookDTO);
            return bookDTO;
        }
        return null;
    }

    public BookDTO update(BookDTO bookDTO) {
        log.debug("Updating book with id {}!", bookDTO.getId());
        if (bookRepository.existsById(bookDTO.getId())) {
            Book book = bookRepository.getById(bookDTO.getId());
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            BeanUtils.copyProperties(bookRepository.save(book), bookDTO);
            return bookDTO;
        }
        return null;

    }

    public void delete(Long bookId) {
        log.debug("Delete book with id {}!", bookId);
        bookRepository.deleteById(bookId);
    }

    @Transactional
    public BookDTO addBookCopy(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        bookRepository.incrementBookCopies(book.getId());
        return bookRepository.findById(book.getId())
                .map(this::toBookDTO)
                .orElseThrow(() -> new RecordNotFoundException(
                        Constants.RECORD_NOT_FOUND_EXCEPTION));
    }

    @Transactional
    public BookDTO loanBook(Long id) {
        Book book = bookRepository.getById(id);
        if(book.getNumberOfCopies() > 0) {
            bookRepository.decrementBookCopies(id);
            return bookRepository.findById(id)
                    .map(this::toBookDTO)
                    .orElseThrow(() -> new RecordNotFoundException(
                            Constants.RECORD_NOT_FOUND_EXCEPTION));
        } else {
            throw new RecordNotFoundException(Constants.BOOK_COPIES_NOT_FOUND_EXCEPTION);
        }
    }
}

package com.online.library.controller;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.dto.BookDTO;
import com.online.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.online.library.utils.Constants.RECORD_NOT_FOUND_EXCEPTION;

@RestController
@Slf4j
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDTO> get(@PathVariable("bookId") Long bookId){

        log.info("Get bookDTO with id {}", bookId);

        BookDTO bookDTO = bookService.get(bookId);
        if(bookDTO != null) {
            log.info("BookDTO details retrieved successful for bookDTO id {}", bookId);
            return ResponseEntity.status(HttpStatus.OK).body(bookDTO);
        } else {
            log.info("BookDTO not found with id {}", bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<List<BookDTO>> getBooks() {
        log.info("Get books list!");

        List<BookDTO> books;
        books = bookService.getBooks();

        return ResponseEntity.status(HttpStatus.OK).body(books);
    }

    @PostMapping("")
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO bookDTO){
        log.info("Create a new bookDTO with title {} written by {}", bookDTO.getTitle(), bookDTO.getAuthor());

        BookDTO existingBook = bookService.findDuplicate(bookDTO);
        if(existingBook != null){
            log.info("The bookDTO {} already exists in our database!", bookDTO.getTitle());
            return ResponseEntity.status(HttpStatus.OK).body(bookService.addBookCopy(existingBook));
        }

        BookDTO addedBook = bookService.create(bookDTO);
        log.info("BookDTO with title {} was added successfully", bookDTO.getTitle());

        return ResponseEntity.status(HttpStatus.OK).body(addedBook);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookDTO> update(@RequestBody BookDTO bookDTO, @PathVariable("bookId") Long bookId) {
        log.info("BookDTO with id {} is being updated", bookId);

        bookDTO.setId(bookId);
        BookDTO updatedBook = bookService.update(bookDTO);

        if(updatedBook == null){
            log.info("BookDTO with id {} was not found!", bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("BookDTO with id {} updated successfully!", updatedBook.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
        }
    }

    @GetMapping("/loan/{bookId}")
    public ResponseEntity<BookDTO> loanBook(@PathVariable("bookId") Long bookId) {
        log.info("BookDTO with id {} is being loaned by a customer", bookId);

        BookDTO updatedBook = bookService.loanBook(bookId);

        if(updatedBook == null){
            log.info("BookDTO with id {} was not found!", bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("BookDTO with id {} loaned successfully!", updatedBook.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
        }
    }


    @GetMapping("/return/{bookId}")
    public ResponseEntity<BookDTO> returnBook(@PathVariable("bookId") Long bookId) {
        log.info("BookDTO with id {} is being returned by a customer", bookId);

        BookDTO existingBook = bookService.get(bookId);
        BookDTO updatedBook = bookService.addBookCopy(existingBook);

        if(updatedBook == null){
            log.info("BookDTO with id {} was not found!", bookId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("BookDTO with id {} returned successfully!", updatedBook.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
        }
    }


    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable("bookId") Long bookId) {
        log.info("Delete bookDTO with id {}!", bookId);

        if(bookService.get(bookId) == null) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_EXCEPTION);
        }
        bookService.delete(bookId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}

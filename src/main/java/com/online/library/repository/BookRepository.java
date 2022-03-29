package com.online.library.repository;

import com.online.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository  extends JpaRepository<Book, Long> {
    Optional<Book> findBookByTitleAndAuthor(String title, String author);

    @Modifying
    @Query("UPDATE Book book set book.numberOfCopies = book.numberOfCopies + 1 WHERE book.id = :id")
    void incrementBookCopies(Long id);

    @Modifying
    @Query("UPDATE Book book set book.numberOfCopies = book.numberOfCopies - 1 WHERE book.id = :id")
    void decrementBookCopies(Long id);
}

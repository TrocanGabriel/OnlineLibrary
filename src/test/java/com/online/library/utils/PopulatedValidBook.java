package com.online.library.utils;

import com.online.library.model.Book;

public class PopulatedValidBook extends Book {

    public PopulatedValidBook() {
        setAuthor("Test author");
        setId(1L);
        setTitle("Test Title");
        setNumberOfCopies(2);
    }
}

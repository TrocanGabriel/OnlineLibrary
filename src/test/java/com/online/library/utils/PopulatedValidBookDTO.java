package com.online.library.utils;

import com.online.library.model.dto.BookDTO;

public class PopulatedValidBookDTO extends BookDTO {


    public PopulatedValidBookDTO() {
        setAuthor("Test author");
        setId(1L);
        setTitle("Test Title");
        setNumberOfCopies(2);
    }
}

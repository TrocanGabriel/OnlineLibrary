package com.online.library.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class BookDTO {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    @Builder.Default
    private Integer numberOfCopies = 1;

}

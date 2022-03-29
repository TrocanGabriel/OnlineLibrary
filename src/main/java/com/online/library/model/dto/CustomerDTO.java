package com.online.library.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Customer must have an email specified!")
    @Size(max = 50)
    @Email(message = "Please specify a valid email address!")
    private String email;

    @NotBlank(message = "Customer must have a name specified!")
    @Size(min = 2, max = 50)
    private String name;

}

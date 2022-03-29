package com.online.library.exception;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private List<String> details;

}

package com.online.library.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Loan {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "book_id")
    Book book;
}

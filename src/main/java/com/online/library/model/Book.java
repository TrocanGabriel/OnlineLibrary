package com.online.library.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"loans"})
@EqualsAndHashCode(exclude = {"loans"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "integer default 1", nullable = false)
    @Builder.Default
    private Integer numberOfCopies = 1;

    @OneToMany(mappedBy = "book")
    private Set<Loan> loans;

}

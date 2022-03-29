package com.online.library.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"loans"})
@EqualsAndHashCode(exclude = {"loans"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique=true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "customer")
    private Set<Loan> loans;

    public Customer(String email, String name, String encode) {
        this.email = email;
        this.name = name;
        this.password = encode;
    }
}

package ee.sandra.wpc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password; // hiljem lisame Spring Security + BCrypt

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
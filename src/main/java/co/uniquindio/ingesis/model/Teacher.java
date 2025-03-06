package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "profesor")
public class Teacher {

    //_____________FIELDS__________________//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Integer id;

    @Column(name = "documento_profesor", nullable = false, unique = true)
    private String cedula;

    @Column(name = "nombre_profesor", nullable = false)
    private String name;

    @Column(name = "email_profesor", unique = true)
    private String email;

    @Column(name = "password_profesor", nullable = false)
    private String password;

}


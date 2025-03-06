package co.uniquindio.ingesis.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "profesor")
public class Teacher implements Serializable {

    //_____________FIELDS__________________//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Integer id;

    @Column(name = "documento_profesor", nullable = false, updatable = false, unique = true)
    private String cedula;

    @Column(name = "nombre_profesor", nullable = false, updatable = false)
    private String nombre;

    @Column(name = "email_profesor", nullable = false, unique = true)
    private String email;

    @Column(name = "password_profesor", nullable = false)
    private String password;

}


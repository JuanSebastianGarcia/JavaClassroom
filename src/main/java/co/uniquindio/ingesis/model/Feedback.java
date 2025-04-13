package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "Coementario")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Teacher Teacher;

    @ManyToOne(optional = false)
    private Program program;

    private String comentario;
    private LocalDateTime fecha;

    // Getters y setters
    // Constructor vacío y con parámetros
}
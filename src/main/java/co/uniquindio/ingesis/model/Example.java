package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a programming example created by a professor.
 * 
 * Each example contains metadata such as title, content, category, and
 * difficulty,
 * and is associated with the professor who created it.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ejemplo")
public class Example {

    /**
     * Unique identifier for the example.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplo")
    private Integer id;

    /**
     * Title of the example.
     * Cannot be null.
     */
    @Column(name = "titulo_ejemplo", nullable = false)
    private String title;

    /**
     * Detailed content or description of the example.
     * Stored as a large object (LOB) in the database.
     */
    @Lob
    @Column(name = "contenido_ejemplo", nullable = false)
    private String content;

    /**
     * Category of the example, such as "Loops", "Arrays", etc.
     * Cannot be null.
     */
    @Column(name = "categoria_ejemplo", nullable = false)
    private String category;

    /**
     * Difficulty level of the example.
     * This is typically an integer value (e.g., 1 = Easy, 2 = Medium, 3 = Hard).
     */
    @Column(name = "dificultad_ejemplo", nullable = false)
    private Integer difficulty;

    /**
     * Professor's ID (cedula) who created the example.
     * This is used to associate the example with the author.
     */
    @Column(name = "cedula_profesor", nullable = false)
    private String cedulaProfesor;
}

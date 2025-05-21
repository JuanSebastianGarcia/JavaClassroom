package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing feedback (comments) made by a teacher on a program.
 * 
 * Each feedback entry includes the teacher who wrote it, the program it's
 * related to,
 * the comment itself, and the date/time it was submitted.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comentario") // fixed typo: Coementario → comentario
public class Feedback {

    /**
     * Unique identifier for the feedback entry.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The teacher who made the comment.
     * This is a required many-to-one relationship with the {@link Teacher} entity.
     */
    @ManyToOne(optional = false)
    private Teacher teacher; // fixed variable name to follow Java naming conventions

    /**
     * The program that the comment is related to.
     * This is a required many-to-one relationship with the {@link Program} entity.
     */
    @ManyToOne(optional = false)
    private Program program;

    /**
     * The textual content of the feedback.
     */
    private String comentario;

    /**
     * The date and time when the feedback was submitted.
     */
    private LocalDateTime fecha;
}

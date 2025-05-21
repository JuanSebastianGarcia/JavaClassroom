package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing the assignment of a program to a student.
 * It includes information about the assigned student, the program,
 * whether it has been resolved, and which teacher resolved it (if any).
 */
@Entity
@Table(name = "student_program")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgram {

    /**
     * Unique ID of the student-program assignment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Reference to the student who is assigned the program.
     */
    @ManyToOne(optional = false)
    private Student student;

    /**
     * Reference to the program assigned to the student.
     */
    @ManyToOne(optional = false)
    private Program program;

    /**
     * Reference to the teacher who resolved the program (optional).
     */
    @ManyToOne
    @JoinColumn(name = "resolvedBy_id_profesor", nullable = true)
    private Teacher resolvedBy;

    /**
     * Indicates whether the student has marked the program as resolved.
     */
    @Column(name = "resolved", nullable = false)
    private boolean resolved;
}

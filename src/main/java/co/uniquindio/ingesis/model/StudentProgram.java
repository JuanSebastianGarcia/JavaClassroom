package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_program")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "resolvedBy_id_profesor", nullable = true)
    private Teacher resolvedBy;

    @Column(name = "resolved", nullable = false)
    private boolean resolved;

}
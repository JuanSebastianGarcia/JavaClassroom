package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the assignment of an example to a student.
 * 
 * This entity links a specific {@link Example} with a student's ID (cedula),
 * indicating that the example has been assigned to or is associated with that
 * student.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asignacion_ejemplo")
public class ExampleAssignment {

    /**
     * Unique identifier for the example assignment.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer id;

    /**
     * The example being assigned to the student.
     * This establishes a many-to-one relationship with the {@link Example} entity.
     */
    @ManyToOne
    @JoinColumn(name = "id_ejemplo", nullable = false)
    private Example example;

    /**
     * The student’s ID (cedula) to whom the example is assigned.
     * This is a reference to the student's unique identifier.
     */
    @Column(name = "cedula_estudiante", nullable = false)
    private String cedulaEstudiante;
}

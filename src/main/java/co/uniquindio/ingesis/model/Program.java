package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Java program submitted by a student.
 * 
 * This entity stores metadata about the program, such as its unique code,
 * name, description, storage path for source code, the student who created it,
 * and whether it has been shared with others.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "programa")
public class Program {

    /**
     * Unique identifier for the program.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    private Integer id;

    /**
     * Unique code assigned to the program.
     * Used to identify the program in listings and references.
     */
    @Column(name = "codigo_programa", nullable = false, unique = true)
    private String code;

    /**
     * Display name of the program.
     */
    @Column(name = "nombre_programa", nullable = false)
    private String name;

    /**
     * Detailed description of what the program does.
     */
    @Column(name = "descripcion_programa", nullable = false)
    private String description;

    /**
     * File system path where the Java source code files for this program are
     * stored.
     */
    @Column(name = "ruta_codigo_fuente", nullable = false)
    private String sourceCodePath;

    /**
     * ID of the student who submitted or created the program.
     * Only stores the numeric identifier of the student.
     */
    @Column(name = "id_estudiante", nullable = false)
    private Integer studentId;

    /**
     * Indicates whether the program has been shared (visible to others).
     * Defaults to false.
     */
    @Column(name = "compartido", nullable = false)
    private boolean shared = false;
}

package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asignacion_ejemplo")
public class ExampleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_ejemplo", nullable = false)
    private Example example;

    @Column(name = "cedula_estudiante", nullable = false)
    private String cedulaEstudiante;
}
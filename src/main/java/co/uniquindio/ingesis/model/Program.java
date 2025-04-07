package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "programa")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    private Integer id;

    @Column(name = "codigo_programa", nullable = false, unique = true)
    private String code;

    @Column(name = "nombre_programa", nullable = false)
    private String name;

    @Column(name = "descripcion_programa", nullable = false)
    private String description;

    @Column(name = "ruta_codigo_fuente", nullable = false)
    private String sourceCodePath; // Path where Java files are stored


}

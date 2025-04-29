package co.uniquindio.ingesis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ejemplo")
public class Example {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplo")
    private Integer id;

    @Column(name = "titulo_ejemplo", nullable = false)
    private String title;
    @Lob
    @Column(name = "contenido_ejemplo", nullable = false)
    private String content;

    @Column(name = "categoria_ejemplo", nullable = false)
    private String category;

    @Column(name = "dificultad_ejemplo", nullable = false)
    private Integer difficulty;

    @Column(name = "cedula_profesor", nullable = false)
    private String cedulaProfesor;
}

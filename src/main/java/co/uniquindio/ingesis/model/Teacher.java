package co.uniquindio.ingesis.model;

import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a teacher in the system.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profesor")
public class Teacher {

    /**
     * Unique ID for the teacher.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Integer id;

    /**
     * Unique identification number for the teacher (cedula).
     */
    @Column(name = "documento_profesor", nullable = false, unique = true)
    private String cedula;

    /**
     * Teacher's full name.
     */
    @Column(name = "nombre_profesor", nullable = false)
    private String name;

    /**
     * Teacher's email address.
     */
    @Column(name = "email_profesor", unique = true)
    private String email;

    /**
     * Teacher's password (hashed or encrypted in practice).
     */
    @Column(name = "password_profesor", nullable = false)
    private String password;

    /**
     * Account status: ACTIVE, PENDING.
     */
    @Column(name = "status")
    private StatusAcountEnum status;

    /**
     * Token used for email/account verification.
     */
    @Column(name = "token_verification")
    private String token;

}

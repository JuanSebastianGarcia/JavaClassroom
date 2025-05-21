package co.uniquindio.ingesis.model;

import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a student in the system.
 * 
 * Stores personal and account-related information including document ID,
 * name, email, password, account status, and a token used for verification.
 */
@Entity
@Table(name = "student")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    /**
     * Unique identifier for the student.
     * This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Student's document (e.g., national ID or university ID).
     * Must be unique and cannot be null.
     */
    @Column(name = "document", unique = true, nullable = false)
    private String document;

    /**
     * Full name of the student.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Email address of the student.
     * Must be unique if present.
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * Encrypted password of the student.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Account status of the student, e.g., ACTIVE, INACTIVE, PENDING.
     */
    @Column(name = "status")
    private StatusAcountEnum status;

    /**
     * Token used for email verification or password reset processes.
     */
    @Column(name = "token_verification")
    private String token;
}

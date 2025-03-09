package co.uniquindio.ingesis.model;

import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "student")
@Data @AllArgsConstructor @NoArgsConstructor
public class Student {

    /*
     * unique student's id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*
     * student's document
     */
    @Column(name = "document", unique = true, nullable = false)
    private String document;

    /*
     * student's name
     */
    @Column(name = "name", nullable = false)
    private String name;


    /*
     * student's email
     */
    @Column(name = "email", unique = true)
    private String email;


    /*
     * student's password
     */
    @Column(name = "password", nullable = false)
    private String password;
    
    /*
     * 
     */
    @Column(name="status")
    private StatusAcountEnum status;


    /*
     * 
     */
    @Column(name="token_verification")
    private String token;
}

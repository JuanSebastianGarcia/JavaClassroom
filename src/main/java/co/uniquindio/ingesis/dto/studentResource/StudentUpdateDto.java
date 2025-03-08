package co.uniquindio.ingesis.dto.studentResource;

import jakarta.validation.constraints.NotBlank;

public record StudentUpdateDto(


    /*
     * student's name
     */
    String nombre,

    /*
     * student's email
     */
    String email,

    /*
     * students's current password
     */
    @NotBlank
    String password,

    /*
     * students's new password
     */
    String new_password

) {

}

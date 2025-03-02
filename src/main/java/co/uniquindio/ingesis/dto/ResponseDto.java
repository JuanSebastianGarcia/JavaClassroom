package co.uniquindio.ingesis.dto;

public record ResponseDto<T>(boolean error, T response) {}

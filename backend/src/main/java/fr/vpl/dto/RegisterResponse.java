package fr.vpl.dto;

public record RegisterResponse(
    Long id,
    String username,
    String email
) {}
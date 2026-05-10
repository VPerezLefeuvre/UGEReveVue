package fr.vpl.dto;

import java.util.List;
import java.util.Map;

public record ErrorResponse(
    String timestamp,
    int status,
    String code,
    Map<String, List<String>> details
) {}
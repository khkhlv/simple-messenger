package ru.khkhlv.messenger.dto;

import java.time.LocalDateTime;

public record MessageDto(Long id, String sender, String content, LocalDateTime timestamp) {}

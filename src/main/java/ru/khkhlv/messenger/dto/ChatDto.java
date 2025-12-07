package ru.khkhlv.messenger.dto;

import java.util.List;

public record ChatDto(Long id, List<String> participants) {}


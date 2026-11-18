package app.calendar.domain;

import java.util.UUID;

public record EventType(UUID id, String title, String description, int durationMinutes) {}

package app.calendar.domain;

import java.time.Instant;
import java.util.UUID;

public record Booking(
    UUID id,
    UUID eventTypeId,
    String eventTypeTitle,
    Instant start,
    Instant end,
    String guestName,
    String guestEmail) {}

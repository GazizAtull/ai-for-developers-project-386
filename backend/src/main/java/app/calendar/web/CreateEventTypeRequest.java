package app.calendar.web;

public record CreateEventTypeRequest(String title, String description, Integer durationMinutes) {}

package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void parse() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse("2022-01-07T15:30:00");
        LocalDateTime expected = LocalDateTime.of(2022, 1, 7, 15, 30, 0);
        assertThat(expected).isEqualTo(result);
    }
}
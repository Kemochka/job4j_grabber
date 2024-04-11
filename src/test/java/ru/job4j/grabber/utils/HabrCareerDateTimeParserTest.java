package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void parseString() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse("2022-01-07T15:30:00+03:00");
        LocalDateTime expected = LocalDateTime.parse("2022-01-07T15:30:00");
        assertThat(expected).isEqualTo(result);
    }
}
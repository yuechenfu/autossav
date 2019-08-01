package com.hiveel.autossav.controller.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        LocalDateTime result = null;
        try {
            result = LocalDateTime.parse(text);
        } catch (Exception e1) {
            try {
                result = LocalDate.parse(text).atStartOfDay();
            } catch (Exception e2) {
            }
        }
        return result;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.toString();
    }
}

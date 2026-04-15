package com.example.shoppingcart;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

final class TestLogHandler extends Handler {

    private final List<LogRecord> records = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        records.add(record);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    boolean contains(Level level, String messageFragment) {
        return records.stream()
                .anyMatch(record -> record.getLevel().equals(level)
                        && record.getMessage().contains(messageFragment));
    }
}

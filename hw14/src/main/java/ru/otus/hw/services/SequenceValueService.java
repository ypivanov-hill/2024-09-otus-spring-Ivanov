package ru.otus.hw.services;

import java.util.List;

public interface SequenceValueService {
    List<Long> getSequenceValues(long count, String sequenceName);
}

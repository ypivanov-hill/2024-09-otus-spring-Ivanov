package ru.otus.hw.services;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface SequenceValueService {

    ConcurrentLinkedQueue<Long> getSequenceValuesQueue(long count, String sequenceName);
}

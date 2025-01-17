package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
@Service
public class SequenceValueServiceImpl implements SequenceValueService {

    private final NamedParameterJdbcOperations jdbc;


    @Override
    public ConcurrentLinkedQueue<Long> getSequenceValuesQueue(long count, String sequenceName) {
        return new ConcurrentLinkedQueue<>(
                jdbc.queryForList("select nextval('" + sequenceName + "') from SYSTEM_RANGE(1, :cnt)",
                Map.of("cnt", count),
                Long.class));
    }
}

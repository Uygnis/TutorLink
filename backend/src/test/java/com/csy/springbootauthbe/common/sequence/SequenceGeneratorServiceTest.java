package com.csy.springbootauthbe.common.sequence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SequenceGeneratorServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    private SequenceGeneratorService sequenceGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ 1. Test getNextSequence returns incremented seq
    @Test
    void getNextSequence_whenCounterExists_returnsIncrementedValue() {
        Counter mockCounter = new Counter();
        mockCounter.setSeq(5L);
        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(Counter.class)
        )).thenReturn(mockCounter);

        long result = sequenceGeneratorService.getNextSequence("studentId");

        assertEquals(5L, result);
        verify(mongoOperations).findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(Counter.class)
        );
    }

    // ✅ 2. Test getNextSequence when Counter is null
    @Test
    void getNextSequence_whenCounterIsNull_returns1() {
        when(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(Counter.class)
        )).thenReturn(null);

        long result = sequenceGeneratorService.getNextSequence("studentId");

        assertEquals(1L, result);
    }

    // ✅ 3. Test getNextStudentId formats properly
    @Test
    void getNextStudentId_returnsFormattedId() {
        SequenceGeneratorService spyService = spy(sequenceGeneratorService);
        doReturn(7L).when(spyService).getNextSequence("studentId");

        String result = spyService.getNextStudentId();

        assertEquals("S07", result);
    }

    // ✅ 4. Test getNextEventId delegates properly
    @Test
    void getNextEventId_returnsSequenceValue() {
        SequenceGeneratorService spyService = spy(sequenceGeneratorService);
        doReturn(99L).when(spyService).getNextSequence("eventId");

        Long result = spyService.getNextEventId();

        assertEquals(99L, result);
    }

    // ✅ 5. Test peekSequence when counter exists
    @Test
    void peekSequence_whenCounterExists_returnsIncrementedValue() {
        Counter mockCounter = new Counter();
        mockCounter.setSeq(10L);
        when(mongoOperations.findOne(any(Query.class), eq(Counter.class))).thenReturn(mockCounter);

        long result = sequenceGeneratorService.peekSequence("studentId");

        assertEquals(11L, result);
    }

    // ✅ 6. Test peekSequence when counter is null
    @Test
    void peekSequence_whenCounterIsNull_returns1() {
        when(mongoOperations.findOne(any(Query.class), eq(Counter.class))).thenReturn(null);

        long result = sequenceGeneratorService.peekSequence("studentId");

        assertEquals(1L, result);
    }

    // ✅ 7. Test peekNextStudentId formats properly
    @Test
    void peekNextStudentId_returnsFormattedValue() {
        SequenceGeneratorService spyService = spy(sequenceGeneratorService);
        doReturn(15L).when(spyService).peekSequence("studentId");

        String result = spyService.peekNextStudentId();

        assertEquals("S15", result);
    }
}

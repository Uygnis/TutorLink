package com.csy.springbootauthbe.common.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    public long getNextSequence(String seqName) {
        Counter counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true), // ← upsert creates it if missing
                Counter.class
        );

        // if somehow counter is still null, return 1
        return counter != null ? counter.getSeq() : 1;
    }

    public String getNextStudentId() {
        long seq = getNextSequence("studentId");
        return String.format("S%02d", seq);
    }

    public Long getNextEventId() {
        return getNextSequence("eventId");
    }

    public long peekSequence(String seqName) {
        Counter counter = mongoOperations.findOne(
                Query.query(Criteria.where("_id").is(seqName)),
                Counter.class
        );
        return counter != null ? counter.getSeq() + 1 : 1;
    }

    public String peekNextStudentId() {
        long seq = peekSequence("studentId");
        return String.format("S%02d", seq);
    }

}


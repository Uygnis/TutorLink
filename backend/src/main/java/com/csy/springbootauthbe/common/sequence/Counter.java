package com.csy.springbootauthbe.common.sequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "counters")
public class Counter {

    @Id
    private String id;   // sequence name (e.g., "studentId")
    private long seq;    // last sequence number used
}



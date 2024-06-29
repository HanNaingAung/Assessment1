package org.example.config.sequence;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promo_sequence")
public class PromoSequence {

    private String id;
    private long seq;

    public PromoSequence() {}

    public PromoSequence(String id, long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}

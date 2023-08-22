package dev.dus.dusbot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {

    Long id;

    String tag;

    public Tag(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }
}

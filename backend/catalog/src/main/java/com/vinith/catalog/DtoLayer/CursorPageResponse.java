package com.vinith.catalog.DtoLayer;

import java.io.Serializable;
import java.util.List;

public class CursorPageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private Long nextCursor;
    private boolean hasNext;

    public CursorPageResponse(List<T> content, Long nextCursor, boolean hasNext) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    public List<T> getContent() {
        return content;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}

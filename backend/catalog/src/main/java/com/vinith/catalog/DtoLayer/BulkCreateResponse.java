package com.vinith.catalog.DtoLayer;

import java.util.List;

public class BulkCreateResponse {
    private List<ProductResponse> inserted;
    private List<String> skipped; // SKUs already present

    public BulkCreateResponse() {}

    public BulkCreateResponse(List<ProductResponse> inserted, List<String> skipped) {
        this.inserted = inserted;
        this.skipped = skipped;
    }

    public List<ProductResponse> getInserted() { return inserted; }
    public void setInserted(List<ProductResponse> inserted) { this.inserted = inserted; }

    public List<String> getSkipped() { return skipped; }
    public void setSkipped(List<String> skipped) { this.skipped = skipped; }
}
package com.czf.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Bangumi API v0 Response Records
 */
public class BangumiDTOs {

    public record SubjectSearchResponse(
            int total,
            int limit,
            int offset,
            List<SubjectItem> data
    ) {}

    public record SubjectItem(
            int id,
            String name,
            @JsonProperty("name_cn") String nameCn,
            String summary,
            Images images,
            int eps,
            String date
    ) {
        public String getDisplayName() {
            return (nameCn == null || nameCn.isBlank()) ? name : nameCn;
        }
    }

    public record Images(
            String large,
            String common,
            String medium,
            String small,
            String grid
    ) {}
}

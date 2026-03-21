package com.czf.blog.dto;

public record AnimeImportResult(String action, String message) {
    public static AnimeImportResult created() {
        return new AnimeImportResult("CREATED", "导入成功");
    }

    public static AnimeImportResult updated() {
        return new AnimeImportResult("UPDATED", "已存在，已刷新番剧信息");
    }
}

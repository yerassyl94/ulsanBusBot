package com.google.zxing.client.android.book;

final class SearchBookContentsResult {
    private static String query;
    private final String pageId;
    private final String pageNumber;
    private final String snippet;
    private final boolean validSnippet;

    SearchBookContentsResult(String pageId, String pageNumber, String snippet, boolean validSnippet) {
        this.pageId = pageId;
        this.pageNumber = pageNumber;
        this.snippet = snippet;
        this.validSnippet = validSnippet;
    }

    public static void setQuery(String query) {
        query = query;
    }

    public String getPageId() {
        return this.pageId;
    }

    public String getPageNumber() {
        return this.pageNumber;
    }

    public String getSnippet() {
        return this.snippet;
    }

    public boolean getValidSnippet() {
        return this.validSnippet;
    }

    public static String getQuery() {
        return query;
    }
}

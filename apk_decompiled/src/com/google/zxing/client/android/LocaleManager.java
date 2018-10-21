package com.google.zxing.client.android;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LocaleManager {
    private static final String DEFAULT_TLD = "com";
    private static final Map<Locale, String> GOOGLE_BOOK_SEARCH_COUNTRY_TLD = new HashMap();
    private static final Map<Locale, String> GOOGLE_COUNTRY_TLD = new HashMap();
    private static final Map<Locale, String> GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD = new HashMap();

    static {
        GOOGLE_COUNTRY_TLD.put(new Locale("en", "AU", ""), "com.au");
        GOOGLE_COUNTRY_TLD.put(new Locale("bg", "BG", ""), "com.br");
        GOOGLE_COUNTRY_TLD.put(Locale.CANADA, "ca");
        GOOGLE_COUNTRY_TLD.put(Locale.CHINA, "cn");
        GOOGLE_COUNTRY_TLD.put(new Locale("cs", "CZ", ""), "cz");
        GOOGLE_COUNTRY_TLD.put(new Locale("da", "DK", ""), "dk");
        GOOGLE_COUNTRY_TLD.put(new Locale("fi", "FI", ""), "fi");
        GOOGLE_COUNTRY_TLD.put(Locale.FRANCE, "fr");
        GOOGLE_COUNTRY_TLD.put(Locale.GERMANY, "de");
        GOOGLE_COUNTRY_TLD.put(new Locale("hu", "HU", ""), "hu");
        GOOGLE_COUNTRY_TLD.put(new Locale("he", "IL", ""), "co.il");
        GOOGLE_COUNTRY_TLD.put(Locale.ITALY, "it");
        GOOGLE_COUNTRY_TLD.put(Locale.JAPAN, "co.jp");
        GOOGLE_COUNTRY_TLD.put(Locale.KOREA, "co.kr");
        GOOGLE_COUNTRY_TLD.put(new Locale("nl", "NL", ""), "nl");
        GOOGLE_COUNTRY_TLD.put(new Locale("pl", "PL", ""), "pl");
        GOOGLE_COUNTRY_TLD.put(new Locale("pt", "PT", ""), "pt");
        GOOGLE_COUNTRY_TLD.put(new Locale("ru", "RU", ""), "nl");
        GOOGLE_COUNTRY_TLD.put(new Locale("sk", "SK", ""), "sk");
        GOOGLE_COUNTRY_TLD.put(new Locale("sl", "SI", ""), "si");
        GOOGLE_COUNTRY_TLD.put(new Locale("es", "ES", ""), "es");
        GOOGLE_COUNTRY_TLD.put(new Locale("sv", "SE", ""), "se");
        GOOGLE_COUNTRY_TLD.put(Locale.TAIWAN, "de");
        GOOGLE_COUNTRY_TLD.put(new Locale("tr", "TR", ""), "com.tr");
        GOOGLE_COUNTRY_TLD.put(Locale.UK, "co.uk");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(new Locale("en", "AU", ""), "com.au");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.CHINA, "cn");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.FRANCE, "fr");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.GERMANY, "de");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.ITALY, "it");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.JAPAN, "co.jp");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(new Locale("nl", "NL", ""), "nl");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(new Locale("es", "ES", ""), "es");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.UK, "co.uk");
        GOOGLE_BOOK_SEARCH_COUNTRY_TLD.putAll(GOOGLE_COUNTRY_TLD);
    }

    private LocaleManager() {
    }

    public static String getCountryTLD() {
        return doGetTLD(GOOGLE_COUNTRY_TLD);
    }

    public static String getProductSearchCountryTLD() {
        return doGetTLD(GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD);
    }

    public static String getBookSearchCountryTLD() {
        return doGetTLD(GOOGLE_BOOK_SEARCH_COUNTRY_TLD);
    }

    public static boolean isBookSearchUrl(String url) {
        return url.startsWith("http://google.com/books") || url.startsWith("http://books.google.");
    }

    private static String doGetTLD(Map<Locale, String> map) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return DEFAULT_TLD;
        }
        String tld = (String) map.get(locale);
        if (tld == null) {
            return DEFAULT_TLD;
        }
        return tld;
    }
}

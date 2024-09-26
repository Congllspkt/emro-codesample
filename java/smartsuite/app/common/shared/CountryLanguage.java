package smartsuite.app.common.shared;

public enum CountryLanguage {
    
    KR("KR", "ko_KR"),   // 대한민국 - 한국어
    US("US", "en_US"),   // 미국 - 영어 (기본값)
    DE("DE", "en_US"),   // 독일 - 영어 (기본값)
    CN("CN", "en_US"),   // 중국 - 영어 (기본값)
    JP("JP", "en_US"),   // 일본 - 영어 (기본값)
    IN("IN", "en_US");   // 인도 - 영어 (기본값)

    private final String countryCode;
    private final String locale;

    CountryLanguage(String countryCode, String locale) {
        this.countryCode = countryCode;
        this.locale = locale;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLocale() {
        return locale;
    }

    // 주어진 국가 코드로 locale 반환
    public static String getLocaleByCountryCode(String countryCode) {
        for (CountryLanguage cl : values()) {
            if (cl.getCountryCode().equalsIgnoreCase(countryCode)) {
                return cl.getLocale();
            }
        }
        return "en_US";  // 기본값
    }
}

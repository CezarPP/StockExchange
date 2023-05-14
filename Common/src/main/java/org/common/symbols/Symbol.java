package org.common.symbols;

public enum Symbol {
    MMM("MMM", "3M Company"),
    AXP("AXP", "American Express"),
    AMGN("AMGN", "Amgen"),
    AAPL("AAPL", "Apple Inc."),
    BA("BA", "Boeing"),
    CAT("CAT", "Caterpillar"),
    CVX("CVX", "Chevron Corporation"),
    CSCO("CSCO", "Cisco Systems"),
    KO("KO", "Coca-Cola"),
    DOW("DOW", "Dow Inc."),
    GS("GS", "Goldman Sachs"),
    HD("HD", "Home Depot"),
    HON("HON", "Honeywell"),
    IBM("IBM", "IBM"),
    INTC("INTC", "Intel Corporation"),
    JNJ("JNJ", "Johnson & Johnson"),
    JPM("JPM", "JPMorgan Chase & Co."),
    MCD("MCD", "McDonald's"),
    MRK("MRK", "Merck & Co."),
    MSFT("MSFT", "Microsoft"),
    NKE("NKE", "Nike"),
    PG("PG", "Procter & Gamble"),
    CRM("CRM", "Salesforce"),
    TRV("TRV", "The Travelers Companies"),
    UNH("UNH", "UnitedHealth Group"),
    VZ("VZ", "Verizon"),
    V("V", "Visa"),
    WBA("WBA", "Walgreens Boots Alliance"),
    WMT("WMT", "Walmart"),
    DIS("DIS", "Walt Disney");

    private final String symbol;
    private final String companyName;

    Symbol(String symbol, String companyName) {
        this.symbol = symbol;
        this.companyName = companyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public static Symbol fromValue(String value) {
        for (Symbol symbol : Symbol.values()) {
            if (symbol.getSymbol().equals(value)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException("Unknown Symbol value: " + value);
    }
}


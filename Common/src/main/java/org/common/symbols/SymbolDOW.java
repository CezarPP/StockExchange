package org.common.symbols;

public enum SymbolDOW implements Symbol {
    MMM("3M Company"),
    AXP("American Express"),
    AMGN("Amgen"),
    AAPL("Apple Inc."),
    BA("Boeing"),
    CAT("Caterpillar"),
    CVX("Chevron Corporation"),
    CSCO("Cisco Systems"),
    KO("Coca-Cola"),
    DOW("Dow Inc."),
    GS("Goldman Sachs"),
    HD("Home Depot"),
    HON("Honeywell"),
    IBM("IBM"),
    INTC("Intel Corporation"),
    JNJ("Johnson & Johnson"),
    JPM("JPMorgan Chase & Co."),
    MCD("McDonald's"),
    MRK("Merck & Co."),
    MSFT("Microsoft"),
    NKE("Nike"),
    PG("Procter & Gamble"),
    CRM("Salesforce"),
    TRV("The Travelers Companies"),
    UNH("UnitedHealth Group"),
    VZ("Verizon"),
    V("Visa"),
    WBA("Walgreens Boots Alliance"),
    WMT("Walmart"),
    DIS("Walt Disney");

    private final String companyName;

    SymbolDOW(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
}

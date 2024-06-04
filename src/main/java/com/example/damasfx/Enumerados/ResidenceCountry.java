package com.example.damasfx.Enumerados;

public enum ResidenceCountry {
    EMPTY(""),
    ALEMANIA("ALEMANIA"),
    AUSTRIA("AUSTRIA"),
    BÉLGICA("BÉLGICA"),
    BULGARIA("BULGARIA"),
    CHIPRE("CHIPRE"),
    CROACIA("CROACIA"),
    DINAMARCA("DINAMARCA"),
    ESLOVAQUIA("ESLOVAQUIA"),
    ESLOVENIA("ESLOVENIA"),
    ESPAÑA("ESPAÑA"),
    ESTONIA("ESTONIA"),
    FINLANDIA("FINLANDIA"),
    FRANCIA("FRANCIA"),
    GRECIA("GRECIA"),
    HUNGRÍA("HUNGRÍA"),
    IRLANDA("IRLANDA"),
    ITALIA("ITALIA"),
    LETONIA("LETONIA"),
    LITUANIA("LITUANIA"),
    LUXEMBURGO("LUXEMBURGO"),
    MALTA("MALTA"),
    POLONIA("POLONIA"),
    PORTUGAL("PORTUGAL"),
    RUMANÍA("RUMANÍA"),
    SUECIA("SUECIA");

    private final String displayName;

    ResidenceCountry(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}


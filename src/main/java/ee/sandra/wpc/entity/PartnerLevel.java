package ee.sandra.wpc.entity;

// Partneri tase ja sellele vastav allahindlus protsentides.
// Tasemed ja protsendid on Sandra plaan (2026-10-08), veel mitte lõplikud.
public enum PartnerLevel {
    PARTNER(10),
    PRO(15);

    private final int discountPercent;

    PartnerLevel(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }
}

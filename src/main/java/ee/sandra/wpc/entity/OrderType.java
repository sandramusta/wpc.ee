package ee.sandra.wpc.entity;

public enum OrderType {
    INQUIRY,   // klient saadab päringu, makset ei toimu
    PURCHASE   // päris ost koos maksega (e-poe valmidus)
}
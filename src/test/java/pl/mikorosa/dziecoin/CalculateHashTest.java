package pl.mikorosa.dziecoin;

import static org.junit.jupiter.api.Assertions.*;

class CalculateHashTest {

    @org.junit.jupiter.api.Test
    void sha256sum() {
        String resultingHash = CalculateHash.sha256sum("testing");
        assertEquals("cf80cd8aed482d5d1527d7dc72fceff84e6326592848447d2dc0b0e87dfc9a90", resultingHash);
    }

    @org.junit.jupiter.api.Test
    void md5sum() {
        String resultingHash = CalculateHash.md5sum("testing");
        assertEquals("ae2b1fca515949e5d54fb22b8ed95575", resultingHash);
    }
}
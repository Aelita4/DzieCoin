package pl.mikorosa.dziecoin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NFTTest {
    @Test
    void NFTContractGen() {
        NFT nft = new NFT("stevejobs", "test");

        String hash = CalculateHash.md5sum("stevejobstest");

        assertEquals(hash, nft.getContract());
    }
}
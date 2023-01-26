package pl.mikorosa.dziecoin;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {
    @Test
    void mineBlock() {
        Main.difficulty = 4;

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "2", 1));

        Block block = new Block("0", 1, transactions);
        block.mineBlock();

        assertEquals(168149, block.getNonce());
        assertEquals("0000e4942b486fa4779fadc32a1d0dd483e117aa57bc84f6ff5912f9abefef0e", block.getHash());
    }

    @Test
    void alterBlock() {
        Main.difficulty = 2;

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "2", 1));

        Block block = new Block("0", 1, transactions);
        block.mineBlock();

        block.getTransactions().add(new Transaction("2", "1", 1));

        Block.VerifyCodes status = block.verifyBlock();

        assertEquals(Block.VerifyCodes.INVALID_BLOCK_HASH, status);
    }

    @Test
    void invalidProofOfWork() {
        Main.difficulty = 2;

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "2", 1));

        Block block = new Block("0", 1, transactions);
        block.mineBlock();

        Main.difficulty = 4;

        Block.VerifyCodes status = block.verifyBlock();

        assertEquals(Block.VerifyCodes.INVALID_POW_SIGNATURE, status);
    }
}
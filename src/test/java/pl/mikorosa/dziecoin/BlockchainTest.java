package pl.mikorosa.dziecoin;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainTest {
    @Test
    void blockchainIntegrity() {
        Main.difficulty = 2;
        Blockchain b = new Blockchain("1", false);

        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(new Transaction("1", "2", 1));

        Block block1 = new Block(b.getLatestBlock().getHash(), 2, transactions1);
        block1.mineBlock();

        try {
            b.addBlock(block1);
        } catch(BlockchainIntegrityException e) {
            e.printStackTrace();
            fail();
        }

        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(new Transaction("2", "3", 1));

        Block block2 = new Block(block1.getHash(), 3, transactions2);
        block2.mineBlock();

        try {
            b.addBlock(block2);
        } catch (BlockchainIntegrityException e) {
            fail();
        }
    }

    @Test
    void blockchainVerify() {
        Main.difficulty = 2;
        Blockchain b = new Blockchain("1", false);

        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(new Transaction("1", "2", 1));

        Block block1 = new Block(b.getLatestBlock().getHash(), 2, transactions1);
        block1.mineBlock();

        try {
            b.addBlock(block1);
        } catch(BlockchainIntegrityException e) {
            e.printStackTrace();
            fail();
        }

        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(new Transaction("2", "3", 1));

        Block block2 = new Block(block1.getHash(), 3, transactions2);
        block2.mineBlock();

        try {
            b.addBlock(block2);
        } catch (BlockchainIntegrityException e) {
            fail();
        }

        assertTrue(b.validateChain() == Block.VerifyCodes.SUCCESS);
    }

    @Test
    void blockchainIntegrityExceptionThrown() {
        Main.difficulty = 2;
        Blockchain b = new Blockchain("1", false);

        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(new Transaction("1", "2", 1));

        Block block1 = new Block(b.getLatestBlock().getHash(), 2, transactions1);
        block1.mineBlock();

        try {
            b.addBlock(block1);
        } catch(BlockchainIntegrityException e) {
            e.printStackTrace();
            fail();
        }

        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(new Transaction("2", "3", 1));

        Block block2 = new Block("asdf", 3, transactions2);
        block2.mineBlock();

        try {
            b.addBlock(block2);
        } catch (BlockchainIntegrityException e) {
            return; // pass
        }
    }
}
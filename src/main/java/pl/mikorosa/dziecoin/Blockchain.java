package pl.mikorosa.dziecoin;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private List<Block> blocks;
    private int length;

    public Blockchain(String genesisBlockRecipient) {
        blocks = new ArrayList<>();
        length = blocks.size();

        Transaction firstTransaction = new Transaction("0", genesisBlockRecipient, 10);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(firstTransaction);

        Block genesisBlock = new Block("0", 1, transactions);

        genesisBlock.mineBlock();
        if(genesisBlock.verifyBlock() != Block.VerifyCodes.SUCCESS) throw new IllegalStateException("Genesis block is invalid");

        try {
            addBlock(genesisBlock);
        } catch (BlockchainIntegrityException e) {
            System.out.println("Blockchain Integrity Exception: " + e.getMessage());
        }
    }

    public int getLength() {
        return length;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Block getLatestBlock() {
        return blocks.get(length - 1);
    }

    public Block.VerifyCodes validateChain() {
        for (int i = 1; i < (length - 1); i++) {
            if(blocks.get(i).verifyBlock() != Block.VerifyCodes.SUCCESS) return blocks.get(i).verifyBlock();
            if(!blocks.get(i).getHash().equals(blocks.get(i + 1).getPrevHash())) return Block.VerifyCodes.HASH_MISMATCH;
        }

        for (Block block : blocks) {
            if(!block.getHash().startsWith("0".repeat(Main.difficulty))) return Block.VerifyCodes.INVALID_POW_SIGNATURE;
        }

        return Block.VerifyCodes.SUCCESS;
    }

    public void addBlock(Block block) throws BlockchainIntegrityException {
        Block.VerifyCodes status = block.verifyBlock();

        if(status == Block.VerifyCodes.HASH_MISMATCH) throw new BlockchainIntegrityException("Block " + block.getHeight() + " calculated hash and actual hash does not match");
        if(status == Block.VerifyCodes.INVALID_POW_SIGNATURE) throw new BlockchainIntegrityException("Block " + block.getHeight() + " contains invalid proof of work signature");
        if(status == Block.VerifyCodes.INVALID_BLOCK_HASH) throw new BlockchainIntegrityException("Block " + block.getHeight() + " contains invalid hash");

        if(length != 0) { // don't check for very first block
            if (!getLatestBlock().getHash().equals(block.getPrevHash())) throw new BlockchainIntegrityException("Block " + block.getHeight() + " previous hash does not match");

            for (Transaction transaction : block.getTransactions()) {
                if(this.getAddressBalance(transaction.getSender()) < transaction.getAmount())
                    throw new BlockchainIntegrityException("Wallet " + transaction.getSender() + " at block " + block.getHeight() + " tried to spend too much\nBalance: " + this.getAddressBalance(transaction.getSender()) + "\nTried to spend: " + transaction.getAmount());
            }
        }
        blocks.add(block);
        length++;
        if(length != 1 && Main.mainFrame != null) Main.mainFrame.onNewBlockUpdate();
    }

    public int getAddressBalance(String walletAddress) {
        int amount = 0;

        for (Block block : blocks) {
            for (Transaction transaction : block.getTransactions()) {
                if(transaction.getSender().equals(walletAddress)) amount -= transaction.getAmount();
                if(transaction.getRecipient().equals(walletAddress)) amount += transaction.getAmount();
            }
        }

        return amount;
    }
}

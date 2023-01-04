package pl.mikorosa.dziecoin;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String hash;
    private String prevHash;
    private int height;
    private List<Transaction> transactions;
    private List<NFT> nfts;
    private int nonce;

    public Block() {
        this.transactions = new ArrayList<>();
        this.nfts = new ArrayList<>();
    }

    public Block(String prevHash, int height, List<Transaction> transactions) {
        this.prevHash = prevHash;
        this.height = height;
        this.transactions = transactions;
        this.nfts = new ArrayList<>();
    }

    public Block(String hash, String prevHash, int height, List<Transaction> transactions, List<NFT> nfts, int nonce) {
        this.hash = hash;
        this.prevHash = prevHash;
        this.height = height;
        this.transactions = transactions;
        this.nfts = nfts;
        this.nonce = nonce;
    }

    public void mineBlock() {
        String blockStruct = this.prevHash + this.height;
        for (Transaction transaction : this.transactions) {
            blockStruct += transaction.toString();
        }

        for (NFT nft : nfts) {
            blockStruct += nft.toString();
        }

        int nonce = 0;

        String hash;

        System.out.println();
        while(true) {
            hash = CalculateHash.sha256sum(blockStruct + nonce);
            if(hash.startsWith("0".repeat(Main.difficulty))) break;
            nonce++;
            System.out.print("\rMining block " + height + "... " + nonce);
        }

        System.out.println("\nSuccessfully mined block " + height + " (nonce: " + nonce + ")");

        this.hash = hash;
        this.nonce = nonce;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void addNFT(NFT nft) {
        nfts.add(nft);
    }

    public enum VerifyCodes {
        SUCCESS,
        INVALID_POW_SIGNATURE,
        HASH_MISMATCH,
        INVALID_BLOCK_HASH
    }

    public VerifyCodes verifyBlock() {
        String blockStruct = this.prevHash + this.height;
        for (Transaction transaction : this.transactions) {
            blockStruct += transaction.toString();
        }
        for (NFT nft : nfts) {
            blockStruct += nft.toString();
        }
        blockStruct += this.nonce;

        String calculatedHash = CalculateHash.sha256sum(blockStruct);

        if(!calculatedHash.equals(this.hash)) return VerifyCodes.INVALID_BLOCK_HASH;
        if(!calculatedHash.startsWith("0".repeat(Main.difficulty))) return VerifyCodes.INVALID_POW_SIGNATURE;
        return VerifyCodes.SUCCESS;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public int getHeight() {
        return height;
    }

    public List<NFT> getNFTs() {
        return nfts;
    }

    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public int getNonce() {
        return nonce;
    }
}

package pl.mikorosa.dziecoin;

import pl.mikorosa.dziecoin.database.DatabaseConnection;
import pl.mikorosa.dziecoin.gui.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int difficulty;
    public static Blockchain blockchain;
    public static MainFrame mainFrame;
    public static DatabaseConnection db;

    public static void main(String[] args) {
        db = new DatabaseConnection();

        System.out.println(db.getBlocksTable().select());
        System.out.println(db.getTransactionsTable().select());
        System.out.println(db.getNFTsTable().select());

        Wallet w1 = new Wallet();
        Wallet w2 = new Wallet();
        Wallet w3 = new Wallet();

        difficulty = 4;
        blockchain = new Blockchain(w1.getAddress());

        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        NFT n1 = new NFT(w1.getAddress(), "licencja na wozek widlowy");

        List<Transaction> t1 = new ArrayList<>();
        t1.add(new Transaction(w1.getAddress(), w2.getAddress(), 5));

        Block b1 = new Block(blockchain.getLatestBlock().getHash(), blockchain.getLength() + 1, t1);

        b1.addNFT(n1);

        b1.mineBlock();

        try {
            blockchain.addBlock(b1);
        } catch (BlockchainIntegrityException e) {
            System.out.println("Blockchain Integrity Exception: " + e.getMessage());
        }

        List<Transaction> t2 = new ArrayList<>();
        t2.add(new Transaction(w1.getAddress(), w3.getAddress(), 2));
        t2.add(new Transaction(w2.getAddress(), w3.getAddress(), 3));

        Block b2 = new Block(blockchain.getLatestBlock().getHash(), blockchain.getLength() + 1, t2);
        b2.mineBlock();

        try {
            blockchain.addBlock(b2);
        } catch (BlockchainIntegrityException e) {
            System.out.println("Blockchain Integrity Exception: " + e.getMessage());
        }

        for (Block block : blockchain.getBlocks()) {
            System.out.println("Block " + block.getHeight() + ": " + block.getHash());
            for (Transaction transaction : block.getTransactions()) {
                System.out.println("    " + transaction.getSender() + " => " + transaction.getRecipient() + " - " + transaction.getAmount());
            }
        }

        System.out.println("Balances:");
        System.out.println("Wallet 1: " + blockchain.getAddressBalance(w1.getAddress()));
        System.out.println("Wallet 2: " + blockchain.getAddressBalance(w2.getAddress()));
        System.out.println("Wallet 3: " + blockchain.getAddressBalance(w3.getAddress()));
    }
}
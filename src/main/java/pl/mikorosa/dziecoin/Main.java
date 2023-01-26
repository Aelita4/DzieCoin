package pl.mikorosa.dziecoin;

import pl.mikorosa.dziecoin.database.DatabaseConnection;
import pl.mikorosa.dziecoin.gui.ImportRuntime;
import pl.mikorosa.dziecoin.gui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int difficulty;
    public static Blockchain blockchain;
    public static DatabaseConnection db;
    public static MainFrame mainFrame;

    public static void main(String[] args) {
        db = new DatabaseConnection();

        ImportRuntime importRuntime = new ImportRuntime();
        importRuntime.setVisible(true);
    }

    public static void initImport(String blocksCSVPath, String transactionCSVPath, String NFTCSVPath, boolean autoImportToDatabase) {
        blockchain = new Blockchain(blocksCSVPath, transactionCSVPath, NFTCSVPath, autoImportToDatabase);
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void initFromDatabase(boolean importToDatabase) {
        blockchain = new Blockchain(importToDatabase);
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void initFromZero(boolean autoImportToDatabase) {
        Wallet w1 = new Wallet();
        Wallet w2 = new Wallet();
        Wallet w3 = new Wallet();

        blockchain = new Blockchain(w1.getAddress(), autoImportToDatabase);
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        NFT n1 = new NFT(w1.getAddress(), "licencja na wozek widlowy");
        NFT n2 = new NFT(w1.getAddress(), "legitymacja");

        List<Transaction> t1 = new ArrayList<>();
        t1.add(new Transaction(w1.getAddress(), w2.getAddress(), 5));

        Block b1 = new Block(blockchain.getLatestBlock().getHash(), blockchain.getLength() + 1, t1);

        b1.addNFT(n1);
        b1.addNFT(n2);

        b1.mineBlock();


        try {
            blockchain.addBlock(b1);
        } catch (BlockchainIntegrityException e) {
            JOptionPane.showMessageDialog(null, "Unable to add a block: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        List<Transaction> t2 = new ArrayList<>();
        t2.add(new Transaction(w1.getAddress(), w3.getAddress(), 2));
        t2.add(new Transaction(w2.getAddress(), w3.getAddress(), 3));

        Block b2 = new Block(blockchain.getLatestBlock().getHash(), blockchain.getLength() + 1, t2);
        b2.mineBlock();

        try {
            blockchain.addBlock(b2);
        } catch (BlockchainIntegrityException e) {
            JOptionPane.showMessageDialog(null, "Unable to add a block: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            blockchain.exportData(0b111);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
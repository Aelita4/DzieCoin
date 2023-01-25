package pl.mikorosa.dziecoin;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Blockchain {
    private List<Block> blocks;
    private int length;
    private boolean importToDatabase;

    public static void clearAllTablesInDatabase() {
        Main.db.getTransactionsTable().cleanTable();
        Main.db.getNFTsTable().cleanTable();
        Main.db.getBlocksTable().cleanTable();
    }

    public Blockchain(String genesisBlockRecipient, boolean importToDatabase) {
        System.out.println("Automatic import to database " + (importToDatabase ? "enabled" : "disabled"));
        if(importToDatabase) clearAllTablesInDatabase();

        this.importToDatabase = importToDatabase;
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

    public Blockchain(String blocksCSVPath, String transactionCSVPath, String NFTsCSVPath, boolean importToDatabase) {
        System.out.println("Automatic import to database " + (importToDatabase ? "enabled" : "disabled"));
        if(importToDatabase) clearAllTablesInDatabase();
        this.importToDatabase = importToDatabase;
        this.blocks = new ArrayList<>();
        this.length = this.blocks.size();

        try {
            List<Block> toMine = importData(blocksCSVPath, transactionCSVPath, NFTsCSVPath, importToDatabase);
            for (Block block : toMine) {
                this.addBlock(block);
            }
            System.out.println("Import complete");
        } catch (IOException e) {
            System.out.println("Error while importing data: ");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to import: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (BlockchainIntegrityException e) {
            JOptionPane.showMessageDialog(null, "Unable to add a block: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public Blockchain(boolean importToDatabase) {
        this.blocks = new ArrayList<>();
        this.length = this.blocks.size();
        this.importToDatabase = importToDatabase;

        this.importFromDatabase();
        System.out.println("Import complete");
    }

    public void importFromDatabase() {
        System.out.println("Importing from database...");
        List<Block> blocksToImport = new ArrayList<>();

        System.out.println("Importing block data...");
        List<Map<String, Object>> blocks = Main.db.getBlocksTable().select();
        for (Map<String, Object> block : blocks) {
            String hash = (String) block.get("hash");
            String prevHash = (String) block.get("prev_hash");
            int nonce = (Integer) block.get("nonce");
            int height = (Integer) block.get("height");

            blocksToImport.add(new Block(hash, prevHash, height, new ArrayList<>(), new ArrayList<>(), nonce));
        }

        System.out.println("Importing transaction data...");
        List<Map<String, Object>> transactions = Main.db.getTransactionsTable().select();
        for (Map<String, Object> transaction : transactions) {
            String sender = (String) transaction.get("sender");
            String recipient = (String) transaction.get("recipient");
            int amount = (Integer) transaction.get("amount");
            int blockHeight = (Integer) transaction.get("block_height");

            blocksToImport.get(blockHeight - 1).addTransaction(new Transaction(sender, recipient, amount));
        }

        System.out.println("Importing NFT data...");
        List<Map<String, Object>> NFTs = Main.db.getNFTsTable().select();
        for (Map<String, Object> NFT : NFTs) {
            String contract = (String) NFT.get("contract");
            String owner = (String) NFT.get("owner");
            String data = (String) NFT.get("data");
            int blockHeight = (Integer) NFT.get("block_height");

            blocksToImport.get(blockHeight - 1).addNFT(new NFT(contract, owner, data));
        }

        for (Block block : blocksToImport) {
            try {
                this.addBlock(block);
            } catch (BlockchainIntegrityException e) {
                JOptionPane.showMessageDialog(null, "Unable to add a block: " + e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    public List<Block> importData(String blocksPath, String transactionsPath, String NFTsPath, boolean importToDatabase) throws IOException {
        List<Block> blocks = new ArrayList<>();

        //if(importToDatabase) clearAllTablesInDatabase();

        Reader blocksReader = Files.newBufferedReader(Paths.get(blocksPath));
        Iterable<CSVRecord> blocksRecords = CSVFormat.DEFAULT.withHeader("height", "hash", "prev_hash", "nonce").withFirstRecordAsHeader().parse(blocksReader);
        System.out.println("Importing block data...");
        for(CSVRecord record : blocksRecords) {
            int height = Integer.parseInt(record.get("height"));
            String hash = record.get("hash");
            if(!hash.startsWith("0".repeat(Main.difficulty))) {
                JOptionPane.showMessageDialog(null, "Unable to import:\nImport data does not match difficulty (" + Main.difficulty + ")", "Fatal error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            String prevHash = record.get("prev_hash");
            int nonce = Integer.parseInt(record.get("nonce"));
            Block block = new Block(hash, prevHash, height, new ArrayList<>(), new ArrayList<>(), nonce);
            blocks.add(block);
            //if(importToDatabase) Main.db.getBlocksTable().insert(height, hash, prevHash, nonce);
        }

        Reader transactionsReader = Files.newBufferedReader(Paths.get(transactionsPath));
        Iterable<CSVRecord> transactionRecords = CSVFormat.DEFAULT.withHeader("id", "sender", "recipient", "amount", "block_height").withFirstRecordAsHeader().parse(transactionsReader);
        System.out.println("Importing transaction data...");
        for(CSVRecord record : transactionRecords) {
            String sender = record.get("sender");
            String recipient = record.get("recipient");
            int amount = Integer.parseInt(record.get("amount"));
            int blockHeight = Integer.parseInt(record.get("block_height"));

            blocks.get(blockHeight - 1).addTransaction(new Transaction(sender, recipient, amount));
            //if(importToDatabase) Main.db.getTransactionsTable().insert(sender, recipient, amount, blockHeight);
        }

        Reader NFTsReader = Files.newBufferedReader(Paths.get(NFTsPath));
        Iterable<CSVRecord> NFTsRecords = CSVFormat.DEFAULT.withHeader("id", "contract", "owner", "data", "block_height").withFirstRecordAsHeader().parse(NFTsReader);
        System.out.println("Importing NFT data...");
        for(CSVRecord record : NFTsRecords) {
            String contract = record.get("contract");
            String owner = record.get("owner");
            String data = record.get("data");
            int blockHeight = Integer.parseInt(record.get("block_height"));

            blocks.get(blockHeight - 1).addNFT(new NFT(contract, owner, data));
            //if(importToDatabase) Main.db.getNFTsTable().insert(contract, owner, data, blockHeight);
        }

        return blocks;
    }

    public enum ExportType {
        BLOCK(1),
        TRANSACTION(2),
        NFT(4);

        public final int value;

        ExportType(int value) {
            this.value = value;
        }
    }

    public void exportData(int bitfield) throws IOException {
        if((bitfield & ExportType.BLOCK.value) != 0) exportBlockData();
        if((bitfield & ExportType.TRANSACTION.value) != 0) exportTransactionData();
        if((bitfield & ExportType.NFT.value) != 0) exportNFTData();
    }

    private void exportBlockData() throws IOException {
        Writer blockWriter = Files.newBufferedWriter(Paths.get("blocks.csv"));
        CSVPrinter blockCsvPrinter = new CSVPrinter(blockWriter, CSVFormat.DEFAULT.withHeader("height", "hash", "prev_hash", "nonce"));
        for (Block block : this.blocks) {
            List<String> data = new ArrayList<>();
            data.add(Integer.toString(block.getHeight()));
            data.add(block.getHash());
            data.add(block.getPrevHash());
            data.add(Integer.toString(block.getNonce()));
            blockCsvPrinter.printRecord(data);
        }
        blockCsvPrinter.flush();
    }

    private void exportTransactionData() throws IOException {
        Writer transactionWriter = Files.newBufferedWriter(Paths.get("transactions.csv"));
        CSVPrinter transactionCsvPrinter = new CSVPrinter(transactionWriter, CSVFormat.DEFAULT.withHeader("id", "sender", "recipient", "amount", "block_height"));
        int transactionCount = 1;
        for (Block block : this.blocks) {
            for (Transaction transaction : block.getTransactions()) {
                List<String> data = new ArrayList<>();
                data.add(Integer.toString(transactionCount++));
                data.add(transaction.getSender());
                data.add(transaction.getRecipient());
                data.add(Integer.toString(transaction.getAmount()));
                data.add(Integer.toString(block.getHeight()));
                transactionCsvPrinter.printRecord(data);
            }
        }
        transactionCsvPrinter.flush();
    }

    private void exportNFTData() throws IOException {
        Writer NFTWriter = Files.newBufferedWriter(Paths.get("nfts.csv"));
        CSVPrinter NFTCsvPrinter = new CSVPrinter(NFTWriter, CSVFormat.DEFAULT.withHeader("id", "contract", "owner", "data", "block_height"));
        int NFTCount = 1;
        for (Block block : this.blocks) {
            for (NFT nft : block.getNFTs()) {
                List<String> data = new ArrayList<>();
                data.add(Integer.toString(NFTCount++));
                data.add(nft.getContract());
                data.add(nft.getOwner());
                data.add(nft.getData());
                data.add(Integer.toString(block.getHeight()));
                NFTCsvPrinter.printRecord(data);
            }
        }
        NFTCsvPrinter.flush();
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

        if(importToDatabase) {
            Main.db.getBlocksTable().insert(block.getHeight(), block.getHash(), block.getPrevHash(), block.getNonce());
            for (Transaction transaction : block.getTransactions()) {
                Main.db.getTransactionsTable().insert(transaction.getSender(), transaction.getRecipient(), transaction.getAmount(), block.getHeight());
            }
            for (NFT nft : block.getNFTs()) {
                Main.db.getNFTsTable().insert(nft.getContract(), nft.getOwner(), nft.getData(), block.getHeight());
            }
        }
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

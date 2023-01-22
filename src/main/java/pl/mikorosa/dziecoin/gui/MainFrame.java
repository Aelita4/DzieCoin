package pl.mikorosa.dziecoin.gui;

import pl.mikorosa.dziecoin.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    Blockchain bc;
    String addr;

    public JPanel panel;
    private JTabbedPane menu;
    private JLabel walletAddress;
    private JLabel balance;
    private JLabel height;
    private JTree blockchainTree;
    private JTree nftTree;
    private JButton createMintNFTButton;
    private JTextField amountToSend;
    private JButton sendSubmit;
    private JComboBox sendRecipient;
    private JLabel sendAmount;
    private JTree mineBlockTree;
    private JButton mineBlockButton;
    private JButton payForTuitionButton;
    private JButton payForAdvanceButton;
    private JButton validateChainButton;
    private JTextField recipient;
    private JButton exportButton;
    private JCheckBox exportBlockDataCheckBox;
    private JCheckBox exportTransactionDataCheckBox;
    private JCheckBox exportNFTDataCheckBox;
    private JButton clearButton;
    private DefaultMutableTreeNode rootBlocks;
    private DefaultMutableTreeNode rootNFT;
    private DefaultMutableTreeNode rootMineBlock;
    private List<Transaction> transactions;
    private List<NFT> nfts;
    private int howMuchLeftToSpend;

    public MainFrame() {
        super("DzieCoin");
        transactions = new ArrayList<>();
        nfts = new ArrayList<>();

        mineBlockButton.setEnabled(false);
        clearButton.setEnabled(false);

        this.bc = Main.blockchain;
        addr = this.bc.getBlocks().get(0).getTransactions().get(0).getRecipient();
        howMuchLeftToSpend = this.bc.getAddressBalance(addr);

        this.walletAddress.setText(addr);

        this.setContentPane(this.panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 360));
        this.pack();

        this.balance.setText(Integer.toString(this.bc.getAddressBalance(addr)));
        this.height.setText(Integer.toString(this.bc.getBlocks().size()));

        JTreeBlocks();
        JTreeNFTs();

        rootMineBlock = new DefaultMutableTreeNode("Objects to add to block");
        rootMineBlock.removeAllChildren();
        DefaultTreeModel model = new DefaultTreeModel(rootMineBlock);
        mineBlockTree.setModel(model);

        sendSubmit.addActionListener(e -> {
            String sendTo = recipient.getText();
            int howMuch;
            if(sendTo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Input correct wallet address");
                return;
            }
            try {
                howMuch = Integer.parseInt(amountToSend.getText());
                if(howMuch <= 0) throw new NumberFormatException("Value cannot be lower or equal to 0");
            } catch(NumberFormatException er) {
                JOptionPane.showMessageDialog(null, "Input correct amount greater than 0");
                return;
            }
            if(bc.getAddressBalance(addr) - howMuch < 0 || howMuchLeftToSpend - howMuch < 0) {
                JOptionPane.showMessageDialog(null, "Transaction reverted, not enough funds");
                return;
            }

            DefaultMutableTreeNode a = new DefaultMutableTreeNode("Transaction");
            transactions.add(new Transaction(addr, sendTo, howMuch));

            a.add(new DefaultMutableTreeNode("Sender: " + addr));
            a.add(new DefaultMutableTreeNode("Recipient: " + sendTo));
            a.add(new DefaultMutableTreeNode("Amount: " + howMuch));

            rootMineBlock.add(a);
            DefaultTreeModel model14 = new DefaultTreeModel(rootMineBlock);
            mineBlockTree.setModel(model14);
            JOptionPane.showMessageDialog(null, "Transaction created, mine block to add it to blockchain");
            howMuchLeftToSpend -= howMuch;
            mineBlockButton.setEnabled(true);
            clearButton.setEnabled(true);
            mineBlockButton.setText("Mine block");
        });

        validateChainButton.addActionListener(e -> {
            Block.VerifyCodes status = bc.validateChain();

            if(status == Block.VerifyCodes.SUCCESS) JOptionPane.showMessageDialog(null, "Chain is valid", "Verification", JOptionPane.INFORMATION_MESSAGE);
            else if(status == Block.VerifyCodes.HASH_MISMATCH) JOptionPane.showMessageDialog(null, "Hash mismatch found", "Verification", JOptionPane.ERROR_MESSAGE);
            else if(status == Block.VerifyCodes.INVALID_POW_SIGNATURE) JOptionPane.showMessageDialog(null, "Invalid proof of work signature found", "Verification", JOptionPane.ERROR_MESSAGE);
            else if(status == Block.VerifyCodes.INVALID_BLOCK_HASH) JOptionPane.showMessageDialog(null, "Invalid block hash found", "Verification", JOptionPane.ERROR_MESSAGE);
        });
        payForTuitionButton.addActionListener(e -> {
            int input = JOptionPane.showConfirmDialog(null, "6 DC will be sent to pay tuition\nAre you sure?", "Payment Confirmation", JOptionPane.YES_NO_OPTION);
            if(input == JOptionPane.YES_OPTION) {
                if(bc.getAddressBalance(addr) - 6 < 0 || howMuchLeftToSpend - 6 < 0) JOptionPane.showMessageDialog(null, "Transaction reverted, not enough funds");
                else {
                    DefaultMutableTreeNode a = new DefaultMutableTreeNode("Transaction");

                    a.add(new DefaultMutableTreeNode("Sender: " + addr));
                    a.add(new DefaultMutableTreeNode("Recipient: wsiz"));
                    a.add(new DefaultMutableTreeNode("Amount: 6"));

                    rootMineBlock.add(a);
                    DefaultTreeModel model13 = new DefaultTreeModel(rootMineBlock);
                    mineBlockTree.setModel(model13);

                    transactions.add(new Transaction(addr, "wsiz", 6));

                    JOptionPane.showMessageDialog(null, "Transaction created, mine block to add it to blockchain");
                    howMuchLeftToSpend -= 6;
                    mineBlockButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    mineBlockButton.setText("Mine block");
                }
            }
        });
        payForAdvanceButton.addActionListener(e -> {
            int input = JOptionPane.showConfirmDialog(null, "1 DC will be sent to pay advance\nAre you sure?", "Payment Confirmation", JOptionPane.YES_NO_OPTION);
            if(input == JOptionPane.YES_OPTION) {
                if(bc.getAddressBalance(addr) - 1 < 0 || howMuchLeftToSpend - 1 < 0) JOptionPane.showMessageDialog(null, "Transaction reverted, not enough funds");
                else {
                    DefaultMutableTreeNode a = new DefaultMutableTreeNode("Transaction");

                    a.add(new DefaultMutableTreeNode("Sender: " + addr));
                    a.add(new DefaultMutableTreeNode("Recipient: wsiz"));
                    a.add(new DefaultMutableTreeNode("Amount: 1"));

                    rootMineBlock.add(a);
                    DefaultTreeModel model12 = new DefaultTreeModel(rootMineBlock);
                    mineBlockTree.setModel(model12);

                    transactions.add(new Transaction(addr, "wsiz", 1));

                    JOptionPane.showMessageDialog(null, "Transaction created, mine block to add it to blockchain");
                    howMuchLeftToSpend -= 1;
                    mineBlockButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    mineBlockButton.setText("Mine block");
                }
            }
        });

        mineBlockButton.addActionListener(e -> {
            mineBlockButton.setEnabled(false);
            clearButton.setEnabled(false);
            Block block = new Block(bc.getLatestBlock().getHash(), bc.getLength() + 1, transactions);

            for (NFT nft : nfts) {
                block.addNFT(nft);
            }

            rootMineBlock.removeAllChildren();
            mineBlockButton.setText("Mining, please wait...");
            block.mineBlock();
            mineBlockButton.setText("Mine block");
            JOptionPane.showMessageDialog(null, "Block mined successfully");

            try {
                bc.addBlock(block);
            } catch (BlockchainIntegrityException er) {
                JOptionPane.showMessageDialog(null, "Unable to add a block: " + er.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            transactions.clear();

            howMuchLeftToSpend = bc.getAddressBalance(addr);

            rootMineBlock.removeAllChildren();
            DefaultTreeModel model1 = new DefaultTreeModel(rootMineBlock);
            mineBlockTree.setModel(model1);
        });

        clearButton.addActionListener(e -> {
            int input = JOptionPane.showConfirmDialog(null, "All pending transactions will be cancelled\nAre you sure?", "Clear Confirmation", JOptionPane.YES_NO_OPTION);
            if(input == JOptionPane.YES_OPTION) {
                mineBlockButton.setEnabled(false);
                clearButton.setEnabled(false);

                transactions.clear();

                howMuchLeftToSpend = bc.getAddressBalance(addr);

                rootMineBlock.removeAllChildren();
                DefaultTreeModel model1 = new DefaultTreeModel(rootMineBlock);
                mineBlockTree.setModel(model1);
            }
        });

        exportButton.addActionListener(e -> {

        });

        createMintNFTButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Input data for NFT: ");
            if(input != null && !input.isEmpty()) {
                NFT n = new NFT(addr, input);
                nfts.add(n);

                DefaultMutableTreeNode a = new DefaultMutableTreeNode("NFT");

                a.add(new DefaultMutableTreeNode("Contract: " + n.getContract()));
                a.add(new DefaultMutableTreeNode("Owner: " + addr));
                a.add(new DefaultMutableTreeNode("Data: " + input));

                rootMineBlock.add(a);
                DefaultTreeModel model13 = new DefaultTreeModel(rootMineBlock);
                mineBlockTree.setModel(model13);

                JOptionPane.showMessageDialog(null, "NFT minted, mine block to add it to blockchain");
                mineBlockButton.setEnabled(true);
                clearButton.setEnabled(true);
                mineBlockButton.setText("Mine block");
            }
        });
    }

    private void JTreeBlocks() {
        rootBlocks = new DefaultMutableTreeNode("Blockchain");
        for (Block block : this.bc.getBlocks()) {
            DefaultMutableTreeNode b = new DefaultMutableTreeNode(block.getHeight() + " (" + block.getHash() + ")");
            rootBlocks.add(b);
            int i = 1;
            for (Transaction transaction : block.getTransactions()) {
                DefaultMutableTreeNode t = new DefaultMutableTreeNode("Transaction " + i++);
                t.add(new DefaultMutableTreeNode("Sender: " + transaction.getSender()));
                t.add(new DefaultMutableTreeNode("Recipient: " + transaction.getRecipient()));
                t.add(new DefaultMutableTreeNode("Amount: " + transaction.getAmount()));
                b.add(t);
            }
        };
        DefaultTreeModel model = new DefaultTreeModel(rootBlocks);
        blockchainTree.setModel(model);
    }

    private void JTreeNFTs() {
        rootNFT = new DefaultMutableTreeNode("NFTs");
        for (Block block : this.bc.getBlocks()) {
            for (NFT nft : block.getNFTs()) {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(nft.getData());
                n.add(new DefaultMutableTreeNode("Contract: " + nft.getContract()));
                n.add(new DefaultMutableTreeNode("Owner: " + nft.getOwner()));
                n.add(new DefaultMutableTreeNode("In block: " + block.getHeight()));
                rootNFT.add(n);
            }
        };
        DefaultTreeModel model = new DefaultTreeModel(rootNFT);
        nftTree.setModel(model);
    }

    public void onNewBlockUpdate() {
        Block newestBlock = this.bc.getLatestBlock();

        this.balance.setText(Integer.toString(this.bc.getAddressBalance(addr)));
        this.height.setText(Integer.toString(this.bc.getBlocks().size()));

        DefaultMutableTreeNode b = new DefaultMutableTreeNode(newestBlock.getHeight() + " (" + newestBlock.getHash() + ")");
        rootBlocks.add(b);
        int i = 1;
        for (Transaction transaction : newestBlock.getTransactions()) {
            DefaultMutableTreeNode t = new DefaultMutableTreeNode("Transaction " + i++);
            t.add(new DefaultMutableTreeNode("Sender: " + transaction.getSender()));
            t.add(new DefaultMutableTreeNode("Recipient: " + transaction.getRecipient()));
            t.add(new DefaultMutableTreeNode("Amount: " + transaction.getAmount()));
            b.add(t);
        }
        DefaultTreeModel model = new DefaultTreeModel(rootBlocks);
        blockchainTree.setModel(model);

        for (NFT nft : newestBlock.getNFTs()) {
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(nft.getData());
            n.add(new DefaultMutableTreeNode("Contract: " + nft.getContract()));
            n.add(new DefaultMutableTreeNode("Owner: " + nft.getOwner()));
            n.add(new DefaultMutableTreeNode("In block: " + newestBlock.getHeight()));
            rootNFT.add(n);
        }
        DefaultTreeModel model1 = new DefaultTreeModel(rootNFT);
        nftTree.setModel(model1);
    }
}

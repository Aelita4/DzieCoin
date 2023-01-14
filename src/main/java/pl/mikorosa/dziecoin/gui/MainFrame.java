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

        });

        validateChainButton.addActionListener(e -> {

        });

        payForTuitionButton.addActionListener(e -> {

        });
        payForAdvanceButton.addActionListener(e -> {

        });

        mineBlockButton.addActionListener(e -> {

        });

        clearButton.addActionListener(e -> {

        });

        exportButton.addActionListener(e -> {

        });

        createMintNFTButton.addActionListener(e -> {

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

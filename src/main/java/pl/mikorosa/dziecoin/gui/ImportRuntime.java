package pl.mikorosa.dziecoin.gui;

import pl.mikorosa.dziecoin.Main;
import pl.mikorosa.dziecoin.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;

import static pl.mikorosa.dziecoin.Main.*;

public class ImportRuntime extends JFrame {
    private JPanel panel;
    private JTextField difficulty;
    private JButton runButton;
    private JTabbedPane tabbedPane1;
    private JButton browseBlock;
    private JButton browseTransaction;
    private JButton browseNFT;
    private JCheckBox updateDatabaseCheckBox;
    private JLabel blockLabel;
    private JLabel transactionLabel;
    private JLabel NFTLabel;

    private String blocksCSVPath;
    private String transactionsCSVPath;
    private String NFTsCSVPath;

    public enum ImportType {
        NEW,
        FROM_FILE,
        FROM_DATABASE
    }

    public ImportRuntime() {
        super("DzieCoin Initial Configuration");
        this.setContentPane(this.panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 360));
        this.pack();

        runButton.addActionListener(e -> {
            int diff = 0;
            ImportType type = null;

            String errorMessage = "";
            String input;

            try {
                input = difficulty.getText();
                if(input == null) throw new Exception();
                diff = Integer.parseInt(input);
                if(diff <= 0) throw new Exception();
            } catch(Exception ex) {
                errorMessage = "Input correct number";
            }

            if(tabbedPane1.getSelectedIndex() == 0) type = ImportType.NEW;
            if(tabbedPane1.getSelectedIndex() == 1) type = ImportType.FROM_FILE;
            if(tabbedPane1.getSelectedIndex() == 2) type = ImportType.FROM_DATABASE;

            if(!errorMessage.isEmpty()) JOptionPane.showMessageDialog(null, errorMessage, "DzieCoin Initial Configuration", JOptionPane.ERROR_MESSAGE);
            else if(type == ImportType.FROM_FILE) {
                if(blocksCSVPath == null || transactionsCSVPath == null || NFTsCSVPath == null) JOptionPane.showMessageDialog(null, "Choose all three files to proceed", "DzieCoin Initial Configuration", JOptionPane.ERROR_MESSAGE);
                else {
                    runButton.setEnabled(false);

                    Main.difficulty = diff;
                    Main.db = new DatabaseConnection();

                    this.setVisible(false);
                    this.dispose();

                    initImport(blocksCSVPath, transactionsCSVPath, NFTsCSVPath, updateDatabaseCheckBox.isSelected());
                }
            }
            else {
                runButton.setEnabled(false);

                Main.difficulty = diff;
                Main.db = new DatabaseConnection();

                this.setVisible(false);
                this.dispose();

                if(type == ImportType.NEW) initFromZero(updateDatabaseCheckBox.isSelected());
                if(type == ImportType.FROM_DATABASE) initFromDatabase(updateDatabaseCheckBox.isSelected());
            }
        });

        browseBlock.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            blockLabel.setText(fc.getSelectedFile().getName());
            blocksCSVPath = fc.getSelectedFile().getAbsolutePath();
        });

        browseTransaction.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            transactionLabel.setText(fc.getSelectedFile().getName());
            transactionsCSVPath = fc.getSelectedFile().getAbsolutePath();
        });

        browseNFT.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            NFTLabel.setText(fc.getSelectedFile().getName());
            NFTsCSVPath = fc.getSelectedFile().getAbsolutePath();
        });
    }
}
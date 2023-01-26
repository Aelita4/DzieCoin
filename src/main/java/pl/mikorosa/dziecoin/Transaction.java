package pl.mikorosa.dziecoin;

public class Transaction extends BlockData {
    private String recipient;
    private int amount;

    public Transaction(String sender, String recipient, int amount) {
        this.owner = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getSender() {
        return owner;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "sender='" + owner + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                '}';
    }
}

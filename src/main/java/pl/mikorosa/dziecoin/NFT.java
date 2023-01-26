package pl.mikorosa.dziecoin;

public class NFT extends BlockData {
    private String contract;
    private String data;

    public NFT(String owner, String data) {
        this.owner = owner;
        this.data = data;
        this.contract = CalculateHash.md5sum(owner + data);
    }

    public NFT(String contract, String owner, String data) {
        String testHash = CalculateHash.md5sum(owner + data);
        if(!testHash.equals(contract)) throw new IllegalStateException("NFT contract and data does not match");
        this.owner = owner;
        this.data = data;
        this.contract = contract;
    };

    public String getContract() {
        return contract;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "NFT{" +
                "contract='" + contract + '\'' +
                ", owner='" + owner + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

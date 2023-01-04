package pl.mikorosa.dziecoin;

import java.security.*;

public class Wallet {
    private String address;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    Wallet() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.genKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            address = CalculateHash.md5sum(publicKey.getEncoded().toString());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algo: " + e);
        } catch (ProviderException e) {
            System.out.println("Provider exception: " + e);
        } catch (Exception e) {
            System.out.println("General exception: " + e);
        }
    }

    public String getAddress() {
        return address;
    }
}

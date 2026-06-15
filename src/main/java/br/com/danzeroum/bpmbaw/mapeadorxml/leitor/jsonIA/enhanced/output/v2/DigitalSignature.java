package br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.enhanced.output.v2;

/**
 * Digital Signature for integrity verification
 */
public class DigitalSignature {
    private String algorithm;
    private String signature;
    private String publicKeyFingerprint;
    private String timestamp;
    private String signerIdentity;

    public DigitalSignature() {
        this.algorithm = "SHA256withRSA";
    }

    // Getters and setters
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getPublicKeyFingerprint() { return publicKeyFingerprint; }
    public void setPublicKeyFingerprint(String publicKeyFingerprint) { this.publicKeyFingerprint = publicKeyFingerprint; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSignerIdentity() { return signerIdentity; }
    public void setSignerIdentity(String signerIdentity) { this.signerIdentity = signerIdentity; }
}
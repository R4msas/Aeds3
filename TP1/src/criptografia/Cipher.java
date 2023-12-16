package criptografia;

public abstract class Cipher {
  protected String key;

  public Cipher(String key) {
    this.key = key;
  }

  public abstract String cipher(String input);

  public abstract String decipher(String input);

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}

package criptografia;

public class Vigenere extends Cipher {
  public Vigenere(String key) {
    super(key);
  }

  @Override
  public String cipher(String input) {
    String output = "";
    for (int i = 0, j = 0; i < input.length(); i++, j = (j + 1) % key.length()) {
      output += (char) ((input.charAt(i) + key.charAt(j)) % (Character.MAX_VALUE + 1));
    }

    return output;
  }

  @Override
  public String decipher(String input) {
    String output = "";
    for (int i = 0, j = 0; i < input.length(); i++, j = (j + 1) % key.length()) {
      output += (char) ((input.charAt(i) - key.charAt(j)) % (Character.MAX_VALUE + 1));
    }

    return output;
  }
}

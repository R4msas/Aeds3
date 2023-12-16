package criptografia;

import java.io.File;

import main.RAF;
import model.PlayerRegister;

public class FileCipher {
  private Cipher cipher;

  public FileCipher(Cipher cipher) {
    this.cipher = cipher;
  }

  public boolean cipherFile(File file) {
    boolean booleanReturn = true;

    RAF inputRaf = null;
    RAF outputRaf = null;
    try {
      inputRaf = new RAF(file, "r");
      inputRaf.readInt();

      File outputFile = new File("resources/criptografia/cipher.db");
      outputFile.delete();

      outputRaf = new RAF(outputFile, "rw");

      while (inputRaf.canRead()) {
        PlayerRegister register = new PlayerRegister();
        register.fromFile(inputRaf, false);
        outputRaf.writeUTF(cipher.cipher(register.toString()));
      }

      inputRaf.close();
      outputRaf.close();
    } catch (Exception e) {
      booleanReturn = false;
      e.printStackTrace();
    }

    return booleanReturn;
  }

  public boolean decipherFile(File file) {
    boolean booleanReturn = true;

    RAF inputRaf = null;
    RAF outputRaf = null;

    try {
      inputRaf = new RAF(file, "r");

      File outputFile = new File("resources/criptografia/decipher.db");
      outputFile.delete();
      outputRaf = new RAF(outputFile, "rw");

      while (inputRaf.canRead()) {
        String stringToDecipher = inputRaf.readUTF();
        outputRaf.writeUTF(cipher.decipher(stringToDecipher));
      }

      inputRaf.close();
      outputRaf.close();
    } catch (Exception e) {
      booleanReturn = false;
      e.printStackTrace();

    }

    return booleanReturn;
  }

  public Cipher getCipher() {
    return cipher;
  }

  public void setCipher(Cipher cipher) {
    this.cipher = cipher;
  }

  public static void main(String[] args) {
    // Cipher columnCipher = new ColumnCipher("cavalo");
    Cipher vingenereCipher = new Vigenere("cavalo");
    FileCipher fileCipher = new FileCipher(vingenereCipher);

    fileCipher.cipherFile(new File("resources/db/csgo_players.db"));
    fileCipher.decipherFile(new File("resources/criptografia/cipher.db"));
  }
}

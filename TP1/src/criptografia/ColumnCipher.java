package criptografia;

import java.util.Arrays;

public class ColumnCipher extends Cipher {
  public ColumnCipher(String key) {
    super(key);
  }

  @Override
  public String cipher(String input) {
    int[] positions = getPositionArray();
    String[] columns = new String[positions.length];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = "";
    }

    for (int i = 0, j = 0; i < input.length(); i++, j = (j + 1) % positions.length) {
      columns[positions[j]] += input.charAt(i);
    }

    String result = "";
    for (String column : columns) {
      result += column;
    }

    return result;
  }

  public String decipher(String input) {
    int[] positions = getPositionArray();
    String[] columns = new String[positions.length];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = "";
    }

    int charactersPerString = input.length() / key.length();
    int columnsWithPlusOneChar = input.length() % key.length();

    int inputIndex = 0;
    for (int i = 0; i < columns.length; i++) {
      int numbCharacters = positions[i] < columnsWithPlusOneChar ? charactersPerString + 1 : charactersPerString;
      columns[i] = input.substring(inputIndex, inputIndex + numbCharacters);
      inputIndex += numbCharacters;
    }

    String returnString = "";
    for (int i = 0; i < charactersPerString + 1; i++) {
      for (int j = 0; j < columns.length && i < columns[positions[j]].length(); j++) {
        returnString += columns[positions[j]].charAt(i);
      }
    }

    return returnString;
  }

  private int[] getPositionArray() {
    int[] positions = new int[key.length()];
    String sortedKey = sortKey();

    for (int i = 0; i < positions.length; i++) {
      positions[i] = sortedKey.indexOf(key.charAt(i));
    }

    return positions;
  }

  private String sortKey() {
    char[] chars = key.toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}

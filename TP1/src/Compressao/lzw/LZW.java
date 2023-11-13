package Compressao.lzw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LZW {
  private HashMap<String, Short> compressionTable;
  private HashMap<Short, String> discompressionTable;
  private short biggestIndex;

  public LZW() {
    setTables();
  }

  public ArrayList<Short> compact(String input) {
    ArrayList<Short> outputList = new ArrayList<>();

    for (int i = 0; i < input.length(); i++) {
      String searchQuery = "";
      short lastIndexFound = -1;

      boolean lastIndexIsValid = true;
      while (lastIndexIsValid && i < input.length()) {
        searchQuery += input.charAt(i);

        Short foundIndex = compressionTable.get(searchQuery);
        lastIndexIsValid = foundIndex != null;

        if (lastIndexIsValid) {
          discompressionTable.put(foundIndex, searchQuery);
          lastIndexFound = foundIndex;
          ++i;
        }
      }
      --i;

      if (!lastIndexIsValid) {
        addToCompressionTable(searchQuery);
      }

      if (lastIndexFound >= 0) {
        outputList.add(lastIndexFound);
      }
    }

    return outputList;
  }

  private boolean addToCompressionTable(String string) {
    if (compressionTable.size() < Short.MAX_VALUE * 2 + 1) {
      compressionTable.put(string, biggestIndex++);
      return true;
    } else {
      return false;
    }
  }

  public String discompact(short input) {
    if (discompressionTable.isEmpty()) {
      return "";
    }

    return discompressionTable.get(input);
  }

  public String discompact(List<Short> input) {
    if (discompressionTable.isEmpty()) {
      return "";
    }

    String outputString = "";
    for (Short short1 : input) {
      outputString += discompressionTable.get(short1);
    }

    return outputString;
  }

  public void setTables() {
    this.compressionTable = new HashMap<String, Short>();
    this.discompressionTable = new HashMap<Short, String>();
    this.biggestIndex = 0;

    List<String> list = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "A", "b", "B", "c", "C", "d",
        "D", "e", "E", "f", "F", "g", "G", "h", "H", "i", "I", "j", "J", "k", "K", "l", "L", "m", "M", "n", "N", "o",
        "O", "p", "P", "q", "Q", "r", "R", "s", "S", "t", "T", "u", "U", "v", "V", "w", "W", "x", "X", "y", "Y", "z",
        "Z", " ");

    list.forEach(element -> addToCompressionTable(element));
  }

  public void setCompressionTable(HashMap<String, Short> compressionTable) {
    this.compressionTable = compressionTable;
  }

  public HashMap<Short, String> getDiscompressionTable() {
    return discompressionTable;
  }

  public void setDiscompressionTable(HashMap<Short, String> discompressionTable) {
    this.discompressionTable = discompressionTable;
  }

  public short getBiggestIndex() {
    return biggestIndex;
  }

  public void setBiggestIndex(short biggestIndex) {
    this.biggestIndex = biggestIndex;
  }

  public HashMap<String, Short> getCompressionTable() {
    return compressionTable;
  }
}
package Compressao.lzw;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import main.RAF;

public class LZWTableFile extends LZW {
  private static final String tableFilePath = "resources/compressao/tableLZW";

  protected static final String outputFileSuffix = ".db";
  protected static int compressionCounter = 0;

  protected int compressionIndex;

  public LZWTableFile() {
    super();
    setNewCompressionIndex();
  }

  public void setNewCompressionIndex() {
    this.compressionIndex = compressionCounter++;
  }

  public void writeTable() throws IOException {
    File outputFile = new File(tableFilePath + compressionIndex + outputFileSuffix);
    if (outputFile.exists()) {
      outputFile.delete();
    }

    HashMap<Short, String> hashMap = getDiscompressionTable();

    RAF raf = new RAF(outputFile, "rw");
    raf.writeShort(hashMap.size());

    for (Entry<Short, String> entry : hashMap.entrySet()) {
      raf.writeShort(entry.getKey());
      raf.writeUTF(entry.getValue());
    }

    raf.close();
  }

  @Override
  public void setTables() {
    File outputFile = new File(tableFilePath + compressionIndex + outputFileSuffix);
    if (!outputFile.exists()) {
      super.setTables();
      return;
    }

    try {
      HashMap<String, Short> compressionTable = new HashMap<String, Short>();
      HashMap<Short, String> discompressionTable = new HashMap<Short, String>();
      RAF raf = new RAF(outputFile, "rw");

      short tableSize = raf.readShort();
      short biggestIndex = -1;
      for (short i = 0; i < tableSize; i++) {
        short index = raf.readShort();
        String value = raf.readUTF();

        compressionTable.put(value, index);
        discompressionTable.put(index, value);
        biggestIndex = index > biggestIndex ? index : biggestIndex;
      }

      raf.close();

      setCompressionTable(compressionTable);
      setDiscompressionTable(discompressionTable);
      setBiggestIndex(++biggestIndex);
    } catch (IOException e) {
      e.printStackTrace();
      super.setTables();
    }
  }

  public long getTableFileSize() {
    return new File(tableFilePath + compressionIndex + outputFileSuffix).length();
  }

}

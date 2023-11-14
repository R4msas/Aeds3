package Compressao.lzw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import main.RAF;
import model.LZWCompressedPlayerRegister;
import model.PlayerRegister;

public class LZWPlayerFileCompresser extends LZWTableFile {
  private static final String compressedFilePath = "resources/compressao/playerLZW";

  public LZWPlayerFileCompresser() {
    super();
  }

  public long compressPlayerFile(String playerDBFile, boolean numbersToHex) throws IOException {
    long startTime = System.currentTimeMillis();
    setTables();

    RAF inputRaf = new RAF(playerDBFile, "r");
    RAF outputRaf = getCompressedFileRaf(true);

    outputRaf.writeBoolean(numbersToHex);

    // write biggest id
    ArrayList<Short> biggestID = compact(inputRaf.readInt() + "");
    outputRaf.writeByte(biggestID.size());
    for (short idBytes : biggestID) {
      outputRaf.writeShort(idBytes);
    }

    while (inputRaf.canRead()) {
      PlayerRegister register = new PlayerRegister();
      register.fromFile(inputRaf, true);

      LZWCompressedPlayerRegister compressedRegister = new LZWCompressedPlayerRegister(register, this, numbersToHex);
      compressedRegister.writeOnFile(outputRaf);
    }

    inputRaf.close();
    outputRaf.close();
    writeTable();

    return System.currentTimeMillis() - startTime;
  }

  public long discompressPlayerFile(String outputDBFile) throws IOException {
    long startTime = System.currentTimeMillis();
    setTables();

    RAF inputRaf = getCompressedFileRaf(false);
    RAF outputRaf = new RAF(outputDBFile, "rw");

    boolean numbersToHex = inputRaf.readBoolean();

    // write biggest id
    String biggestID = "";
    byte compactedIDSize = inputRaf.readByte();
    for (byte i = 0; i < compactedIDSize; i++) {
      biggestID += discompact(inputRaf.readShort());
    }
    outputRaf.writeInt(Integer.parseInt(biggestID));

    while (inputRaf.canRead()) {
      LZWCompressedPlayerRegister compressedRegister = new LZWCompressedPlayerRegister(this, numbersToHex);
      PlayerRegister register = compressedRegister.getRegisterFromCompressedFile(inputRaf, true);

      outputRaf.write(register.toByteArray());
    }

    inputRaf.close();
    outputRaf.close();
    return System.currentTimeMillis() - startTime;
  }

  public RAF getCompressedFileRaf(boolean write) throws FileNotFoundException {
    File outputFile = new File(compressedFilePath + compressionIndex + outputFileSuffix);
    if (outputFile.exists() && write) {
      outputFile.delete();
      return new RAF(outputFile, "rw");
    } else if (write) {
      return new RAF(outputFile, "rw");
    } else {
      return new RAF(outputFile, "r");
    }
  }

  public String getCompressedFileSize() {
    long fileLength = new File(compressedFilePath + compressionIndex + outputFileSuffix).length();
    return String.format("%.2f KB", fileLength / 1024.0);
  }

  public String getCombinedFilesSize() {
    double compressed = Double.parseDouble(getCompressedFileSize().replace(" KB", ""));
    double table = Double.parseDouble(getTableFileSize().replace(" KB", ""));
    return String.format("%.2f KB", compressed + table);
  }
}

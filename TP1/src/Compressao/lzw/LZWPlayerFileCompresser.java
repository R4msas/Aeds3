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

  public void compressPlayerFile(String playerDBFile, boolean numbersToHex) throws IOException {
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
  }

  public void discompressPlayerFile(String outputDBFile) throws IOException {
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

  public static void main(String[] args) throws IOException {
    LZWPlayerFileCompresser compresser = new LZWPlayerFileCompresser();

    compresser.compressPlayerFile("resources/db/csgo_players.db", true);
    compresser.discompressPlayerFile("resources/db/csgo_players.db");
  }
}

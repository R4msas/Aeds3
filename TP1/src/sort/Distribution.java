package sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import main.RAF;
import model.PlayerRegister;

public class Distribution {
  public int header; // Biggest ID
  public String mainFileName;
  public String mainFilePath;
  public int numberFiles;
  public int distributionSize;

  public Distribution(String mainFileName, String mainFilePath, int numberFiles, int distributionSize) {
    this.mainFileName = mainFileName;
    this.mainFilePath = mainFilePath;
    this.numberFiles = numberFiles;
    this.distributionSize = distributionSize;
  }

  public File[] distribute() throws IOException {
    RAF arqPrincipal = new RAF(mainFilePath + mainFileName, "r");
    header = arqPrincipal.readInt();

    File[] files = new File[numberFiles];
    RAF[] tmps = new RAF[numberFiles];
    for (int i = 0; i < tmps.length; i++) {
      files[i] = new File(mainFilePath + "tmp" + i + ".db");
      tmps[i] = new RAF(files[i], "rw");
      tmps[i].writeInt(header);
    }

    for (int i = 0; arqPrincipal.canRead(); i++) {
      ArrayList<PlayerRegister> registers = new ArrayList<>();

      for (int j = 0; j < distributionSize && arqPrincipal.canRead(); j++) {
        // Lendo um jogador do arquivo principal
        PlayerRegister pr = new PlayerRegister();
        pr.fromFile(arqPrincipal, true);

        if (!pr.isTombstone()) {
          // salvando no array list
          registers.add(pr);
        } else {
          --j;
        }
      }

      MergeSort.mergeSort(registers);
      for (PlayerRegister pr : registers) {
        tmps[i % numberFiles].write(pr.toByteArray());
      }
    }

    arqPrincipal.close();
    for (RAF RAF : tmps) {
      RAF.close();
    }

    return files;
  }
}

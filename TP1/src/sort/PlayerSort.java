package sort;

import java.io.IOException;

public class PlayerSort {
  private int header; // Biggest ID
  private String mainFileName;
  private String mainFilePath;

  public PlayerSort() {
  }

  public PlayerSort(String mainFileName, String mainFilePath) {
    setMainFileName(mainFileName);
    setMainFilePath(mainFilePath);
  }

  @Override
  public String toString() {
    return "PlayerSort [header=" + header + ", mainFileName=" + mainFileName + ", mainFilePath=" + mainFilePath + "]";
  }

  public void sortFixedSize(int numberFiles, int size) throws IOException {
    Distribution distribution = new Distribution(mainFileName, mainFilePath, numberFiles, size);
    Intercalation intercalation = new Intercalation(mainFileName, mainFilePath);
    intercalation.intercalate(distribution.distribute(), size);
  }

  public void sort(int numberFiles, int distributionSize) throws IOException {
    Distribution distribution = new Distribution(mainFileName, mainFilePath, numberFiles, distributionSize);
    Intercalation intercalation = new Intercalation(mainFileName, mainFilePath);

    intercalation.intercalate(distribution.distribute());
  }

  public void sort(int heapSize) throws IOException {
    PlayerHeap heap = new PlayerHeap(heapSize);
    Intercalation intercalation = new Intercalation(mainFileName, mainFilePath);

    intercalation.intercalate(heap.createTemporaryFiles(mainFileName, mainFilePath));
  }

  public String getMainFile() {
    return mainFileName;
  }

  public void setMainFileName(String mainFileName) {
    this.mainFileName = mainFileName;
  }

  public String getMainFilePath() {
    return mainFilePath;
  }

  public void setMainFilePath(String mainFilePath) {
    this.mainFilePath = mainFilePath;
  }
}

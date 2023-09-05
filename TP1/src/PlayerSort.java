import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

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

  public void sort(int numberFiles, int distributionSize, int intercalationSize) throws IOException {
    intercalation(distribution(numberFiles, distributionSize), intercalationSize);
  }

  public void sort(int numberFiles, int distributionSize) throws IOException {
    intercalation(distribution(numberFiles, distributionSize));
  }

  private File[] distribution(int numberFiles, int distributionSize) throws IOException {
    RandomAccessFile arqPrincipal = new RandomAccessFile(mainFilePath + mainFileName, "r");
    header = arqPrincipal.readInt();

    File[] files = new File[numberFiles];
    RandomAccessFile[] tmps = new RandomAccessFile[numberFiles];
    for (int i = 0; i < tmps.length; i++) {
      files[i] = new File(mainFilePath + "tmp" + i + ".db");
      tmps[i] = new RandomAccessFile(files[i], "rw");
      tmps[i].writeInt(header);
    }

    for (int i = 0; arqPrincipal.getFilePointer() < arqPrincipal.length(); i++) {
      ArrayList<PlayerRegister> registers = new ArrayList<>();

      for (int j = 0; j < distributionSize && arqPrincipal.getFilePointer() < arqPrincipal.length(); j++) {
        PlayerRegister pr = new PlayerRegister();
        pr.fromFileIfNotTomb(arqPrincipal);

        if (pr.getPlayer() != null) {
          registers.add(pr);
        } else {
          --j;
        }
      }

      mergeSort(registers);
      for (PlayerRegister pr : registers) {
        tmps[i % numberFiles].write(pr.toByteArray());
      }
    }

    arqPrincipal.close();
    for (RandomAccessFile randomAccessFile : tmps) {
      randomAccessFile.close();
    }

    return files;
  }

  private void intercalation(File[] inputFiles, int segmentSize) throws IOException {
    RandomAccessFile[] inputRAF = new RandomAccessFile[inputFiles.length];

    File[] outputFiles = new File[inputFiles.length];
    RandomAccessFile[] outputRAF = new RandomAccessFile[inputFiles.length];

    // Files and RAFs initialization
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RandomAccessFile(inputFiles[i], "r");
      inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RandomAccessFile(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge
    for (int i = 0; checkStillReadable(inputRAF); i++) {
      SortedSegment[] segments = initializeSegments(inputRAF, segmentSize);
      mergeAndWrite(segments, outputRAF[i % outputRAF.length]);
    }

    if (outputRAF.length > 1 && outputRAF[1].length() > 4) {
      outputFiles = prepareForReintercalation(inputFiles, inputRAF, outputFiles, outputRAF);
      intercalation(outputFiles, segmentSize * 2);
    }

    finalizeSort(inputFiles, inputRAF, outputFiles, outputRAF);
  }

  private void intercalation(File[] inputFiles) throws IOException {
    RandomAccessFile[] inputRAF = new RandomAccessFile[inputFiles.length];

    File[] outputFiles = new File[inputFiles.length];
    RandomAccessFile[] outputRAF = new RandomAccessFile[inputFiles.length];

    // Files and RAFs initialization
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RandomAccessFile(inputFiles[i], "r");
      inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RandomAccessFile(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge
    for (int i = 0; checkStillReadable(inputRAF); i++) {
      SortedSegment[] segments = initializeSegments(inputRAF);
      mergeVariableSize(segments, outputRAF[i % outputRAF.length]);
    }

    if (outputRAF.length > 1 && outputRAF[1].length() > 4) {
      outputFiles = prepareForReintercalation(inputFiles, inputRAF, outputFiles, outputRAF);
      intercalation(outputFiles);
    }

    finalizeSort(inputFiles, inputRAF, outputFiles, outputRAF);
  }

  private SortedSegment[] initializeSegments(RandomAccessFile[] inputFiles, int size) throws IOException {
    SortedSegment[] segments = new SortedSegment[inputFiles.length];
    for (int j = 0; j < segments.length; j++) {
      segments[j] = new SortedSegment(inputFiles[j], size);
    }
    return segments;
  }

  private SortedSegment[] initializeSegments(RandomAccessFile[] inputFiles) throws IOException {
    SortedSegment[] segments = new SortedSegment[inputFiles.length];
    for (int j = 0; j < segments.length; j++) {
      segments[j] = new SortedSegment(inputFiles[j]);
    }
    return segments;
  }

  private void mergeAndWrite(SortedSegment[] toMerge, RandomAccessFile toWrite) throws IOException {
    // Merge the sorted segments and write to output
    while (true) {
      SortedSegment smallest = SortedSegment.getSmallest(toMerge);
      if (smallest != null) {
        toWrite.write(smallest.getFirstRegister().toByteArray());
        smallest.loadNextRegister();
      } else {
        // No more records to merge, exit loop
        break;
      }
    }
  }

  private void mergeVariableSize(SortedSegment[] toMerge, RandomAccessFile toWrite) throws IOException {
    while (true) {
      SortedSegment smallest = SortedSegment.getSmallest(toMerge);
      if (smallest != null) {
        toWrite.write(smallest.getFirstRegister().toByteArray());
        smallest.loadNextIfBigger();
      } else {
        // No more records to merge, exit loop
        break;
      }
    }
  }

  private boolean checkStillReadable(RandomAccessFile[] inputFiles) throws IOException {
    for (RandomAccessFile inputFile : inputFiles) {
      if (inputFile.getFilePointer() < inputFile.length()) {
        return true;
      }
    }

    return false;
  }

  private File[] prepareForReintercalation(File[] inputFiles, RandomAccessFile[] inputRAF, File[] outputFiles,
      RandomAccessFile[] outputRAF) throws IOException {

    ArrayList<File> files = new ArrayList<>();
    for (int i = 0; i < inputFiles.length; i++) {
      inputFiles[i].delete();
      inputRAF[i].close();

      if (outputRAF[i].length() > 4) {
        File renamed = new File(mainFilePath + "inputFile" + i + ".db");
        outputFiles[i].renameTo(renamed);
        files.add(renamed);
      } else {
        outputFiles[i].delete();
      }

      outputRAF[i].close();
    }

    return files.toArray(new File[0]);
  }

  private void finalizeSort(File[] inputFiles, RandomAccessFile[] inputRAF, File[] outputFiles,
      RandomAccessFile[] outputRAF) throws IOException {
    inputFiles[0].delete();
    inputRAF[0].close();
    outputFiles[0].renameTo(new File(mainFilePath + mainFileName));
    outputRAF[0].close();

    for (int i = 1; i < outputFiles.length; i++) {
      inputFiles[i].delete();
      inputRAF[i].close();
      outputFiles[i].delete();
      outputRAF[i].close();
    }
  }

  public void mergeSort(ArrayList<PlayerRegister> lista) {
    mergeSort(lista, 0, lista.size() - 1);
  }

  private void mergeSort(ArrayList<PlayerRegister> arr, int left, int right) {
    if (left < right) {
      int mid = left + (right - left) / 2;
      mergeSort(arr, left, mid);
      mergeSort(arr, mid + 1, right);
      merge(arr, left, mid, right);
    }
  }

  private void merge(ArrayList<PlayerRegister> arr, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;

    ArrayList<PlayerRegister> leftArr = new ArrayList<>();
    ArrayList<PlayerRegister> rightArr = new ArrayList<>();

    for (int i = 0; i < n1; i++) {
      leftArr.add(arr.get(left + i));
    }
    for (int j = 0; j < n2; j++) {
      rightArr.add(arr.get(mid + 1 + j));
    }

    int i = 0, j = 0, k = left;

    while (i < n1 && j < n2) {
      if (leftArr.get(i).getPlayer().getPlayerId() <= rightArr.get(j).getPlayer().getPlayerId()) {
        arr.set(k, leftArr.get(i));
        i++;
      } else {
        arr.set(k, rightArr.get(j));
        j++;
      }
      k++;
    }

    while (i < n1) {
      arr.set(k, leftArr.get(i));
      i++;
      k++;
    }

    while (j < n2) {
      arr.set(k, rightArr.get(j));
      j++;
      k++;
    }
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

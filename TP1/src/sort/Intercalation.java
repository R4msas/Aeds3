package sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import main.RAF;

public class Intercalation {
  public int header; // Biggest ID
  public String mainFileName;
  public String mainFilePath;

  public Intercalation(String mainFileName, String mainFilePath) {
    this.mainFileName = mainFileName;
    this.mainFilePath = mainFilePath;
  }

  public void intercalate(File[] inputFiles, int segmentSize) throws IOException {
    RAF[] inputRAF = new RAF[inputFiles.length];

    File[] outputFiles = new File[inputFiles.length];
    RAF[] outputRAF = new RAF[inputFiles.length];

    // Files and RAFs initialization
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RAF(inputFiles[i], "r");
      header = inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RAF(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge
    for (int i = 0; checkStillReadable(inputRAF); i++) {
      SortedSegment[] segments = initializeSegments(inputRAF, segmentSize);
      mergeAndWrite(segments, outputRAF[i % outputRAF.length]);
    }

    if (outputRAF.length > 1 && outputRAF[1].length() > 4) {
      outputFiles = prepareForReintercalation(inputFiles, inputRAF, outputFiles, outputRAF);
      intercalate(outputFiles, segmentSize * 2);
    }

    finalizeSort(inputFiles, inputRAF, outputFiles, outputRAF);
  }

  public void intercalate(File[] inputFiles) throws IOException {
    RAF[] inputRAF = new RAF[inputFiles.length];
    File[] outputFiles = new File[inputFiles.length];
    RAF[] outputRAF = new RAF[inputFiles.length];

    // Files and RAFs initialization
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RAF(inputFiles[i], "r");
      header = inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RAF(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge
    SortedSegment[] remainingSegments = null;
    for (int i = 0; checkStillReadable(inputRAF); i++) {
      if (remainingSegments == null) {
        SortedSegment[] segments = initializeSegments(inputRAF);
        remainingSegments = mergeVariableSize(segments, outputRAF[i % outputRAF.length]);
      } else {
        remainingSegments = mergeVariableSize(remainingSegments, outputRAF[i % outputRAF.length]);
      }
    }

    if (outputRAF.length > 1 && outputRAF[1].length() > 4) {
      outputFiles = prepareForReintercalation(inputFiles, inputRAF, outputFiles, outputRAF);
      intercalate(outputFiles);
    }

    finalizeSort(inputFiles, inputRAF, outputFiles, outputRAF);
  }

  private SortedSegment[] initializeSegments(RAF[] inputFiles, int size) throws IOException {
    SortedSegment[] segments = new SortedSegment[inputFiles.length];
    for (int j = 0; j < segments.length; j++) {
      segments[j] = new SortedSegment(inputFiles[j], size);
    }
    return segments;
  }

  private SortedSegment[] initializeSegments(RAF[] inputFiles) throws IOException {
    SortedSegment[] segments = new SortedSegment[inputFiles.length];
    for (int j = 0; j < segments.length; j++) {
      segments[j] = new SortedSegment(inputFiles[j]);
    }
    return segments;
  }

  private void mergeAndWrite(SortedSegment[] toMerge, RAF toWrite) throws IOException {
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

  private SortedSegment[] mergeVariableSize(SortedSegment[] toMerge, RAF toWrite) throws IOException {
    ArrayList<SortedSegment> sortedSegments = new ArrayList<>();

    while (true) {
      SortedSegment smallest = SortedSegment.getSmallest(toMerge);
      if (smallest != null) {
        toWrite.write(smallest.getFirstRegister().toByteArray());
        SortedSegment remaining = smallest.loadNextIfBigger();

        if (remaining != null) {
          sortedSegments.add(remaining);
        }
      } else {
        // No more records to merge, exit loop
        break;
      }
    }

    return sortedSegments.toArray(new SortedSegment[0]);
  }

  private boolean checkStillReadable(RAF[] inputFiles) throws IOException {
    for (RAF inputFile : inputFiles) {
      if (inputFile.canRead()) {
        return true;
      }
    }
    return false;
  }

  private File[] prepareForReintercalation(File[] inputFiles, RAF[] inputRAF, File[] outputFiles,
      RAF[] outputRAF) throws IOException {

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

  private void finalizeSort(File[] inputFiles, RAF[] inputRAF, File[] outputFiles,
      RAF[] outputRAF) throws IOException {
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

}

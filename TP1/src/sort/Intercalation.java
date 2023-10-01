package sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import main.RAF;

/**
 * A partir de arquivos temporários cria arquivos de saída até obter o novo
 * arquivo ordenado.
 */
public class Intercalation {
  public int header; // Biggest ID
  public String mainFileName;
  public String mainFilePath;

  public Intercalation(String mainFileName, String mainFilePath) {
    this.mainFileName = mainFileName;
    this.mainFilePath = mainFilePath;
  }

  public void intercalate(File[] inputFiles, int segmentSize) throws IOException {
    RAF[] inputRAF = new RAF[inputFiles.length]; // Para ler arquivos de entrada

    // Para escrever arquivos de saída
    File[] outputFiles = new File[inputFiles.length];
    RAF[] outputRAF = new RAF[inputFiles.length];

    // Inicializa os arquivos de saída e seus RandomAcessFile.
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RAF(inputFiles[i], "r");
      header = inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RAF(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge arquivos de entrada nos arquivos de saída enquanto houverem registros
    // na entrada para intercalar.
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
    RAF[] inputRAF = new RAF[inputFiles.length]; // Para ler arquivos de entrada

    File[] outputFiles = new File[inputFiles.length];
    RAF[] outputRAF = new RAF[inputFiles.length];

    // Inicializa os arquivos de saída e seus RandomAcessFile.
    for (int i = 0; i < outputRAF.length; i++) {
      inputRAF[i] = new RAF(inputFiles[i], "r");
      header = inputRAF[i].readInt();

      outputFiles[i] = new File(mainFilePath + "outputFile" + i + ".db");
      outputRAF[i] = new RAF(outputFiles[i], "rw");
      outputRAF[i].writeInt(header);
    }

    // Merge arquivos de entrada nos arquivos de saída enquanto houverem registros
    // na entrada para intercalar.
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

  // ==================== Intercalation complementary methods ====================

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

  /**
   * Faz o merge dos registros dos arquivos de entrada no arquivo de saída
   * enquanto há registros nos segmento.
   * 
   * @param toMerge os segmentos ordenados que devem sofrer merge
   * @param toWrite o RandomAccessFile que manipula o arquivo onde os registros
   *                extraídos dos segmentos ordenados devem ser impressos.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  private void mergeAndWrite(SortedSegment[] toMerge, RAF toWrite) throws IOException {
    SortedSegment smallest = SortedSegment.getSmallest(toMerge);

    // Merge the sorted segments and write to output
    while (smallest != null) {
      toWrite.write(smallest.getFirstRegister().toByteArray());
      smallest.loadNextRegister();
      smallest = SortedSegment.getSmallest(toMerge);
    }
  }

  /**
   * Faz o merge dos registros dos arquivos de entrada no arquivo de saída
   * enquanto não é lido um registro menor do que o anterior. Para evitar que o
   * registro menor do que o anterior tenha que ser recuperado duas vezes do
   * arquivo de saída, ele é lido apenas uma vez e inicializa outro segmento
   * ordenado, que deve ser utilizado na próxima iteração da intercalação.
   * 
   * @param toMerge os segmentos ordenados que devem sofrer merge
   * @param toWrite o RandomAccessFile que manipula o arquivo onde os registros
   *                extraídos dos segmentos ordenados devem ser impressos.
   * @return Array de segmentos ordenados que devem ser utilizados na próxima
   *         iteração da intercalação.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  private SortedSegment[] mergeVariableSize(SortedSegment[] toMerge, RAF toWrite) throws IOException {
    ArrayList<SortedSegment> sortedSegments = new ArrayList<>();

    SortedSegment smallest = SortedSegment.getSmallest(toMerge);
    while (smallest != null) {
      toWrite.write(smallest.getFirstRegister().toByteArray());

      SortedSegment remaining = smallest.loadNextIfBigger();
      if (remaining != null) {
        sortedSegments.add(remaining);
      }
      smallest = SortedSegment.getSmallest(toMerge);
    }

    return sortedSegments.toArray(new SortedSegment[0]);
  }

  /**
   * Indica se existe pelo menos um arquivo de entrada com registros para ler.
   * 
   * @param inputFiles RandomAccesFile onde ocorrerá a verificação.
   * @return True se houver algum inputFile possível de ler, false do contrário.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  private boolean checkStillReadable(RAF[] inputFiles) throws IOException {
    for (RAF inputFile : inputFiles) {
      if (inputFile.canRead()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Transforma os arquivos de saída em arquivos de entrada, se tiverem algum
   * registro escrito, para intercalar novamente, já que todos os arquivos de
   * entrada serão apagados. Além disso, os RandomAccessFile abertos serão
   * fechados e os outputFiles que só possuem cabeçalho serão apagados.
   * 
   * @param inputFiles
   * @param inputRAF
   * @param outputFiles
   * @param outputRAF
   * @return Array de novos arrays de entrada.
   * @throws IOException Erro na manipulação dos arquivos.
   */
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

  /**
   * Fecha os RandomAccessFile e apaga todos os arquivos, exceto outputRAF[0], que
   * será renomeado para se tornar o novo arquivo principal.
   * 
   * @param inputFiles
   * @param inputRAF
   * @param outputFiles
   * @param outputRAF
   * @throws IOException Erro na manipulação dos arquivos.
   */
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

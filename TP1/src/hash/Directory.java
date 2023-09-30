package hash;

import main.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Armazena a profundidade e a posição do bucket que referencia.
 */
public class Directory {
  private int depth;
  private long bucketPosition;

  /**
   * Construtor do diretório, seta densidade e bucketPosition como 0;
   */
  public Directory() {
    this(0, 0);
  }

  public Directory(int depth, long bucketPosition) {
    this.depth = depth;
    this.bucketPosition = bucketPosition;
  }

  /**
   * Indica o tamanho de um registro do tipo diretório em um arquivo binário.
   * 
   * @return Número de bytes ocupados por um diretório.
   */
  public static int sizeof() {
    return Integer.BYTES + Long.BYTES;
  }

  /**
   * Substitui os atributos do diretório pelos que lê em um array de bytes.
   * 
   * @param byteArray deve conter as informações a serem importadas pelo jogador,
   *                  na seguinte ordem: profundidade (int),
   *                  (long) posição apontada no arquivo de buckets.
   * @throws IOException Erro na leitura dos bytes
   */
  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    depth = dis.readInt();
    bucketPosition = dis.readLong();

    dis.close();
  }

  /**
   * Substitui os atributos do diretório pelos que lê diretamente de um arquivo
   * binário de diretórios.
   * 
   * @param inputFile                Arquivo binário de diretórios.
   * @param registerStartingPosition Posição de onde deve começar a ler.
   * @return True se for possível ler do arquivo, false do contrário.
   * @throws IOException Erro na leitura.
   */
  public boolean fromFile(File inputFile, long registerStartingPosition) throws IOException {
    if (!inputFile.exists()) {
      return false;
    }

    RAF randomAccesFile = new RAF(inputFile, "r");
    randomAccesFile.seek(registerStartingPosition);

    byte[] bytes = new byte[Directory.sizeof()];
    randomAccesFile.read(bytes);
    fromByteArray(bytes);

    randomAccesFile.close();
    return true;
  }

  /**
   * 
   * Transforma o índice em array de bytes.
   * 
   * @return byte[] conténdo os dados do índice na seguinte ordem: profundidade
   *         (int), (long) posição apontada no arquivo de buckets.
   * @throws IOException Erro na escrita dos bytes.
   */
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(depth);
    dos.writeLong(bucketPosition);

    dos.close();
    return baos.toByteArray();
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public long getBucketPosition() {
    return bucketPosition;
  }

  public void setBucketPosition(long bucketPosition) {
    this.bucketPosition = bucketPosition;
  }

  @Override
  public String toString() {
    return "Directory depth=" + depth + ", bucketPosition=" + bucketPosition;
  }
}

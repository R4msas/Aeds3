package hash;

import main.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Estrutura que armazena uma quantidade definida de índices.
 */
public class Bucket {
  private int size;
  private LinkedList<Index> indexList;

  /**
   * Construtor padrão da classe bucket, cria lista encadeada vazia e seta a
   * capacidade para a informada.
   * 
   * @param size quantos índices a lista encadeada pode conter.
   */
  public Bucket(int size) {
    this.indexList = new LinkedList<>();
    this.size = size;
  }

  /**
   * Indica o tamanho de um bucket no arquivo binário a depender de quantos
   * índices consegue armazenar.
   * 
   * @param listSize capacidade do bucket.
   * @return A quantidade de bytes ocupada pelo bucket no arquivo binário.
   */
  public static int sizeof(int listSize) {
    return listSize * Index.sizeof();
  }

  @Override
  public String toString() {
    String listToString = "";

    for (Index index : indexList) {
      listToString += index.toString() + "\n";
    }
    if (listToString.length() > 0)
      listToString = listToString.substring(0, listToString.length() - 1); // Remove último \n

    return "Bucket {\nsize =" + size + "\nnumIndexes =" + indexList.size() + "\nindexList = [\n" + listToString
        + "]\n}";
  }

  /**
   * Insere índices em forma de array de bytes na lista encadeada. Ignora índices
   * lápide.
   * 
   * @param byteArray deve conter os bytes para ler n índices, sendo n a
   *                  capacidade do bucket.
   * @throws IOException Erro na leitura dos bytes
   */
  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    for (int i = 0; i < size; i++) {
      Index currentIndex = new Index();
      currentIndex.fromByteArray(dis.readNBytes(Index.sizeof()));
      if (!currentIndex.isTombstone()) {
        insert(currentIndex);
      }
    }
  }

  /**
   * Insere índices que lê em do arquivo de índices, ignorando lápides.
   * 
   * @param inputFile                arquivo binário de índices
   * @param registerStartingPosition posição do registro que se quer ler
   * @return Verdadeiro se o arquivo de leitura existe, falso do contrário.
   * @throws IOException Erro de leitura.
   */
  public boolean fromFile(File inputFile, long registerStartingPosition) throws IOException {
    if (!inputFile.exists()) {
      return false;
    }

    RAF randomAccessFile = new RAF(inputFile, "r");
    randomAccessFile.seek(registerStartingPosition);

    fromFile(randomAccessFile);
    randomAccessFile.close();
    return true;
  }

  public void fromFile(RAF inputFile) throws IOException {
    byte[] bytes = new byte[Bucket.sizeof(size)];
    inputFile.read(bytes);
    fromByteArray(bytes);
  }

  /**
   * Transforma o índice em array de bytes e, se o número de índices armazenados
   * for menor do que a capacidade do bucket, preenche o array com índices lápide.
   * 
   * @return byte[] contendo os índices armazenados.
   * @throws IOException Erro na escrita dos bytes.
   */
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    for (int i = 0; i < indexList.size(); i++) {
      dos.write(indexList.get(i).toByteArray());
    }

    // Fills with tombstones if indexList < size
    for (int i = indexList.size(); i < size; i++) {
      dos.write(new Index().toByteArray());
    }

    dos.close();
    return baos.toByteArray();
  }

  /**
   * Insere um índice na lista se ela não estiver cheia.
   * 
   * @param index índice que se deseja inserir.
   * @return True se foi possível inserir, falso do contrário.
   */
  public boolean insert(Index index) {
    if (isNotFull()) {
      indexList.add(index);
      return true;
    }

    return false;
  }

  /**
   * Procura um id na lista de índices.
   * 
   * @param indexId id do índice que se deseja encontrar.
   * @return O índice se encontrar, null do contrário.
   */
  public Index get(int indexId) {
    int position = seek(indexId);
    return position < 0 ? null : indexList.get(position);
  }

  /**
   * Encontra a posição de um id se estiver presente na lista.
   * 
   * @param id id do índice que se deseja encontrar.
   * @return A posição do índice se encontrar, -1 do contrário.
   */
  public int seek(int id) {
    for (int i = 0; i < indexList.size(); i++) {
      if (indexList.get(i).getId() == id) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Atualiza um índice na lista se ele estiver contido nela.
   * 
   * @param index índice que se quer atualizar
   * @return True se índice encontrado e atualizado, false do contrário.
   */
  public boolean update(Index index) {
    int position = seek(index.getId());
    if (position < 0) {
      return false;
    }

    indexList.set(position, index);
    return true;
  }

  /**
   * Remove um índice na lista se ele estiver contido nela.
   * 
   * @param index índice que se quer atualizar
   * @return True se índice encontrado e removido, false do contrário.
   */
  public boolean delete(int id) {
    int position = seek(id);
    if (position < 0) {
      return false;
    }

    indexList.remove(position);
    return true;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public LinkedList<Index> getIndexList() {
    return indexList;
  }

  public void setIndexList(LinkedList<Index> indexList) {
    this.indexList = indexList;
  }

  public boolean isFull() {
    return indexList.size() == size;
  }

  public boolean isNotFull() {
    return indexList.size() < size;
  }
}

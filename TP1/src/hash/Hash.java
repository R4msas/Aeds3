package hash;

import model.*;
import main.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Hash implements Indexacao {
  private int depth;
  private File directoryFile;
  private File bucketFile;
  private int bucketSize;

  /**
   * <p>
   * Construtor da classe Hash. Ao ser chamado, pode criar os arquivos de bucket e
   * diretório ou tentar aproveitar os que já existem. Os arquivos que irá acessar
   * se terão nome directory.db e bucket.db.
   * </p>
   * <p>
   * Se reaproveitar os arquivos existentes, a profundidade do hash e o tamanho do
   * bucket serão substituídos pelos valores obtidos desses arquivos.
   * </p>
   * 
   * @param depth       profundidade do hash.
   * @param path        caminho onde os arquivos directory.db e bucket.db
   *                    estão/ficarão.
   * @param bucketSize  capacidade do bucket.
   * @param replaceFile se deve recriar os arquivos ou se deve tentar
   *                    reaproveitá-los,caso existam.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  public Hash(int depth, String path, int bucketSize, boolean replaceFile) throws IOException {
    this(depth, new File(path + "directory.db"), new File(path + "bucket.db"), bucketSize, replaceFile);
  }

  /**
   * <p>
   * Construtor da classe Hash. Ao ser chamado, pode criar os arquivos de bucket e
   * diretório ou tentar aproveitar os que já existem.
   * </p>
   * <p>
   * Se reaproveitar os arquivos existentes, a profundidade do hash e o tamanho do
   * bucket serão substituídos pelos valores obtidos desses arquivos.
   * </p>
   * 
   * @param depth         profundidade do hash.
   * @param directoryFile arquivo de diretórios.
   * @param bucketFile    arquivo de buckets.
   * @param bucketSize    capacidade do bucket.
   * @param replaceFile   se deve recriar os arquivos ou se deve tentar
   *                      reaproveitá-los,caso existam.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  public Hash(int depth, File directoryFile, File bucketFile, int bucketSize, boolean replaceFile)
      throws IOException {
    this.directoryFile = directoryFile;
    this.bucketFile = bucketFile;
    createFiles(depth, directoryFile, bucketFile, bucketSize, replaceFile);

  }

  /**
   * Insere um registro nos arquivos de índice
   * 
   * @param register registro a inserir.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  public void insert(PlayerRegister register) throws IOException {
    insert(new Index(register));
  }

  @Override
  public void insert(Index indexToInsert) throws IOException {
    // Get directory
    long directoryPosition = getDirectoryPosition(indexToInsert.getId());
    Directory directory = new Directory();
    directory.fromFile(directoryFile, directoryPosition);

    // Get bucket
    Bucket bucket = new Bucket(bucketSize);
    long bucketPosition = directory.getBucketPosition();
    bucket.fromFile(bucketFile, directory.getBucketPosition());

    if (bucket.isNotFull()) {
      bucket.insert(indexToInsert);

      RAF randomAccessFile = new RAF(bucketFile, "rw");
      randomAccessFile.seek(bucketPosition);

      randomAccessFile.write(bucket.toByteArray());
      randomAccessFile.close();
    } else {
      eraseOldBucket(bucketPosition);
      long newBucketPosition = appendBucket(new Bucket(bucketSize));

      Directory newDirectory = new Directory(directory.getDepth() + 1, newBucketPosition);
      if (this.depth == directory.getDepth()) {
        duplicateDirectories(newDirectory, directoryPosition);
        ++depth;
      } else {
        updateDirectory(newDirectory, directoryPosition);
      }

      LinkedList<Index> indexes = bucket.getIndexList();
      for (Index index : indexes) {
        insert(index);
      }

      insert(indexToInsert);
    }
  }

  @Override
  public Index read(int id) throws IOException {
    Directory directory = new Directory();
    directory.fromFile(directoryFile, getDirectoryPosition(id));

    Bucket bucket = new Bucket(this.bucketSize);
    bucket.fromFile(bucketFile, directory.getBucketPosition());

    return bucket.get(id);
  }

  /**
   * Cria um índice para um registro o atualiza no hash.
   * 
   * @param register registro a se atualizar
   * @return True se for possível realizar a atualização, False do contrário.
   * @throws IOException Erro de manipulação do arquivo.
   */
  public boolean update(PlayerRegister register) throws IOException {
    return update(new Index(register));
  }

  @Override
  public boolean update(Index index) throws IOException {
    Directory directory = new Directory();
    directory.fromFile(directoryFile, getDirectoryPosition(index.getId()));

    Bucket bucket = new Bucket(this.bucketSize);
    bucket.fromFile(bucketFile, directory.getBucketPosition());

    if (bucket.update(index)) {
      RAF randomAccessFile = new RAF(bucketFile, "rw");
      randomAccessFile.seek(directory.getBucketPosition());

      randomAccessFile.write(bucket.toByteArray());
      randomAccessFile.close();
      return true;
    }

    return false;
  }

  @Override
  public boolean delete(int id) throws IOException {
    Directory directory = new Directory();
    directory.fromFile(directoryFile, getDirectoryPosition(id));

    Bucket bucket = new Bucket(this.bucketSize);
    bucket.fromFile(bucketFile, directory.getBucketPosition());

    if (bucket.delete(id)) {
      RAF randomAccessFile = new RAF(bucketFile, "rw");
      randomAccessFile.seek(directory.getBucketPosition());

      randomAccessFile.write(bucket.toByteArray());
      randomAccessFile.close();
      return true;
    }

    return false;
  }

  /**
   * Cria os arquivos de índice ou os utiliza para atualizar os valores do objeto.
   * <p>
   * Se reaproveitar os arquivos existentes, a profundidade do hash e o tamanho do
   * bucket serão substituídos pelos valores obtidos desses arquivos.
   * </p>
   * 
   * @param depth         profundidade do hash.
   * @param directoryFile arquivo de diretórios.
   * @param bucketFile    arquivo de buckets.
   * @param bucketSize    capacidade do bucket.
   * @param replaceFile   se deve recriar os arquivos ou se deve tentar
   *                      reaproveitá-los,caso existam.
   * @throws IOException Erro na manipulação dos arquivos.
   *
   */
  public void createFiles(int depth, File directoryFile, File bucketFile, int bucketSize, boolean replaceFile)
      throws IOException {
    boolean bucketAlreadyExists = bucketFile.exists();
    if (bucketAlreadyExists && replaceFile) {
      this.bucketFile.delete();
    }

    long bucketPosition[] = new long[(int) Math.pow(2, depth)];
    RAF randomAccessFile = new RAF(bucketFile, "rw");

    // New Bucket File Config
    if (bucketAlreadyExists && !replaceFile) {
      this.bucketSize = randomAccessFile.readInt();
    } else {
      this.bucketSize = bucketSize;
      randomAccessFile.writeInt(this.bucketSize);

      for (int i = 0; i < bucketPosition.length; i++) {
        bucketPosition[i] = randomAccessFile.getFilePointer();
        randomAccessFile.write(new Bucket(this.bucketSize).toByteArray());
      }
    }
    randomAccessFile.close();

    if ((directoryFile.exists() && replaceFile) || !bucketAlreadyExists) {
      this.directoryFile.delete();
    }

    // Directory File Config
    randomAccessFile = new RAF(directoryFile, "rw");
    if (bucketAlreadyExists && directoryFile.exists() && !replaceFile) {
      this.depth = randomAccessFile.readInt();
    } else {
      this.depth = depth;
      randomAccessFile.writeInt(depth);

      for (long l : bucketPosition) {
        randomAccessFile.write(new Directory(this.depth, l).toByteArray());
      }
    }
    randomAccessFile.close();
  }

  public File getBucketFile() {
    return bucketFile;
  }

  public void setBucketFile(File bucketFile) {
    this.bucketFile = bucketFile;
  }

  /**
   * Utiliza de um hash para calcular a posição do diretório que apontaria para um
   * bucket onde um id estaria armazenado.
   * 
   * @param id que quer-se saber a posição do diretório.
   * @return Posição do diretório.
   */
  private long getDirectoryPosition(int id) {
    long position = Integer.BYTES; // Pula cabeçalho
    int hash = id % (int) Math.pow(2, depth);

    position = position + hash * Directory.sizeof();
    return position;
  }

  public File getDirectoryFile() {
    return directoryFile;
  }

  public void setDirectoryFile(File directoryFile) {
    this.directoryFile = directoryFile;
  }

  public int getBucketSize() {
    return bucketSize;
  }

  public void setBucketSize(int bucketSize) {
    this.bucketSize = bucketSize;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  // Insert register complementary methods

  /**
   * Substitui o bucket especificado por outro de mesmo tamanho mas apenas com
   * índices lápide.
   * 
   * @param bucketPosition Posição do bucket que vai ser apagado.
   * @throws IOException Erro na manipulação do arquivo.
   */
  private void eraseOldBucket(long bucketPosition) throws IOException {
    RAF randomAccessFile = new RAF(bucketFile, "rw");
    randomAccessFile.seek(bucketPosition);
    randomAccessFile.write(new Bucket(bucketSize).toByteArray());
    randomAccessFile.close();
  }

  /**
   * Cria um bucet ao final do arquivo de buckets.
   * 
   * @param bucket que será inserido.
   * @return Postição em que o bucket foi inserido.
   * @throws IOException Erro de escrita no arquivo.
   */
  private long appendBucket(Bucket bucket) throws IOException {
    RAF randomAcessFile = new RAF(bucketFile, "rw");
    randomAcessFile.movePointerToEnd();

    long bucketPosition = randomAcessFile.getFilePointer();
    randomAcessFile.write(bucket.toByteArray());
    randomAcessFile.close();

    return bucketPosition;
  }

  /**
   * Duplica a quantidade existente de diretórios, fazendo cópias dos diretórios
   * existentes ao final do arquvio de diretórios. O diretório na posição
   * especificada vai ter apenas sua profundidade atualizada, já que o diretório
   * que faz referência ao bucket sofreu append será inserido na posição em que
   * sua cópia seria.
   * 
   * @param include           Diretório que faz referência ao bucket que sofreu
   *                          append.
   * @param directoryPosition Posição do diretório que não será copiado.
   * @throws IOException Erro na manipulação do arquivo.
   */
  private void duplicateDirectories(Directory include, long directoryPosition) throws IOException {
    RAF readRaf = new RAF(directoryFile, "rw");
    readRaf.seek(Integer.BYTES);

    RAF writeRaf = new RAF(directoryFile, "rw");
    writeRaf.writeInt(depth + 1);
    writeRaf.movePointerToEnd();

    int numDiretories = (int) Math.pow(2, depth);
    for (int i = 0; i < numDiretories; i++) {

      // Get currentDirectory
      Directory currentDirectory = new Directory();
      currentDirectory.fromFile(directoryFile, readRaf.getFilePointer());

      if (readRaf.getFilePointer() == directoryPosition) {
        currentDirectory.setDepth(depth + 1);

        readRaf.write(currentDirectory.toByteArray());
        writeRaf.write(include.toByteArray());
      } else {
        byte[] bytes = currentDirectory.toByteArray();

        writeRaf.write(bytes);
        readRaf.seek(readRaf.getFilePointer() + bytes.length);
      }
    }

    readRaf.close();
    writeRaf.close();
  }

  private void updateDirectory(Directory toUpdate, long directoryPosition) throws IOException {
    RAF randomAccessFile = new RAF(directoryFile, "rw");
    randomAccessFile.seek(directoryPosition);
    randomAccessFile.write(toUpdate.toByteArray());
    randomAccessFile.close();
  }

}

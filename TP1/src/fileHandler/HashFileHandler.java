package fileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dao.IndexDAO;
import indexacao.hash.Bucket;
import indexacao.hash.Directory;
import indexacao.hash.Hash;
import main.RAF;

/**
 * Extende IndexFileHandler para, além de criar os arquivos de índice .db, poder
 * ler todo o seu conteúdo quando a indexação for feita por meio de hash.
 */
public class HashFileHandler extends IndexFileHandler {

  /**
   * Construtor da classe IndexFileHandler.
   * 
   * @param biggestID  será o cabeçalho do arquivo .db. É alterado pelas funções
   *                   de leitura pelo maior cabeçalho lido.
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   * @param hash       estrutura que indexará os registros.
   */
  public HashFileHandler(int biggestID, String dbFilePath, Hash hash) {
    super(biggestID, dbFilePath, hash);
  }

  /**
   * Construtor da classe IndexFileHandler. Seta o atributo maior id para -1.
   * 
   * @param dbFilePath diretório e nome do arquivo .db onde registros serão
   *                   lidos/criados.
   * @param hash       estrutura que indexará os registros.
   */
  public HashFileHandler(String dbFilePath, Hash hash) {
    this(-1, dbFilePath, hash);
  }

  /**
   * Construtor que converte um objeto DBHandler em um HashFileHandler.
   * 
   * @param dbHandler objeto antigo que deve ser convertido.
   * @param hash      estrutura que indexará os registros.
   */
  public HashFileHandler(DBHandler dbHandler, Hash hash) {
    this(dbHandler.biggestID, dbHandler.dbFilePath, hash);
  }

  /**
   * Indexa e constrói o arquivo binário de índices dos registros lidos em um
   * arquivo .db utilizando Hash.
   * <p>
   * <strong>Importante:</strong> se os arquivo de indexação .db já existirem,
   * serão apagados e substituídos por novos.
   * </p>
   * 
   * @return Objeto DAO que permite alterar o arquivo criado.
   * @throws IOException Erro na manipulação dos arquivos.
   */
  @Override
  public IndexDAO buildIndexFromDB() throws IOException {
    Hash hash = (Hash) indexacao;
    hash.createFiles(hash.getDepth(), hash.getDirectoryFile(), hash.getBucketFile(), hash.getBucketSize(), true);
    return super.buildIndexFromDB();
  }

  /**
   * Lê todos os diretórios criados pela indexação.
   * 
   * @return Array de diretórios.
   * @throws IOException Erro na leitura do arquivo de índices.
   */
  public Directory[] readDirectories() throws IOException {
    File directoryFile = ((Hash) indexacao).getDirectoryFile();

    ArrayList<Directory> directories = new ArrayList<>();

    // Percorre o arquivo adicionando cada diretório à lista.
    for (long nextPosition = 4; nextPosition < directoryFile.length(); nextPosition += Directory.sizeof()) {
      Directory currentDirectory = new Directory();
      currentDirectory.fromFile(directoryFile, nextPosition);

      directories.add(currentDirectory);
    }

    return directories.toArray(new Directory[0]);
  }

  /**
   * Lê todos os buckets criados pela indexação.
   * 
   * @return Array de buckets.
   * @throws IOException Erro na leitura do arquivo de índices.
   */
  public Bucket[] readBuckets() throws IOException {
    Hash hash = (Hash) indexacao;
    RAF randomAccessFile = new RAF(hash.getBucketFile(), "r");

    int bucketSize = randomAccessFile.readInt();

    ArrayList<Bucket> buckets = new ArrayList<>();
    while (randomAccessFile.canRead()) {
      Bucket currentBucket = new Bucket(bucketSize);
      currentBucket.fromFile(randomAccessFile);

      buckets.add(currentBucket);
    }

    randomAccessFile.close();
    return buckets.toArray(new Bucket[0]);
  }
}

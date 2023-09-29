package fileHandler;

import java.io.IOException;
import java.util.ArrayList;

import hash.Bucket;
import hash.Directory;
import hash.Hash;
import main.RAF;

public class HashFileHandler extends IndexFileHandler {
  public HashFileHandler(int biggestID, String dbFilePath, Hash indexacao) {
    super(biggestID, dbFilePath, indexacao);
  }

  public HashFileHandler(String dbFilePath, Hash indexacao) {
    this(-1, dbFilePath, indexacao);
  }

  public HashFileHandler(DBHandler dbHandler, Hash indexacao) {
    this(dbHandler.biggestID, dbHandler.dbFilePath, indexacao);
  }

  public Directory[] readDirectories() throws IOException {
    Hash hash = (Hash) indexacao;
    RAF randomAccessFile = new RAF(hash.getDirectoryFile(), "r");

    randomAccessFile.seek(Integer.BYTES); // Pula cabeçalho

    ArrayList<Directory> directories = new ArrayList<>();
    while (randomAccessFile.canRead()) {
      Directory currentDirectory = new Directory();
      currentDirectory.fromFile(hash.getDirectoryFile(), randomAccessFile.getFilePointer());
      directories.add(currentDirectory);

      randomAccessFile.skipBytes(Directory.sizeof());
    }

    randomAccessFile.close();
    return directories.toArray(new Directory[0]);
  }

  public Bucket[] readBuckets() throws IOException {
    Hash hash = (Hash) indexacao;
    RAF randomAccessFile = new RAF(hash.getBucketFile(), "r");

    int bucketSize = randomAccessFile.readInt();

    ArrayList<Bucket> buckets = new ArrayList<>();
    while (randomAccessFile.canRead()) {
      Bucket currentBucket = new Bucket(bucketSize);
      currentBucket.fromFile(hash.getBucketFile(), randomAccessFile.getFilePointer());
      buckets.add(currentBucket);

      randomAccessFile.skipBytes(Bucket.sizeof(bucketSize));
    }

    randomAccessFile.close();
    return buckets.toArray(new Bucket[0]);
  }

}

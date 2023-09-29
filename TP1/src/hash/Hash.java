package hash;

import model.*;
import main.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class Hash implements Indexacao {
  private int depth;
  private File directoryFile;
  private File bucketFile;
  private int bucketSize;

  public Hash(int depth, String path, int bucketSize, boolean replaceFile) throws IOException {
    this(depth, new File(path + "directory.db"), new File(path + "bucket.db"), bucketSize, replaceFile);
  }

  public Hash(int depth, File directoryFile, File bucketFile, int bucketSize, boolean replaceFile)
      throws IOException {
    this.directoryFile = directoryFile;
    this.bucketFile = bucketFile;
    createFiles(depth, directoryFile, bucketFile, bucketSize, replaceFile);

  }

  private int hash(int id) {
    return id % (int) Math.pow(2, depth);
  }

  public void insert(PlayerRegister register) throws IOException {
    insert(new Index(register));
  }

  @Override
  public void insert(Index indexToInsert) throws IOException {
    // Get directory
    Directory directory = new Directory();
    long directoryPosition = getDirectoryPosition(indexToInsert.getId());
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

  public long getDirectoryPosition(int id) {
    return hash(id) * Directory.sizeof() + Integer.BYTES;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  // Insert register complementary methods

  private void eraseOldBucket(long buketToErasePosition) throws IOException {
    RAF randomAccessFile = new RAF(bucketFile, "rw");
    randomAccessFile.seek(buketToErasePosition);
    randomAccessFile.write(new Bucket(bucketSize).toByteArray());
    randomAccessFile.close();
  }

  private long appendBucket(Bucket bucket) throws IOException {
    RAF randomAcessFile = new RAF(bucketFile, "rw");
    randomAcessFile.movePointerToEnd();

    long bucketPosition = randomAcessFile.getFilePointer();
    randomAcessFile.write(bucket.toByteArray());
    randomAcessFile.close();

    return bucketPosition;
  }

  private void duplicateDirectories(Directory include, long position) throws IOException {
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

      if (readRaf.getFilePointer() == position) {
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

  private void updateDirectory(Directory toUpdate, long position) throws IOException {
    RAF randomAccessFile = new RAF(directoryFile, "rw");
    randomAccessFile.seek(position);
    randomAccessFile.write(toUpdate.toByteArray());
    randomAccessFile.close();
  }

}

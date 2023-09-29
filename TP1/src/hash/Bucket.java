package hash;

import main.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Bucket {
  private int size;
  private LinkedList<Index> indexList;

  private Bucket() {
    size = 0;
    this.indexList = new LinkedList<>();
  }

  public Bucket(int size) {
    this();
    this.size = size;
  }

  public static int sizeof(int listSize) {
    return listSize * Index.sizeof();
  }

  @Override
  public String toString() {
    String listToString = "";

    for (Index index : indexList) {
      listToString += index.toString() + "\n";
    }
    listToString = listToString.substring(0, listToString.length() - 1); // Remove último \n

    return "Bucket {\nsize =" + size + "\nnumIndexes =" + indexList.size() + "\nindexList = [\n" + listToString
        + "]\n}";
  }

  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    for (int i = 0; i < size; i++) {
      Index currentIndex = new Index();
      currentIndex.fromByteArray(dis.readNBytes(Index.sizeof()));
      if (!currentIndex.isTombstone()) {
        indexList.add(currentIndex);
      }
    }
  }

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

  public boolean insert(Index index) {
    if (indexList.size() < size) {
      indexList.add(index);
      return true;
    }

    return false;
  }

  public Index get(int indexId) {
    int position = seek(indexId);
    return position < 0 ? null : indexList.get(position);
  }

  public int seek(int id) {
    for (int i = 0; i < indexList.size(); i++) {
      if (indexList.get(i).getId() == id) {
        return i;
      }
    }

    return -1;
  }

  public boolean update(Index index) {
    int position = seek(index.getId());
    if (position < 0) {
      return false;
    }

    indexList.set(position, index);
    return true;
  }

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

  public boolean isEmpty() {
    return indexList.size() == 0;
  }
}

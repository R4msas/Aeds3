package sort;

import java.io.File;
import java.io.IOException;

import main.RAF;
import model.Player;
import model.PlayerRegister;

public class PlayerHeap {
  private Node[] nodes;
  private int header;

  public PlayerHeap(int size) {
    nodes = new Node[size];
  }

  public File[] buildTemporaryFiles(String mainFileName, String mainFilePath, int numberFiles) throws IOException {
    RAF mainFile = new RAF(mainFilePath + mainFileName, "r");
    header = mainFile.readInt();
    fill(mainFile);

    File[] temporaryFiles = new File[numberFiles];
    RAF[] temporaryRafs = new RAF[numberFiles];
    for (int i = 0; i < temporaryFiles.length; i++) {
      temporaryFiles[i] = new File(mainFilePath + "tmp" + i + ".db");
      temporaryRafs[i] = new RAF(temporaryFiles[i], "rw");
      temporaryRafs[i].writeInt(header);
    }

    while (mainFile.canRead()) {
      PlayerRegister currentRegister = new PlayerRegister();
      if (currentRegister.fromFile(mainFile, true) != null) {
        Node previousFirstNode = insertRegister(currentRegister);
        writeNode(previousFirstNode, temporaryRafs);
      }
    }

    // Write Remaining Nodes
    emptyAndWrite(temporaryRafs);

    mainFile.close();
    for (RAF randomAccessFile : temporaryRafs) {
      randomAccessFile.close();
    }

    return temporaryFiles;
  }

  private void fill(RAF randomAccessFile) throws IOException {
    for (int i = 0; i < nodes.length; i++) {
      PlayerRegister register = new PlayerRegister();
      if (register.fromFile(randomAccessFile, true) != null) {
        nodes[i] = new Node(register, 0);
        balanceFatherOf(i);
      }
    }
  }

  private Node insertRegister(PlayerRegister register) throws IOException {
    Node firstNode = nodes[0];
    Node nodeToInsert = new Node(register, firstNode.sortedSegmentIndex);

    if (!nodeToInsert.isBiggerThan(firstNode)) {
      ++nodeToInsert.sortedSegmentIndex;
    }

    nodes[0] = nodeToInsert;
    balanceChildrenOf(0);
    return firstNode;
  }

  private Node insertNode(Node node) throws IOException {
    Node firstNode = nodes[0];
    nodes[0] = node;
    balanceChildrenOf(0);
    return firstNode;
  }

  private void balanceFatherOf(int index) {
    int fatherIndex = (index - 1) / 2;
    Node father = nodes[fatherIndex];
    if (index == 0 || nodes[index] == null || !father.isBiggerThan(nodes[index])) {
      return;
    }

    swap(fatherIndex, index);
    balanceFatherOf(fatherIndex);
  }

  private void balanceChildrenOf(int index) {
    boolean cantHaveChilds = index >= nodes.length / 2;
    if (cantHaveChilds || nodes[index] == null) {
      return;
    }

    int newPosition = getChildIfSmallerThan(nodes[index], index);
    if (newPosition != -1) {
      swap(index, newPosition);
      balanceChildrenOf(newPosition);
    }
  }

  private int getChildIfSmallerThan(Node father, int fatherIndex) {
    int leftChildIndex = fatherIndex * 2 + 1;
    Node leftNode = nodes[leftChildIndex];

    int rightChildIndex = leftChildIndex + 1;
    if (rightChildIndex < nodes.length) {
      Node rightNode = nodes[rightChildIndex];

      if (leftNode != null && father.isBiggerThan(leftNode)
          && (rightNode == null || rightNode.isBiggerThan(leftNode))) {
        return leftChildIndex;
      } else if (rightNode != null && father.isBiggerThan(rightNode)
          && (leftNode == null || leftNode.isBiggerThan(rightNode))) {
        return rightChildIndex;
      }
    } else if (leftNode != null && father.isBiggerThan(leftNode)) {
      return leftChildIndex;
    }

    return -1;
  }

  private void swap(int originalNode, int newNode) {
    Node tmp = nodes[originalNode];
    nodes[originalNode] = nodes[newNode];
    nodes[newNode] = tmp;
  }

  private void writeNode(Node nodeToWrite, RAF[] temporaryRafs) throws IOException {
    int index = nodeToWrite.sortedSegmentIndex;
    RAF rafToWrite = temporaryRafs[index % temporaryRafs.length];
    rafToWrite.write(nodeToWrite.register.toByteArray());
  }

  private void emptyAndWrite(RAF[] temporaryRafs) throws IOException {
    for (int i = 0; i < nodes.length; i++) {
      Player emptyPlayer = new Player("", new String[0], i * -1, 0, "", 0);
      Node emptyNode = new Node(new PlayerRegister(true, emptyPlayer), Integer.MAX_VALUE);

      Node previousFirstNode = insertNode(emptyNode);
      writeNode(previousFirstNode, temporaryRafs);
    }
    this.nodes = new Node[nodes.length];
  }

}

class Node {
  public int sortedSegmentIndex;
  public PlayerRegister register;

  public Node(PlayerRegister register, int sortedSegmentIndex) {
    this.sortedSegmentIndex = sortedSegmentIndex;
    this.register = register;
  }

  public boolean isBiggerThan(Node that) {
    if (this.sortedSegmentIndex > that.sortedSegmentIndex) {
      return true;
    } else if (this.sortedSegmentIndex == that.sortedSegmentIndex && this.register.isBiggerThan(that.register)) {
      return true;
    }

    return false;
  }
}

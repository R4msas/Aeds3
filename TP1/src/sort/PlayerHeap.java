package sort;

import java.io.File;
import java.io.IOException;

import main.RAF;
import model.PlayerRegister;

/**
 * Faz a distribuição de jogadores utilizando um heap de mínimo.
 */
public class PlayerHeap {
  private Node[] nodes;
  private int header; // Biggest ID

  /**
   * Construtor da classe PlayerHeap
   * 
   * @param size quantos nós o heap terá.
   */
  public PlayerHeap(int size) {
    nodes = new Node[size];
  }

  /**
   * Distribuição propriamente dita
   * 
   * @param mainFileName
   * @param mainFilePath
   * @param numberFiles  em quantos arquivos os registros devem ser distribuídos.
   * @return Array de arquivos temporários
   * @throws IOException Erro de manipulação dos arquivos.
   */
  public File[] buildTemporaryFiles(String mainFileName, String mainFilePath, int numberFiles) throws IOException {
    RAF mainFile = new RAF(mainFilePath + mainFileName, "r");
    header = mainFile.readInt();
    fill(mainFile);

    // Cria arquivos temporários
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

  /**
   * Enche o heap com os registros do arquivo principal e faz balanceamento para
   * garantir que o menor sempre esteja na raiz.
   * 
   * @param randomAccessFile RAF que acessa o arquivo principal, de onde os
   *                         registros serão lidos.
   * @throws IOException Erro de manipulação dos arquivos.
   */
  private void fill(RAF randomAccessFile) throws IOException {
    for (int i = 0; i < nodes.length; i++) {
      PlayerRegister register = new PlayerRegister();
      if (register.fromFile(randomAccessFile, true) != null) {
        nodes[i] = new Node(register, 0);
        balanceFatherOf(i);
      }
    }
  }

  /**
   * Verifica recursivamente se um registro é maior do que o pai. Se sim, ocorre
   * swap.
   * 
   * @param index posição do nó filho que será comparado com o pai.
   */
  private void balanceFatherOf(int index) {
    int fatherIndex = (index - 1) / 2;
    Node father = nodes[fatherIndex];
    if (index == 0 || nodes[index] == null || !father.isBiggerThan(nodes[index])) {
      return;
    }

    swap(fatherIndex, index);
    balanceFatherOf(fatherIndex);
  }

  private void swap(int originalNode, int newNode) {
    Node tmp = nodes[originalNode];
    nodes[originalNode] = nodes[newNode];
    nodes[newNode] = tmp;
  }

  /**
   * Remove a raiz, insere um registro já convertido em nó e balanceia o heap para
   * que o menor nó esteja sempre na raiz. Se o nó a ser inserido for maior do que
   * o anterior, a inserção ocorre normalmente, mas, se o nó inserido for menor do
   * que o nó removido, ele deve ter o seu índice aumentado para que "afunde" no
   * heap.
   * 
   * @param register registro a ser inserido.
   * @return Antigo nó raiz que foi removido.
   * @throws IOException Erro de manipulação do arquivo.
   */
  private Node insertRegister(PlayerRegister register) throws IOException {
    Node firstNode = nodes[0];
    Node nodeToInsert = new Node(register, firstNode.index);

    if (!nodeToInsert.isBiggerThan(firstNode)) {
      ++nodeToInsert.index;
    }

    nodes[0] = nodeToInsert;
    balanceChildrenOf(0);
    return firstNode;
  }

  /**
   * Troca recursivamente o nó pai com o menor nó filho se ele for maior do que
   * pelo menos um de seus filhos.
   * 
   * @param index posição do nó pai que se quer verificar.
   */
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

  /**
   * Encontra o menor nó filho se pelo menos um nó filho for menor do que o pai.
   * 
   * @param father
   * @param fatherIndex
   * @return Posicção do menor filho.
   */
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

  /**
   * Escreve o registro armazenado pelo nó no arquivo designado pelo resto da
   * divisão do índice pela quantidade de arquivos temporários.
   * 
   * @param nodeToWrite
   * @param temporaryRafs
   * @throws IOException
   */
  private void writeNode(Node nodeToWrite, RAF[] temporaryRafs) throws IOException {
    int index = nodeToWrite.index;
    RAF rafToWrite = temporaryRafs[index % temporaryRafs.length];
    rafToWrite.write(nodeToWrite.register.toByteArray());
  }

  /**
   * Escreve os nós remanescentes do heap criando nós vazios que afundam e os
   * fazem subir para a raiz um por um.
   * 
   * @param temporaryRafs arquivos onde os registros serão escritos
   * @throws IOException Erro de manipulação dos arquivos.
   */
  private void emptyAndWrite(RAF[] temporaryRafs) throws IOException {
    for (int i = 0; i < nodes.length; i++) {
      Node emptyNode = new Node(new PlayerRegister(), Integer.MAX_VALUE);
      Node previousFirstNode = insertNode(emptyNode);
      writeNode(previousFirstNode, temporaryRafs);
    }
    this.nodes = new Node[nodes.length];
  }

  /**
   * Insere um nó sem alterá-lo ao substituí-lo pela raiz, que é retornada.
   * 
   * @param node nó a ser inserido.
   * @return Antiga raiz.
   * @throws IOException Erro de manipulação do arquivo.
   */
  private Node insertNode(Node node) throws IOException {
    Node firstNode = nodes[0];
    nodes[0] = node;
    balanceChildrenOf(0);
    return firstNode;
  }
}

/**
 * Nó do heap, armazena o índice do arquivo que deve ser escrito no array de
 * arquivos de saída e um registro.
 */
class Node {
  public int index;
  public PlayerRegister register;

  public Node(PlayerRegister register, int index) {
    this.index = index;
    this.register = register;
  }

  /**
   * Informa se um nó é maior que o outro.
   * 
   * @param that nó que deseja comparar.
   * @return True se ou o índice é maior ou se o índice é igual e o registro é
   *         maior, false do contrário.
   */
  public boolean isBiggerThan(Node that) {
    boolean resp = (this.index > that.index || (this.index == that.index && this.register.isBiggerThan(that.register)));

    return resp;
  }
}

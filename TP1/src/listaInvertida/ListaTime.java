package listaInvertida;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import dao.IndexDAO;
import indexacao.hash.*;
import main.RAF;
import model.*;
/*
 * Esta classe cria um índice de clustering de times, cada índice secundário tem como nome do time
 * dos jogadores, dentro deste arquivo estarão os id's de cada jogador.
 */

public class ListaTime {

  private int id;
  private float rating;
  private String listaTimesExistentes = "listaTimesExistentes.txt";
  private String prefixo = "resources/indiceSecundario/time/";
  private String arqHash = "resources/db/";
  private String arqPrincipal = "resources/db/csgo_players.db";

  public ListaTime() {
  }

  public ListaTime(int id, float rating) {
    this.id = id;
    this.rating = rating;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public float getRating() {
    return rating;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }

  /**
   * cria índice de dados, caso já exista um índice, apaga todos os arquivos e
   * grava novamente.
   */
  public void insere(PlayerRegister player) throws Exception {
    String[] times = player.getPlayer().getTeams();
    String nomeTime = times[0];// o primeiro time é o time atual do jogador.
    if (PrecisaEscreverOTime(nomeTime)) {
      FileWriter arqTime = new FileWriter(prefixo + listaTimesExistentes, true);
      arqTime.write(nomeTime + ",");
      arqTime.close();
    }
    String nomeArq = prefixo + nomeTime + ".db";
    RAF indice = new RAF(nomeArq, "rw");
    indice.movePointerToEnd();
    int id = player.getPlayer().getPlayerId();
    indice.writeInt(id);
    indice.close();
  }

  /**
   * método para manter a lista de paises sem nomes duplicados, o try/catch é para
   * o primeiro
   * teste, caso o arquivo não exista
   */
  public boolean PrecisaEscreverOTime(String nomeTime) throws Exception {
    boolean resp = true;
    try {
      Scanner arqTimes = new Scanner(new File(prefixo + listaTimesExistentes));
      String strCsv = arqTimes.nextLine();
      String times[] = strCsv.split(",");

      for (int c = 0; c < times.length; c++) {
        if (times[c].equals(nomeTime)) {
          resp = false;
          break;
        }
      }
    } catch (Exception e) {
      resp = true;
    }
    return resp;
  }

  /**
   * este método lê todo o arquivo de dados e grava no arquivo id por id, gravando
   * em um arquivo
   * que tem o nome do pais do jogador.
   * 
   * @param caminhoDoArquivo
   * @throws Exception
   */

  public void criaIndiceSecundario(String caminhoDoArquivo) throws Exception {
    apagaIndice();
    RAF arquivoIndice = new RAF(caminhoDoArquivo, "r");
    arquivoIndice.skipBytes(4);// primeira parte do arquivo é
    while (arquivoIndice.canRead()) {
      PlayerRegister player = new PlayerRegister();
      player.fromFile(arquivoIndice, true);
      if (!player.isTombstone()) {
        insere(player);
      }
    }
    arquivoIndice.close();
  }

  /**
   * lista-se os times no arquivo auxiliar, após escolhido, chama o método privado
   * que efetuará a
   * busca pelo nome do país
   * 
   * @return
   * @throws Exception
   */
  public ArrayList<Player> procura() throws Exception {
    Scanner arqTimes = new Scanner(new File(prefixo + listaTimesExistentes));
    String strCsv = arqTimes.nextLine();
    String times[] = strCsv.split(",");
    System.out.println("Escolha o número do time que deseja pesquisar:");
    for (int c = 0; c < times.length; c++) {
      System.out.println(c + ")" + times[c]);
    }
    Scanner sc = new Scanner(System.in);
    int numTimes = sc.nextInt();
    ArrayList<Player> resp = procura(times[numTimes]);
    arqTimes.close();
    return resp;

  }

  /**
   * as procuras no índice são feitas utilizando o hash para ter mais eficiência
   * 
   * @param time
   * @return
   * @throws Exception
   */

  private ArrayList<Player> procura(String time) throws Exception {
    ArrayList<Player> resp = new ArrayList<>();
    String nomeArquivo = prefixo + time + ".db";
    RAF arquivo = new RAF(nomeArquivo, "r");
    arquivo.movePointerToStart();
    while (arquivo.canRead()) {
      int id = arquivo.readInt();
      Hash indiceHash = new Hash(0, arqHash, 1, false);
      IndexDAO index = new IndexDAO(arqPrincipal, indiceHash);
      Player player = index.read(id);
      resp.add(player);
    }
    arquivo.close();
    return resp;
  }

  public void imprime(ArrayList<Player> lista) {
    for (Player l : lista) {
      System.out.print(l);
    }
  }

  /**
   * no método de deleção, as alteração somente persistem no índice secundário,
   * deste modo,
   * alterações no arquivo de dados deverão chamar a função de deleção, do
   * contrário, poderá haver
   * informações incongruentes neste método são feitas as deleções, procura-se o
   * id desejado,
   * salva esta posição, grava o último id nela, depois trunca-se o arquivo com um
   * registro a
   * menos.
   */

  public boolean delete(Player player) throws Exception {
    String times[] = player.getTeams();
    String nomeTime = times[0];
    boolean resp = false;
    String nomeArquivo = prefixo + nomeTime + ".db";
    RAF arquivo = new RAF(nomeArquivo, "rw");
    arquivo.movePointerToStart();
    while (arquivo.canRead()) {
      long posicaoDeletada = arquivo.getFilePointer();
      int id = arquivo.readInt();
      if (id == player.getPlayerId()) {
        long posicaoUltimo = arquivo.length() - 4;
        arquivo.seek(posicaoUltimo);
        int temp = arquivo.readInt();
        arquivo.seek(posicaoDeletada);
        arquivo.writeInt(temp);
        arquivo.setLength(posicaoUltimo);
        resp = true;
        break;
      }
    }
    arquivo.close();
    return resp;

  }

  private void apagaIndice() throws Exception {
    try {
      Scanner arqTimes = new Scanner(new File(prefixo + listaTimesExistentes));
      String strCsv = arqTimes.nextLine();
      String times[] = strCsv.split(",");
      for (String t : times) {
        File arquivo = new File(prefixo + t + ".db");
        arquivo.delete();
      }
    } catch (Exception NoSuchElementException) {
      File arq = new File(prefixo + listaTimesExistentes);
    }
  }

}

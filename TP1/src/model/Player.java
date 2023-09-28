package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Representa a entidade Jogador com os campos nome, o campo multivalorado
 * times, id, data de nascimento, país e rating.
 */
public class Player {
  private String name;
  private String[] teams;
  private int playerId;
  private long birthDate;
  private String country;
  private float rating;

  public Player() {
  }

  public Player(String name, String[] teams, int playerId, String birthDate, String country, float rating) {
    this.setName(name);
    this.setTeams(teams);
    this.setPlayerId(playerId);
    this.setBirthDate(birthDate);
    this.setCountry(country);
    this.setRating(rating);
  }

  public Player(String name, String[] teams, int playerId, long birthDate, String country, float rating) {
    this.setName(name);
    this.setTeams(teams);
    this.setPlayerId(playerId);
    this.setBirthDate(birthDate);
    this.setCountry(country);
    this.setRating(rating);
  }

  @Override
  public String toString() {
    String returnString = "Player" +
        "\nNickname: " + this.name + "\nTeams: ";

    for (String team : teams) {
      returnString += team + ", ";
    }

    // Remove os últimos dois caracteres, que não devem ser utilizados.
    returnString = returnString.substring(0, returnString.length() - 2);

    // Converter data de nascimento de float para String
    String birthDateString = "";
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date(this.birthDate);
      birthDateString = dateFormat.format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }

    returnString += "\nID: " + this.playerId +
        "\nBirthdate: " + birthDateString +
        "\nCountry: " + this.country +
        "\nRating: " + this.rating;

    return returnString;
  }

  /**
   * Compara o ID do jogador que chamou a função com o ID do jogador que é passado
   * como parâmetro.
   * 
   * @param that Jogador que quer se descobrir se o ID atual é maior ou não.
   * @return Se ID deste registro for maior que aquele ou falso se aquele jogador
   *         for null.
   */
  public boolean isBiggerThan(Player that) {
    if (that != null) {
      return this.playerId > that.playerId;
    }
    return false;
  }

  /**
   * Transforma o Jogador em array de bytes.
   * 
   * @return byte[] conténdo os dados do jogador na seguinte ordem: ID (int),
   *         nome (UTF), número de times (int), nome dos times (UTF),
   *         data de nascimento (long), país (UTF) e rating (float).
   * @throws IOException Erro na escrita dos bytes.
   */
  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(getPlayerId());
    dos.writeUTF(getName());
    dos.writeInt(getTeams().length);

    // Escreve os times no arquivo
    for (String team : teams) {
      dos.writeUTF(team);
    }

    dos.writeLong(getBirthDate());
    dos.writeUTF(getCountry());
    dos.writeFloat(getRating());
    dos.close();

    return baos.toByteArray();
  }

  /**
   * Substitui os atributos do jogador pelos que lê em um array de bytes.
   * 
   * @param byteArray deve conter as informações a serem importadas pelo jogador,
   *                  na seguinte ordem: ID (int), nome (UTF), número de
   *                  times (int), nome dos times (UTF), data de nascimento
   *                  (long), país (UTF) e rating (float).
   * @throws IOException Erro na leitura dos bytes
   */
  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    setPlayerId(dis.readInt());
    setName(dis.readUTF());
    String[] teams = new String[dis.readInt()];

    // Insere os times no array e salva na variável
    for (int i = 0; i < teams.length; i++) {
      teams[i] = dis.readUTF();
    }
    setTeams(teams);

    setBirthDate(dis.readLong());
    setCountry(dis.readUTF());
    setRating(dis.readFloat());

    dis.close();
  }

  /**
   * Substitui os atributos do jogador pelos que lê em uma linha CSV.
   * 
   * @param csvLine Linha CSV separada por vírgulas, deve conter as informações a
   *                serem importadas pelo jogador, na seguinte ordem: ID (int);
   *                nome (String); nome dos times (String - caso possua mais de um
   *                time deve ter o caractere '\"' na primeira e na última
   *                posição, além de espaço separando os campos); data de
   *                nascimento (AAAA-MM-DD); país (String) e rating (float).
   * @throws IOException Erro na leitura dos da string.
   * @throws Exception   Se o campo que indica o primeiro time começa com abre
   *                     aspas e não houver outro que as feche, ocorre um erro na
   *                     atribuição.
   */
  public void fromCSVLine(String csvLine) throws Exception {
    String[] fields = csvLine.split(",");
    int i = 0;

    setName(fields[i++]);
    setTeams(getTeams(fields, i));
    i += this.teams.length; // Pula os campos utilizados para o time

    setPlayerId(Integer.parseInt(fields[i++]));
    setBirthDate(fields[i++]);
    setCountry(fields[i++]);
    setRating(Float.parseFloat(fields[i]));
  }

  /**
   * Organiza os campos extraídos da linha em um array que contém o nome dos
   * times em um novo array de Strings, além de eliminar espaços e aspas
   * desnecessárias.
   * 
   * @param fields Campos extraídos da linha CSV
   * @param index  A partir de qual posição deve-se extrair os times
   * @return Array de Strings com os times contidos nos campos.
   * @throws Exception Se o campo começar com abre aspas e não houver outro que
   *                   termine as fechando, a função entra em loop infinito.
   */
  private static String[] getTeams(String[] fields, int index) throws Exception {
    ArrayList<String> teams = new ArrayList<>();
    teams.add(fields[index]);

    // Cria múltiplos times se o primeiro começa com aspas duplas
    if (fields[index].startsWith("\"")) {
      teams.set(0, fields[index].substring(1)); // Remover aspas do primeiro time

      String lastTeam = null;
      // Adiciona todos até a variável last team realmente ser o último registro
      do {
        lastTeam = fields[++index].substring(1); // Remove o espaço que separa os campos
        teams.add(lastTeam);
      } while (!lastTeam.endsWith("\""));

      // Remove as aspas ao final do último time
      teams.set(teams.size() - 1, lastTeam.substring(0, lastTeam.length() - 1));
    }

    return teams.toArray(new String[0]);
  }

  String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getTeams() {
    return this.teams;
  }

  public void setTeams(String[] teams) {
    this.teams = teams;
  }

  public int getPlayerId() {
    return this.playerId;
  }

  public void setPlayerId(int id) {
    this.playerId = id;
  }

  public long getBirthDate() {
    return this.birthDate;
  }

  public void setBirthDate(long birthDate) {
    this.birthDate = birthDate;
  }

  /**
   * @param birthDate Deve ser do tipo "AAAA-MM-DD"
   */
  public void setBirthDate(String birthDate) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = dateFormat.parse(birthDate);
      this.birthDate = date.getTime();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getCountry() {
    return this.country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public float getRating() {
    return this.rating;
  }

  public void setRating(float rating) {
    this.rating = rating;
  }
}

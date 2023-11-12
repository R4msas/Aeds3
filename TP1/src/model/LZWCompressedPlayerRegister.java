package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import Compressao.lzw.LZW;

public class LZWCompressedPlayerRegister {
  private LZW compacter;

  private static final short TOMBSTONE_INDICATOR = (short) (1 << 15);

  private boolean tombstone;
  private ArrayList<Short> name;
  private ArrayList<Short> teams;
  private ArrayList<Short> id;
  private ArrayList<Short> birthDate;
  private ArrayList<Short> country;
  private ArrayList<Short> rating;

  public LZWCompressedPlayerRegister(LZW compacter) {
    this.compacter = compacter;
  }

  public LZWCompressedPlayerRegister(PlayerRegister register, LZW lzwObject) throws IOException {
    this(lzwObject);
    this.tombstone = register.isTombstone();
    Player player = register.getPlayer();

    this.name = this.compacter.compact(player.getName());
    this.teams = new ArrayList<>();
    String[] teams = player.getTeams();

    this.teams.add((short) teams.length);
    for (String team : teams) {
      ArrayList<Short> teamtoInsert = new ArrayList<>();
      ArrayList<Short> compactedTeam = this.compacter.compact(team);

      teamtoInsert.add((short) compactedTeam.size());
      teamtoInsert.addAll(compactedTeam);

      this.teams.addAll(teamtoInsert);
    }

    this.id = this.compacter.compact(player.getPlayerId() + "");
    this.birthDate = this.compacter.compact(player.getBirthDate() + "");
    this.country = this.compacter.compact(player.getCountry());
    this.rating = this.compacter.compact(player.getRating() + "");
  }

  public PlayerRegister getRegisterFromCompressedFile(RandomAccessFile raf, boolean ignoreTombstone)
      throws IOException {
    long position = raf.getFilePointer();
    fromCompressedFile(raf, ignoreTombstone);

    return new PlayerRegister(position, this.tombstone, toPlayer());
  }

  public void fromCompressedFile(RandomAccessFile raf, boolean ignoreTombstone) throws IOException {
    short header = raf.readShort();

    this.tombstone = (header & TOMBSTONE_INDICATOR) == TOMBSTONE_INDICATOR;
    int size = this.tombstone ? header - TOMBSTONE_INDICATOR : header;

    if (this.tombstone && ignoreTombstone) {
      raf.skipBytes(size);
    } else {
      byte[] byteArray = new byte[size];

      raf.read(byteArray);
      fromByteArray(byteArray);
    }
  }

  public Player toPlayer() {
    String name = compacter.discompact(this.name);

    String[] teams = new String[this.teams.get(0)];
    short teamSize = 0;
    for (int compactedIndex = 1,
        teamIndex = 0; compactedIndex < this.teams.size(); compactedIndex += teamSize, teamIndex++) {

      teamSize = this.teams.get(compactedIndex++);
      String team = compacter.discompact(this.teams.subList(compactedIndex, compactedIndex + teamSize));

      teams[teamIndex] = team;
    }

    int id = Integer.parseInt(compacter.discompact(this.id));
    long birthDate = Long.parseLong(compacter.discompact(this.birthDate));
    String country = compacter.discompact(this.country);
    float rating = Float.parseFloat(compacter.discompact(this.rating));

    return new Player(name, teams, id, birthDate, country, rating);
  }

  public void fromByteArray(byte[] byteArray) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
    DataInputStream dis = new DataInputStream(bais);

    byte nameLength = dis.readByte();
    this.name = new ArrayList<>(nameLength);
    for (byte index = 0; index < nameLength; index++) {
      this.name.add(dis.readShort());
    }

    this.teams = new ArrayList<>();

    byte numberOfTeams = dis.readByte();
    this.teams.add((short) numberOfTeams);

    for (int index = 0; index < numberOfTeams; index++) {
      byte teamSize = dis.readByte();
      this.teams.add((short) teamSize);

      for (int i = 0; i < teamSize; i++) {
        short teamByte = dis.readShort();
        this.teams.add(teamByte);
      }
    }

    byte idLength = dis.readByte();
    this.id = new ArrayList<>(idLength);
    for (byte index = 0; index < idLength; index++) {
      this.id.add(dis.readShort());
    }

    byte birthDateLength = dis.readByte();
    this.birthDate = new ArrayList<>(birthDateLength);
    for (int index = 0; index < birthDateLength; index++) {
      this.birthDate.add(dis.readShort());
    }

    byte countryLength = dis.readByte();
    this.country = new ArrayList<>(countryLength);
    for (int index = 0; index < countryLength; index++) {
      this.country.add(dis.readShort());
    }

    byte ratingLength = dis.readByte();
    this.rating = new ArrayList<>(ratingLength);
    for (int i = 0; i < ratingLength; i++) {
      this.rating.add(dis.readShort());
    }

    dis.close();
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeByte(name.size());
    for (short shortByte : name) {
      dos.writeShort(shortByte);
    }

    byte numOfTeams = (byte) teams.get(0).shortValue();
    dos.writeByte(numOfTeams);

    byte teamSize = 0;
    for (byte index = 1; index < teams.size(); index += teamSize) {
      teamSize = (byte) teams.get(index++).shortValue();
      dos.writeByte(teamSize);

      for (byte i = 0; i < teamSize; i++) {
        short teamByte = teams.get(index + i);
        dos.writeShort(teamByte);
      }
    }

    dos.writeByte(id.size());
    for (short shortByte : id) {
      dos.writeShort(shortByte);
    }

    dos.writeByte(birthDate.size());
    for (short shortByte : birthDate) {
      dos.writeShort(shortByte);
    }

    dos.writeByte(country.size());
    for (short shortByte : country) {
      dos.writeShort(shortByte);
    }

    dos.writeByte(rating.size());
    for (short shortByte : rating) {
      dos.writeShort(shortByte);
    }

    dos.close();
    return baos.toByteArray();
  }

  public void writeOnFile(RandomAccessFile raf) throws IOException {
    byte[] byteArray = toByteArray();

    int byteArrayLength = byteArray.length & ((int) Math.pow(2, Short.SIZE) - 1);
    short tombstoneIndicator = tombstone ? TOMBSTONE_INDICATOR : 0;

    short header = (short) (byteArrayLength | tombstoneIndicator);
    raf.writeShort(header);
    raf.write(byteArray);
  }
}
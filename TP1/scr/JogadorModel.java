import java.time.LocalDate;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class JogadorModel {


    private int id;
    private String nick;
    private String time;
    private int idade;
    private LocalDate dataNascimento;
    private String pais;
    private float rating;
    private Boolean lapide;

    

    
    // getters e setters
    public Boolean getLapide()
    {
        return lapide;
    }

    public void setLapide(Boolean lapide)
    {
        this.lapide = lapide;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public int getIdade()
    {
        return idade;
    }

    public void setIdade(int idade)
    {
        this.idade = idade;
    }

    public LocalDate getDataNascimento()
    {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento)
    {
        this.dataNascimento = dataNascimento;
    }

    public String getPais()
    {
        return pais;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    public float getRating()
    {
        return rating;
    }

    public void setRating(float rating)
    {
        this.rating = rating;
    }


    // construtores
    public JogadorModel(Boolean lapide) {
        this.lapide = lapide;
    }
    public JogadorModel(int id, String nick, String time, int idade, LocalDate dataNascimento,
            String pais, float rating, Boolean lapide) {
        this.id = id;
        this.nick = nick;
        this.time = time;
        this.idade = idade;
        this.dataNascimento = dataNascimento;
        this.pais = pais;
        this.rating = rating;
        this.lapide=lapide;
    }

    public JogadorModel() {
        this.id = -1;
        this.nick = null;
        this.time = null;
        this.idade = -1;
        this.dataNascimento = null;
        this.pais = null;
        this.rating = -1;
        this.lapide=false;
    }


    
}

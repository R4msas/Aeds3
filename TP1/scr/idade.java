import java.util.*;
import java.time.LocalDate;
import java.lang.Math;

class Idade {



    private LocalDate dataNascimento;
    private int idadeAtual;
//getters and setters
    public int getIdadeAtual()
    {
        return idadeAtual;
    }

    public void setIdadeAtual(int idadeAtual)
    {
        this.idadeAtual = idadeAtual;
    }

    public LocalDate getDataNascimento()
    {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento)
    {
        this.dataNascimento = dataNascimento;
    }

//construtors
    public Idade()
    {
        this.idadeAtual=-1;
        this.dataNascimento=null;
    }
    public Idade(int idadeAtual) {//construtor recebe a idade e gera um mês e dia aleatório de nascimento
        this.idadeAtual = idadeAtual;
        int dia, mes, ano;
        Random rMes = new Random();
        mes = (Math.abs(rMes.nextInt()))%11;
        mes++;
        int seedDia;
        if (mes == 4 || mes == 6 || mes == 9 || mes == 11)
        {
            seedDia = 29;
        } else if (mes == 2)
        {
            seedDia = 27;
        } else
        {
            seedDia = 30;
        }

        Random rDia = new Random();
        dia = Math.abs((rDia.nextInt()))%seedDia;
        dia++;
        ano = LocalDate.now().getYear() - idadeAtual;
        this.dataNascimento=LocalDate.of(ano, mes, dia);
    }

public static void main(String[] args) {
    int idade;
    Scanner sc=new Scanner(System.in);
    do
    {
        System.out.println("informe a idade:");
        idade=sc.nextInt();
        Idade id=new Idade(idade);
        System.out.println(id.dataNascimento);
    } while(idade!=0);
sc.close();
}
  
}

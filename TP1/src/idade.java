import java.util.*;
import java.time.LocalDate;

class idade {

    private LocalDate dataNascimento;
    private int idadeAtual;

    // getters and setters
    public int getIdadeAtual() {
        return idadeAtual;
    }

    public void setIdadeAtual(int idadeAtual) {
        this.idadeAtual = idadeAtual;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    // construtors
    public idade() {
        this.idadeAtual = -1;
        this.dataNascimento = null;
    }

    public idade(int idadeAtual) {// construtor recebe a idade e gera um mês e dia aleatório de nascimento
        this.idadeAtual = idadeAtual;
        int dia, mes, ano;
        Random rMes = new Random(11);// gera um número aleatório de 0 a 11(doze possibilidades)
        mes = rMes.nextInt() + 1;
        int seedDia;
        if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            seedDia = 29;
        } else if (mes == 2) {
            seedDia = 27;
        } else {
            seedDia = 30;
        }

        Random rDia = new Random(seedDia);
        dia = rDia.nextInt() + 1;
        ano = LocalDate.now().getYear() - idadeAtual;
        this.dataNascimento = LocalDate.of(ano, mes, dia);
    }

public static void main(String[] args) {
    int idade;
    do
    {
        System.out.println("informe a idade:");
        idade=System.in.read();
    } while(idade!=0)

}

}

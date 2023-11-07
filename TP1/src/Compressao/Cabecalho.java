package Compressao;

import java.io.IOException;

import main.RAF;

public class Cabecalho {
    private int numeroDeBitsUltimoByte;
    private int numeroBytes;
    private long inicioTabela;
    private long inicioTexto;
    public int numeroNos;    
    public int getNumeroDeBitsUltimoByte() {
        return numeroDeBitsUltimoByte;
    }

    public void setNumeroDeBitsUltimoByte(int numeroDeBitsUltimoByte) {
        this.numeroDeBitsUltimoByte = numeroDeBitsUltimoByte;
    }

    public int getNumeroBytes() {
        return numeroBytes;
    }

    public void setNumeroBytes(int numeroBytes) {
        this.numeroBytes = numeroBytes;
    }

    public long getInicioTabela() {
        return inicioTabela;
    }

    public void setInicioTabela(long inicioTabela) {
        this.inicioTabela = inicioTabela;
    }

    public long getInicioTexto() {
        return inicioTexto;
    }

    public void setInicioTexto(long inicioTexto) {
        this.inicioTexto = inicioTexto;
    }

    public Cabecalho(){
        inicioTabela=16;
    }
    public Cabecalho(RAF arquivoEntrada) throws IOException
    {
        arquivoEntrada.movePointerToStart();
        inicioTexto=arquivoEntrada.readLong();
        inicioTabela=16;
        numeroBytes=arquivoEntrada.readInt();
        numeroDeBitsUltimoByte=arquivoEntrada.readInt();
    }
    public void incrementaNumeroBytes()
    {
        numeroBytes++;
    }
    public void incrementaNumeroNos(){
        numeroNos++;
    }
    public void atualizaCabecalho(RAF arquivo) throws IOException
    {
        arquivo.movePointerToStart();
        arquivo.writeLong(inicioTexto);
        arquivo.writeInt(numeroBytes);
        arquivo.writeInt(numeroDeBitsUltimoByte);
    }
}

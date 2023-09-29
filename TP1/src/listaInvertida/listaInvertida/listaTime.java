package listaInvertida;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import hash.Hash;
import hash.Index;
import main.RAF;
import model.*;
public class listaTime {
    
    
        private int  id;
        private float rating;
        private String listaTimesExistentes="listaTimesExistentes.txt";
        private String prefixo="resources/indiceSecundario/times/";
    
        public listaTime() {
        }
        public listaTime(int id, float rating) {
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
        public void insere(PlayerRegister player)throws Exception
        {
            String nomeTime=player.getPlayer().getCountry();
            if(ExisteTime(nomeTime))
            {
            FileWriter arqTime=new FileWriter(prefixo+listaTimesExistentes,true);
            arqTime.write(nomeTime+",");
            arqTime.close();
            }
            String nomeArq=prefixo+nomeTime+".db";
            RAF indice=new RAF(nomeArq, "rw");
            indice.movePointerToEnd();
            int id=player.getPlayer().getPlayerId();
            indice.writeInt(id);
            indice.close();
        }
        public boolean ExisteTime(String nomeTime)throws Exception
        {
            boolean resp=true;
            try{
            Scanner arqTimes=new Scanner(new File(prefixo+listaTimesExistentes));
            String strCsv=arqTimes.nextLine();
            String times[]=strCsv.split(",");

            for(int c=0; c<times.length;c++)
            {
                if(times[c].equals(nomeTime))
                {
                    resp=false;
                    break;
                }
            }}
            catch(Exception e) {
                resp=true;
            }
            return resp;            
        }
        public void criaIndiceSecundario(String caminhoDoArquivo)throws Exception{
            RAF arquivoIndice= new RAF(caminhoDoArquivo, "r");
            arquivoIndice.skipBytes(4);//primeira parte do arquivo é
            while(arquivoIndice.canRead()){
               PlayerRegister player=new PlayerRegister();
               player.fromFile(arquivoIndice, true);
               if(!player.isTombstone())
               {
                    insere(player);
               }
            }
            arquivoIndice.close();
        }
        public ArrayList<Player> procura()throws Exception
        {
            Scanner arqTimes=new Scanner(new File(prefixo+listaTimesExistentes));
            String strCsv=arqTimes.nextLine();
            String times[]=strCsv.split(",");
            System.out.println("Escolha o número de pais que deseja pesquisar:");
            for(int c=0; c<times.length;c++)
            {
                System.out.println(c+")"+times[c]);
            }
            Scanner sc=new Scanner(System.in);
            int numTimes=sc.nextInt();
            ArrayList<Player>resp=procura(times[numTimes]);
            sc.close();
            arqTimes.close();
            return resp;

        }
        private ArrayList<Player> procura(String pais)throws Exception
        {
            ArrayList<Player> resp=new ArrayList<>();
            String nomeArquivo=prefixo+pais+".db";
            RAF arquivo=new RAF(nomeArquivo,"r");
            while(arquivo.canRead())
            {
                PlayerRegister tmp=new PlayerRegister();
                Index indiceHash=new Index();
                Hash hash=new Hash();
                int id=arquivo.readInt();
                indiceHash.read(id);
                /*esse pedaço aqui está errado por enquanto, ver com o Andre como retornar um player para cada ID */
                
            }


            arquivo.close();
            return resp;
        }

        }
    

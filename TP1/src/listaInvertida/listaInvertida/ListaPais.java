package listaInvertida;
import model.*;
import java.util.Scanner;
import main.RAF;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import hash.*;
import dao.*;

/*Esta classe cria um índice de clustering de paises, cada índice secundário tem como nome do país dos jogadores, dentro deste arquivo estarão os id's de cada jogador. */
public class ListaPais {
    
        private int  id;
        private String pais;
        private String listaPaisesExistentes="listaPaisesExistentes.txt";
        private String prefixo="resources/indiceSecundario/pais/";
        private String arqHash="resources/db/";
        private String arqPrincipal="resources/db/csgo_players.db";
    
        public ListaPais(int id, String pais) {
            this.id = id;
            this.pais = pais;
        }
        public ListaPais() {
            id=-1;
            pais=null; 
        }

        // Getters e Setters
    
        public int getId() {
            return id;
        }
    
        public String getPais() {
            return pais;
        }
    
        public void setId(int id) {
            this.id = id;
        }
    
        public void setPais(String pais) {
            this.pais = pais;
        }
       
        public void insere(PlayerRegister player)throws Exception
        {
            String nomePais=player.getPlayer().getCountry();
            if(ExistePais(nomePais))
            {
            FileWriter arqPais=new FileWriter(prefixo+listaPaisesExistentes,true);
            arqPais.write(nomePais+",");
            arqPais.close();
            }
            String nomeArq=prefixo+nomePais+".db";
            RAF indice=new RAF(nomeArq, "rw");
            indice.movePointerToEnd();
            int id=player.getPlayer().getPlayerId();
            indice.writeInt(id);
            indice.close();
        }
        public boolean ExistePais(String nomePais)throws Exception
        {
            boolean resp=true;
            try{
            Scanner arqPaises=new Scanner(new File(prefixo+listaPaisesExistentes));
            String strCsv=arqPaises.nextLine();
            String paises[]=strCsv.split(",");

            for(int c=0; c<paises.length;c++)
            {
                if(paises[c].equals(nomePais))
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
            Scanner arqPaises=new Scanner(new File(prefixo+listaPaisesExistentes));
            String strCsv=arqPaises.nextLine();
            String paises[]=strCsv.split(",");
            System.out.println("Escolha o número de pais que deseja pesquisar:");
            for(int c=0; c<paises.length;c++)
            {
                System.out.println(c+")"+paises[c]);
            }
            Scanner sc=new Scanner(System.in);
            int numPais=sc.nextInt();
            ArrayList<Player>resp=procura(paises[numPais]);
            arqPaises.close();
            return resp;

        }
        private ArrayList<Player> procura(String pais)throws Exception
        {
            ArrayList<Player> resp=new ArrayList<>();
            String nomeArquivo=prefixo+pais+".db";
            RAF arquivo=new RAF(nomeArquivo,"r");
            arquivo.movePointerToStart();
            while(arquivo.canRead())
            {
                int id=arquivo.readInt();
                Hash indiceHash=new Hash(0,arqHash,1,false);
                IndexDAO index=new IndexDAO(arqPrincipal,indiceHash);
                Player player=index.read(id);
                resp.add(player);
            }
            arquivo.close();
            return resp;
        }
        public void imprime(ArrayList<Player>lista)
        {
            for(Player l:lista)
            {
                System.out.print(l);
            }
        }
        public ArrayList <Player> join(ArrayList <Player> paises,ArrayList <Player> times)
        {
            ArrayList <Player> resp=new ArrayList<>();
            for (Player p:paises)
            {
                for(Player pl:times)
                {
                    if(p.getPlayerId()==pl.getPlayerId())
                    {
                        resp.add(p);
                    }
                }
            }
            return resp;
        }
        
        }
    

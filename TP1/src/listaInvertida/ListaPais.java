import model.PlayerRegister;
import java.util.Scanner;
import main.RAF;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
public class ListaPais {
    
        private int  id;
        private String pais;
        private String nomeIndice="indicePais.db";
        private String listaPaisesExistentes="resources/indiceSecundario/listaPaisesExistentes.txt";
    
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
            FileWriter arqPais=new FileWriter(listaPaisesExistentes,true);
            arqPais.write(nomePais+",");
            arqPais.close();
            }
            String nomeArq="resources/indiceSecundario/"+nomePais+".db";
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
            Scanner arqPaises=new Scanner(new File(listaPaisesExistentes));
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

        }
    

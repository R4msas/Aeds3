import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import main.RAF;
import model.PlayerRegister;
public class ListaPais {
    
        private int  id;
        private String pais;
        private String nomeIndice="indicePais.db";
    
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
        public ArrayList<ListaPais> criaLista(String caminhoDoArquivo)throws Exception{
             ArrayList<ListaPais> listaPaises = new ArrayList<>();
             RAF arquivoIndice= new RAF(caminhoDoArquivo, "r");
             arquivoIndice.skipBytes(4);
             while(arquivoIndice.canRead()){
                PlayerRegister player=new PlayerRegister();
                player.fromFile(arquivoIndice, true);
                if(!player.isTombstone())
                {

                ListaPais listaPais=new ListaPais(player.getPlayer().getPlayerId(), player.getPlayer().getCountry());
                listaPaises.add(listaPais);
                }
             }
             arquivoIndice.close();
             return listaPaises;
        }
        public void ordenaLista(ArrayList<ListaPais> listaPaises)
        {
            Comparator<ListaPais> comparadorPorPais = Comparator.comparing(ListaPais::getPais);
            Collections.sort(listaPaises, comparadorPorPais);
        }
        public void criaIndiceSecundario(ArrayList<ListaPais> listaPaises)throws Exception{
            RAF indicePais=new RAF(nomeIndice, "rw");
            boolean escrevePais=true;
            int contador=0;
            String nomePais=listaPaises(0);
            for (ListaPais listaPais : listaPaises) {
                if(escreverPais)
                {  
                indicePais.writeInt(listaPais.getId());
                

                }
                indicePais.writeUTF(listaPais.getPais());
                contador++;
                
            }
            indicePais.close();
        }
        public void percorreEContaPais(ArrayList<ListaPais>){

        }

        }
    

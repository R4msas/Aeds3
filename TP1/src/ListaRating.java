import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import main.RAF;
import model.*;
public class ListaRating {
    
    
        private int  id;
        private float rating;
        public ListaRating() {
        }
        public ListaRating(int id, float rating) {
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
        public ArrayList<ListaRating> criaLista(String caminhoDoArquivo)throws Exception{
             ArrayList<ListaRating> listaRatings = new ArrayList<>();
             RAF arquivoIndice= new RAF(caminhoDoArquivo, "r");
             arquivoIndice.skipBytes(4);//primeira parte do arquivo é 
             while(arquivoIndice.canRead()){
                PlayerRegister player=new PlayerRegister();
                player.fromFile(arquivoIndice, true);
                if(!player.isTombstone())
                {

                ListaRating listaRating=new ListaRating(player.getPlayer().getPlayerId(), player.getPlayer().getRating());
                listaRatings.add(listaRating);
                }
             }
             arquivoIndice.close();
             return listaRatings;
        }
        public void ordenaLista(ArrayList<ListaRating> listaRatings)
        {
            Comparator<ListaRating> comparadorPorRating = Comparator.comparing(ListaRating::getRating);
            Collections.sort(listaRatings, comparadorPorRating);
        }
        public void criaIndiceSecundario(ArrayList<ListaRating> ListaRatings)throws Exception{
            RAF indiceRating=new RAF("indiceRating.db", "rw");
            for (ListaRating ListaRating : ListaRatings) {
                indiceRating.writeInt(ListaRating.getId());
                indiceRating.writeFloat(ListaRating.getRating());
            }
            indiceRating.close();
        }

        }
    

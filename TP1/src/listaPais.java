import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccessFile;
public class listaPais {
    
        private long enderecoArquivoDados;
        private String pais;
    
        public listaPais(long enderecoArquivoDados, String pais) {
            this.enderecoArquivoDados = enderecoArquivoDados;
            this.pais = pais;
        }
    
        // Getters e Setters
    
        public long getEnderecoArquivoDados() {
            return enderecoArquivoDados;
        }
    
        public String getPais() {
            return pais;
        }
    
        public void setEnderecoArquivoDados(long enderecoArquivoDados) {
            this.enderecoArquivoDados = enderecoArquivoDados;
        }
    
        public void setPais(String pais) {
            this.pais = pais;
        }
        public void criaLista(){
             List<listaPais> listaPaises = new ArrayList<>();
             RandomAcessFile arquivoIndice
             while(){

             }
    
        }
        public void ordenaLista(ArrayList<listaPais> listaPaises)
        {
            Comparator<listaPais> comparadorPorPais = Comparator.comparing(listaPais::getPais);
            Collections.sort(listaPaises, comparadorPorPais);


        }
        public void criaIndiceSecundario(ArrayList<listaPais> listaPaises){
            for (listaPais listaPais : listaPaises) {
                
            }
        }
        }
    }
    
class OrdenacaoPorPais {
        public static void main(String[] args) {
           
        }
    } 


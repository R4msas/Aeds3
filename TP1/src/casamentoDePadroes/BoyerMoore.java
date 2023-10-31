package casamentoDePadroes;

import java.util.HashMap;
import java.util.Map;
import model.PlayerRegister;

public class BoyerMoore {
    public static String findBoyerMoore(PlayerRegister player, char[] pattern) {
      char[]text=player.getPlayer().toString().toCharArray();
      int n = text.length;
	  int m = pattern.length;
      String resp="";
	    
	  // Test for empty string
	  if (m == 0) 
      {
        resp="";
      }
      else{
	  // Initialization, create Map of last position of each character = O(n)
	  Map<Character, Integer> last = new HashMap<>();
	  for (int i = 0; i < n; i++) {
		  last.put(text[i], -1);   // set all chars, by default, to -1
	  }        
	  for (int i = 0; i < m; i++) {
		  last.put(pattern[i], i); // update last seen positions
	  }
	    
	  // Start with the end of the pattern aligned at index m-1 in the text. 
	  int i = m - 1;  // index into the text
	  int k = m - 1;  // index into the pattern
	  while (i < n) { 
		  if (text[i] == pattern[k]) { // match! return i if complete match; otherwise, keep checking.
			  if (k == 0) {
				  System.out.println("\tFound match at index " + i);
                  resp=text.toString();
                  break;
			  }
			  i--; k--;
		  } 
		  else { // jump step + restart at end of pattern
			  i += m - Math.min(k, 1 + last.get(text[i]));  //move in text 
			  k = m - 1; //move to end of pattern
		  }
	  }
    }
	  System.out.println("\tNo match found");
	  return resp; // not found
  }
}

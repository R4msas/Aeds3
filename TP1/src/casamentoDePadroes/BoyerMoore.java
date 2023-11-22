package casamentoDePadroes;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {
	public static int comparisons;
	protected static boolean findBoyerMoore(char[] text, char[] pattern)
	{
		int n = text.length;
		int m = pattern.length;
		boolean resp = false;
		if (m == 0)
		{
			resp = false;
		} else
		{
			Map<Character, Integer> last = new HashMap<>();
			for (int i = 0; i < n; i++)
			{
				last.put(text[i], -1);
			}
			for (int i = 0; i < m; i++)
			{
				last.put(pattern[i], i);
			}

			int i = m - 1;
			int k = m - 1;
			while (i < n)
			{
				comparisons++;
				if (text[i] == pattern[k])
				{
					if (k == 0)
					{
						resp = true;
						break;
					}
					i--;
					k--;
				} else
				{
					i += m - Math.min(k, 1 + last.get(text[i]));
					k = m - 1;
				}
			}
		}
		return resp;
	}
}

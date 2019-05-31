package permutation.pack;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author M1047227
 *
 */
public final class Permutation {
	public static ArrayList<List<Integer>> data = new ArrayList<List<Integer>>();

	private Permutation() {
	};

	/**
	 * Return permutation of a given string. But, if the string contains
	 * duplicate characters, it takes care to eradicate duplicate permutations.
	 * 
	 * @param string
	 *            the string whose permutation needs to be found out.
	 * @return a list with permuted values.
	 */
	public static List<String> permutation(String string) {
		final List<String> stringPermutations = new ArrayList<String>();
		permute(string, 0, stringPermutations);
		return stringPermutations;
	}

	private static void permute(String s, int currIndex, List<String> stringPermutations) {
		if (currIndex == s.length() - 1) {
			stringPermutations.add(s);
			return;
		}

		// prints the string without permuting characters from currIndex
		// onwards.
		permute(s, currIndex + 1, stringPermutations);

		// prints the strings on permuting the characters from currIndex
		// onwards.
		for (int i = currIndex + 1; i < s.length(); i++) {
			if (s.charAt(currIndex) == s.charAt(i))
				continue;
			s = swap(s, currIndex, i);
			permute(s, currIndex + 1, stringPermutations);
		}
	}

	private static String swap(String s, int i, int j) {
		char[] ch = s.toCharArray();
		char tmp = ch[i];
		ch[i] = ch[j];
		ch[j] = tmp;
		return new String(ch);
	}

	public static void findPermutation(String args) {
		for (String str : permutation(args)) {
			List<Integer> permuation = new ArrayList<Integer>();
			for (int i = 0; i < str.length(); i++) {
				permuation.add(Integer.parseInt(str.charAt(i) + ""));
			}
			Permutation.data.add(permuation);
		} 
	}

	// public static void main(String[] args) {
	// findPermutation("1");
	// System.out.println(Permutation.data);
	// }
}
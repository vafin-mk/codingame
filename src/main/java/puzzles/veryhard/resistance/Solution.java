package puzzles.veryhard.resistance;

import java.util.*;

class Solution {
  public static void main(String args[]) {
    new Solver(new Scanner(System.in)).solve();
  }
}

class Solver {
  final Scanner scanner;
  final String morseSequence;
  final Set<String> dictionary;
  final MorseEncoder morseEncoder;

  Solver(Scanner scanner) {
    this.scanner = scanner;
    morseEncoder = new MorseEncoder();
    morseSequence = scanner.next();
    int N = scanner.nextInt();
    dictionary = new HashSet<>(N);

    for (int i = 0; i < N; i++) {
      String word = scanner.next();
      dictionary.add(word);
    }
  }

  void solve() {
    Map<String, Integer> wordsCountByEncodings = new HashMap<>();
    int longestEncode = 0;

    for (String word : dictionary) {
      String encoded = morseEncoder.encodeWord(word);
      wordsCountByEncodings.putIfAbsent(encoded, 0);
      wordsCountByEncodings.put(encoded, wordsCountByEncodings.get(encoded) + 1);
      if (encoded.length() > longestEncode) {
        longestEncode = encoded.length();
      }
    }

    long[] matches = new long[morseSequence.length()];

    for (int wordEndIndex = 0; wordEndIndex < morseSequence.length(); wordEndIndex++) {
      for (int wordStartIndex = Math.max(0, wordEndIndex - longestEncode + 1); wordStartIndex <= wordEndIndex; wordStartIndex++) {
        String encoding = morseSequence.substring(wordStartIndex, wordEndIndex + 1);

        int wordsCountForEncoding = wordsCountByEncodings.getOrDefault(encoding, 0);

        if (wordsCountForEncoding > 0) {
          if (wordStartIndex == 0) {
            //single word for morseSequence
            matches[wordEndIndex] = wordsCountForEncoding;
          } else {
            matches[wordEndIndex] += wordsCountForEncoding * matches[wordStartIndex - 1];
          }
        }
      }
    }

    System.out.println(matches[morseSequence.length() - 1]);
  }

}

class MorseEncoder {
  String encode(char letter) {
    switch (letter) {
      case 65: return ".-";
      case 66: return "-...";
      case 67: return "-.-.";
      case 68: return "-..";
      case 69: return ".";
      case 70: return "..-.";
      case 71: return "--.";
      case 72: return "....";
      case 73: return "..";
      case 74: return ".---";
      case 75: return "-.-";
      case 76: return ".-..";
      case 77: return "--";
      case 78: return "-.";
      case 79: return "---";
      case 80: return ".--.";
      case 81: return "--.-";
      case 82: return ".-.";
      case 83: return "...";
      case 84: return "-";
      case 85: return "..-";
      case 86: return "...-";
      case 87: return ".--";
      case 88: return "-..-";
      case 89: return "-.--";
      case 90: return "--..";
    }
    throw new IllegalStateException();
  }

  String encodeWord(String word) {
    StringBuilder builder = new StringBuilder();
    for (char c : word.toCharArray()) {
      builder.append(encode(c));
    }
    return builder.toString();
  }
}
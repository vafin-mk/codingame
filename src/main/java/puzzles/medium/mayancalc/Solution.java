package puzzles.medium.mayancalc;

import java.util.*;
import java.util.stream.Collectors;

public class Solution {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int numberLength = in.nextInt();
    int numberHeight = in.nextInt();
    StringBuilder alphabet = new StringBuilder();
    for (int i = 0; i < numberHeight; i++) {
      alphabet.append(in.next());
    }
    Map<String, Integer> numbers = parseAlphabet(alphabet.toString(), numberLength, numberHeight);

    int firstNumberHeight = in.nextInt();
    StringBuilder firstNumber = new StringBuilder();
    for (int i = 0; i < firstNumberHeight; i++) {
      firstNumber.append(in.next());
    }
    long first = parseNumber(firstNumber.toString(), numbers, numberLength, numberHeight);

    int secondNumberHeight = in.nextInt();
    StringBuilder secondNumber = new StringBuilder();
    for (int i = 0; i < secondNumberHeight; i++) {
      secondNumber.append(in.next());
    }
    long second = parseNumber(secondNumber.toString(), numbers, numberLength, numberHeight);

    String operation = in.next();
    long result = 0;
    switch (operation) {
      case "*":
        result = first * second;
        break;
      case "/":
        result = first / second;
        break;
      case "+":
        result = first + second;
        break;
      case "-":
        result = first - second;
        break;
    }

    Map<Integer, String> inversedNumbers =
      numbers.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    String encoded = encodeNumber(result, inversedNumbers);
    for (int i = 0; i < encoded.length() / numberLength; i++) {
      System.out.println(encoded.substring(i * numberLength, (i + 1) * numberLength));
    }
  }

  private static Map<String, Integer> parseAlphabet(String alphabet, int numberLength, int numberHeight) {
    Map<String, Integer> result = new HashMap<>(20);
    for (int number = 0; number < 20; number++) {
      StringBuilder representation = new StringBuilder();
      for (int h = 0; h < numberHeight; h++) {
        int start = number * numberLength + numberLength * 20 * h;
        int end = start + numberLength;
        representation.append(alphabet.substring(start, end));
      }
      result.put(representation.toString(), number);
    }
    return result;
  }

  private static long parseNumber(String number, Map<String, Integer> numbers, int numberLength, int numberHeight) {
    int digitLength = numberLength * numberHeight;
    int digits = number.length() / digitLength - 1;
    long result = 0;
    while (true) {
      if (number.length() == digitLength) {
        result += numbers.get(number);
        break;
      }
      String digit = number.substring(0, digitLength);
      result += numbers.get(digit) * Math.pow(20, digits);
      number = number.substring(digitLength, number.length());
      digits--;
    }
    return result;
  }

  private static String encodeNumber(long number, Map<Integer, String> numbers) {
    List<String> digits = new ArrayList<>();
    while (true) {
      long digit = number % 20;
      digits.add(numbers.get((int)digit));
      if (number < 20) break;
      number = number / 20;
    }
    Collections.reverse(digits);

    StringBuilder result = new StringBuilder();
    for (String digit : digits) {
      result.append(digit);
    }
    return result.toString();
  }
}

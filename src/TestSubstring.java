import java.util.ArrayList;

public class TestSubstring {
    public static void main(String[] args) {
        String Orig = "abcbacdabdbabdbabcabddba";
        String SubStr = "abdbab";
        SubString test = new SubString();

        ArrayList<Integer> answer = test.RabinCarp(Orig, SubStr);
        System.out.println(answer);
        System.out.println("------------------------------");

        answer = test.BoyerMoore(Orig, SubStr);
        System.out.println(answer);
        System.out.println("------------------------------");

        answer = test.KnuthMorrisPratt(Orig, SubStr);
        System.out.println(answer);
    }
}
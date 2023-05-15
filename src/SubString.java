import java.util.ArrayList;

public class SubString {
    public SubString() {
    }

    // алгоритм Бойера-Мура // по стаблицам стоп символов и суффиксному смещению
    public ArrayList<Integer> BoyerMoore(String orig, String find) {
        ArrayList<Integer> answer = new ArrayList<>();

        String[] origMas = orig.split("");         // переводим строку в массив срок по символу в одну ячейку
        String[] findMas = find.split("");
        String[] tmpFind = find.split("");

        int[] stopTable = getStopTable(tmpFind); // таблица стоп-символов для каждого символа (по Ascii первые-служебные 32 символа не учитываются (без кирилицы))
        int[] suffTable = getSuffixTable(tmpFind); // таблица суффиксов

//        for (int i=0; i<suffTable.length; ++i)
//            System.out.print(suffTable[i]+"; ");
//        System.out.println("");

        for (int i=0; i<origMas.length-findMas.length+1; ++i){
            if (i>0)
                --i;
            int delta_stop = -5, delta_suff = -5;
            int j = findMas.length-1;
//            System.out.println(i);
            while ((j >= 0) && (findMas[j].equals(origMas[i+j]))) {
                //System.out.println(findMas[j]+" - "+origMas[i+j]);
                --j;
            }
            if (j == -1){
                answer.add(i+1);
                delta_stop = 1;
            }else {
                delta_stop = j - stopTable[TranslateToInt(origMas[i + j]) - 32];
//                System.out.println(origMas[i + j]);
//                System.out.println("*"+j+"-"+stopTable[TranslateToInt(origMas[i + j]) - 32]);
            }
            delta_suff = suffTable[j+1];
//            System.out.println("**"+delta_stop+" - "+delta_suff);
            i += Integer.max(delta_stop, delta_suff);
        }

        return answer;
    }
    private int[] PreffFunc(String[] S, int[] pi){// преффиксная функция
        int k = 0;
        pi[0] = k;
        for (int i=1; i<S.length; ++i){
            while ((k>0) && (!S[k].equals(S[i])))
                k = pi[k-1];
            if (S[k].equals(S[i]))
                ++k;
            pi[i] = k;
            //System.out.println(k+" - "+S[i]);
        }
        return pi;
    }
    private int[] getSuffixTable(String[] P){ // функция для заполнения таблицы суффиксов
        int m = P.length;
        int[] suffTable = new int[m+1];
        int[] PrefP = new int[m];
        PrefP = PreffFunc(P, PrefP);
        int[] RevPrefP = new int[m];
        RevPrefP = PreffFunc(reverse(P), RevPrefP);

        for (int i=0; i<m+1; ++i)             // верхняя граница таблицы суффиксов
            suffTable[i] = m - PrefP[m-1];

        for (int i=0; i<m; ++i){
            int index = m - RevPrefP[i];
            int shift = i - RevPrefP[i]+1;
            if (suffTable[index] > shift)
                suffTable[index] = shift;
        }
        return suffTable;
    }
    private String[] reverse(String[] array){ // переворячивает массиф проффиксов
        for (int i=0; i<array.length/2; ++i){
            String tmp = array[i];
            array[i] = array[array.length-i-1];
            array[array.length-i-1] = tmp;
        }
        return array;
    }

    private int[] getStopTable(String[] P){ // заполнение стоп-таблицы
        int[] stopTable = new int[95];
        for (int i=0; i<95; ++i)
            stopTable[i]=-1;
        for (int i=0; i<P.length-1; ++i) {
            //System.out.println(TranslateToInt(P[i]));
            stopTable[TranslateToInt(P[i]) - 32] = i;
        }
        return stopTable;
    }

    // алгоритм Рабина-Карпа // по модулю от хеша
    public ArrayList<Integer> RabinCarp(String orig, String find) {

        ArrayList<Integer> answer = new ArrayList<>();

        String[] origMas = orig.split("");         // переводим строку в массив срок по символу в 1й ячейке
        String[] findMas = find.split("");
        int d = 127, q = 7; // размер ASCII java // простое число, по модулю которого дальше будем производить расчеты
        int n = origMas.length, m = findMas.length;

        if (findMas.length == 0) {
            System.out.println("В подстроке, которую вы хотите найти, нет символов! Исправьте это.");
            answer.add(-5);
            return answer;
        }

        int HeshP = 0, HeshOrig = 0;

        for (int i = 0; i < m; ++i) {
            HeshP = (d * HeshP + TranslateToInt(findMas[i])) % q;
            HeshOrig = (d * HeshOrig + TranslateToInt(origMas[i])) % q;
        }
        for (int i = 0; i <= n - m; ++i) {
            if (HeshP == HeshOrig) {
                int tmp = 0;
                boolean marker = true;
                for (int j = 0; j < m; ++j)
                    if (findMas[j].equals(origMas[i + j]) != true)
                        marker = false;
                if (marker)
                    answer.add(i+1);
            }
            if (i < n - m)
                HeshOrig = (d *(HeshOrig-d*TranslateToInt(origMas[i]))+TranslateToInt(origMas[i+m])) % q;
        }
        return answer;
    }

    // алгоритм Кнута-Морриса-Пратта
    public ArrayList<Integer> KnuthMorrisPratt (String orig, String find){
        ArrayList<Integer> answer = new ArrayList<>();
        String[] origMas = orig.split("");
        String[] findMas = find.split("");
        int[] PrefP = new int[findMas.length];
        PrefP = PreffFunc(findMas, PrefP);
        int k = 0;

        for (int i=0; i<origMas.length-1; ++i){
            while ((k>0) && (!findMas[k].equals(origMas[i])))
                k = PrefP[k-1];
            if (findMas[k].equals(origMas[i]))
                ++k;
            if (k == findMas.length){
                answer.add(i - findMas.length+2);
                k = PrefP[k-1];
            }
        }
        return answer;
    }

    private int TranslateToInt (String orig){
        int answer = -5;
        char c = orig.charAt(0);
        answer = (int) c;
        return answer;
    }
}
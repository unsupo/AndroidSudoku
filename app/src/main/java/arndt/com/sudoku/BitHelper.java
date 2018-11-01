package arndt.com.sudoku;

import java.math.BigInteger;

public class BitHelper {

    public static String fill(String v, int size){
        if(v.length() == size)
            return v;
        StringBuilder vBuilder = new StringBuilder(v);
        while (vBuilder.length()<size)
            vBuilder.insert(0, 0);
        v = vBuilder.toString();
        return v;
    }

    public static void printLong(long v, int size){
        String s = fill(Long.toBinaryString(v),size), ss = "";
        for (int i = 0; i < s.toCharArray().length; i++) {
            ss += s.toCharArray()[i];
            if((i+1)%9==0)ss += "\n";
        }
        System.out.println(ss);
    }

    public static long parseLong(String s) {
        return parseLong(s,2);
    }public static long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }
}
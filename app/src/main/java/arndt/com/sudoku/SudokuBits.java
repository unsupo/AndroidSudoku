package arndt.com.sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuBits {
    public static void main(String[] args) throws Exception {
    }

    public static int[] getRandom(int count){
        char[] s = solve(new SudokuBits()).toString().replace("\n","").toCharArray();
        count=s.length-count;
        List<Integer> notDone = new ArrayList<>();
        for (int i = 0; i < s.length; i++)
            if(s[i]!='-')
                notDone.add(i);
        Collections.shuffle(notDone);
        for (int i = 0; i < count; i++)
            s[notDone.get(i)]='-';
        int[] ss = new int[s.length];
        for (int i = 0; i < s.length; i++)
            ss[i]=s[i]=='-'?-1:Integer.parseInt(s[i]+"");
        return ss;
    }

    /**
     * This is incorrect method (maybe)
     * @param count
     * @return
     */
    public static int[] getRandomIncorect(int count){
        Random r = new Random();
        SudokuBits bits = new SudokuBits();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 9; i++) integers.add(i+1);
        int[] values = new int[size];
        while (count > 0){
            int pos = r.nextInt(size);
            Collections.shuffle(integers);
            int i = 0;
            Integer num = integers.get(i++);
            boolean isValid = true;
            while (!bits.isValid(num,pos))
                if(i>=integers.size()) {
                    isValid = false;
                    break;
                }else num = integers.get(i++);
            if(isValid) {
                bits.putNumber(num, pos);
                values[pos] = num;
                count--;
            }
        }
        return values;
    }

    private static BigInteger
            horizontal   = new BigInteger("111111111000000000000000000000000000000000000000000000000000000000000000000000000",2),
            vertical     = new BigInteger("100000000100000000100000000100000000100000000100000000100000000100000000100000000",2),
            square       = new BigInteger("111000000111000000111000000000000000000000000000000000000000000000000000000000000",2);
    private static String position = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private static int lsize = 9, size = lsize*lsize;

    private int[] startingNumbers = new int[size];
    private BigInteger[] numbers = new BigInteger[lsize];

    public SudokuBits(){
        for (int i = 0; i < this.numbers.length; i++) {
            this.numbers[i]=BigInteger.ZERO;
        }
    }

    public SudokuBits(int[] startingNumbers, BigInteger[] numbers) {
        this.startingNumbers = startingNumbers;
        this.numbers = numbers;
    }

    public SudokuBits(int[] startingNumbers){
        if(startingNumbers.length != size) throw new IllegalArgumentException("Invalid length");
        this.startingNumbers = startingNumbers;
        String[] bits = new String[numbers.length];
        for (int i = 0; i < bits.length; i++)
            bits[i]="";
        for (int i = 0; i < startingNumbers.length; i++)
            for (int j = 0; j < bits.length; j++)
                if((j+1)==startingNumbers[i])
                    bits[j]+="1";
                else
                    bits[j]+="0";
        for (int i = 0; i < bits.length; i++)
            numbers[i] = new BigInteger(bits[i],2);
    }

    private boolean checker(BigInteger a, BigInteger b, BigInteger checker, int size, int osize){
        BigInteger s = checker;
        for (int i = 0; i < osize; i++){
            //if a is in this check (row/column/square)
            if(a.and(s).equals(a)) { //no need for a second loop because this is the test row
                //and if b has a number in this row then invalid item
                if (!b.and(s).equals(BigInteger.ZERO))
                    return false;
                return true;
            }
            s = s.shiftRight(size);
        }
        return true;
    }
    private BigInteger getNumAtPos(int pos){
        char[] c = position.toCharArray();
        c[pos]='1';
        return new BigInteger(String.valueOf(c),2);
    }

    public boolean isValid(int num, int pos){
        BigInteger numpos = numbers[num-1];
        BigInteger number = getNumAtPos(pos);
        for (int i = 0; i < numbers.length; i++)
            if(!numbers[i].equals(BigInteger.ZERO) && number.and(numbers[i]).equals(numbers[i]))
                return false;
        //check horizontal
        if(!checker(number,numpos,horizontal,lsize,lsize))
            return false;
        //check vertical
        if(!checker(number,numpos,vertical,1,lsize))
            return false;
        //check square
        if(!checker(number,numpos,square,3,3))
            return false;
        if(!checker(number,numpos,square.shiftRight(9*3),3,3))
            return false;
        if(!checker(number,numpos,square.shiftRight(9*6),3,3))
            return false;
        return true;
    }

    private BigInteger orAll() {
        BigInteger b = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            b = b.or(numbers[i]);
        return b;
    }

    @Override
    public SudokuBits clone(){
        return new SudokuBits(startingNumbers,numbers.clone());
    }

    @Override
    public String toString() {
        String[] b = new String[this.numbers.length];
        for (int i = 0; i < this.numbers.length; i++)
            b[i]=BitHelper.fill(this.numbers[i].toString(2),size);
        String f = "";
        for (int j = 0; j < b[0].toCharArray().length; j++) {
            boolean isSomething = false;
            for (int i = 0; i < b.length; i++)
                if (b[i].toCharArray()[j] == '1'){
                    f += "" + (i + 1);
                    isSomething = true;
                }
            if(!isSomething) f+="-";
            if((j+1)%lsize==0) {
                f += "\n";
            }
        }
        return f;
    }


    /**
     *  Static methods
     */
    //driver
    public static SudokuBits solve(SudokuBits sb){
        return _diffSolve(sb,getAllowedOptions(sb));
    }

    public static List<Pair<Integer, List<Integer>>> getAllowedOptions(SudokuBits sb){
        //get all places without a number
        char[] c = BitHelper.fill(sb.orAll().toString(2),size).toCharArray();
        // get all allowed moves
        List<Pair<Integer,List<Integer>>> optionCount = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            if (c[i] != '0') continue;
            else {
                Pair<Integer, List<Integer>> p = new Pair<>(i, new ArrayList<>());
                for (int j = 1; j <= 9; j++)
                    if (sb.isValid(j, i))
                        p.end.add(j);

                optionCount.add(p);
            }
        }
        Collections.sort(optionCount, (a,b)->new Integer(a.end.size()).compareTo(b.end.size()));
        return optionCount;
    }
    public static List<Pair<Integer, List<Integer>>> getAllowedOptionsSubset(SudokuBits sb, List<Pair<Integer, List<Integer>>> subset){
        // get all allowed moves
        List<Pair<Integer, List<Integer>>> subsetClone = new ArrayList<>();
        for (Pair<Integer, List<Integer>> p : subset) {
            List<Integer> allowed = new ArrayList<>();
            for (Integer i : p.end)
                if (sb.isValid(i, p.start))
                    allowed.add(i);
            Pair<Integer, List<Integer>> pp = new Pair<>(p.start, allowed);
            subsetClone.add(pp);
        }
        Collections.sort(subsetClone, (a,b)->new Integer(a.end.size()).compareTo(b.end.size()));
        return subsetClone;
    }
    public static SudokuBits diffSolve(SudokuBits sb){
        return _diffSolve(sb,getAllowedOptions(sb));
    }

    private static SudokuBits _diffSolve(SudokuBits sb, List<Pair<Integer, List<Integer>>> optionCount) {
        for(Pair<Integer, List<Integer>> s : optionCount){
            for(Integer i : s.end) {
                SudokuBits clone = sb.clone();
                clone.putNumber(i , s.start);
                ArrayList<Pair<Integer, List<Integer>>> temp = new ArrayList<>(optionCount);
                temp.remove(s);
                List<Pair<Integer, List<Integer>>> options = getAllowedOptions(clone);//getAllowedOptionsSubset(clone,temp);//getAllowedOptions(clone);
                List<Pair<Integer, List<Integer>>> remove = new ArrayList<>();
                boolean noOptions = false;
                for(Pair<Integer, List<Integer>> t : options){
                    if(t.end.size()==0) {
                        noOptions=true;
                        break;//remove.add(t);
                    }
                    if(t.end.size() > 1 || t.end.size() == 0)
                        break;
                    clone.putNumber(t.end.get(0), t.start);
                    remove.add(t);
                }
                if(noOptions)break;
                options.removeAll(remove);
                SudokuBits ss = _diffSolve(clone, options);
                if(ss.orAll().bitCount()==size)
                    return ss;
            }
        }
        return sb;
    }


    public void putNumber(int num, int pos) {
        numbers[num-1]=numbers[num-1].or(getNumAtPos(pos));
    }

    public static void print(SudokuBits sb){
        String[] b = new String[sb.numbers.length];
        for (int i = 0; i < sb.numbers.length; i++)
            b[i]=BitHelper.fill(sb.numbers[i].toString(2),size);
        String f = "";
        for (int j = 0; j < b[0].toCharArray().length; j++) {
            boolean isSomething = false;
            for (int i = 0; i < b.length; i++)
                if (b[i].toCharArray()[j] == '1'){
                    f += "" + (i + 1);
                    isSomething = true;
                }
            if(!isSomething) f+="-";
            if((j+1)%3==0) f+="|";
            if((j+1)%lsize==0) {
                f += "\n";
                if((j+1)%(3*9)==0) {
                    for (int i = 0; i < lsize + 3; i++)
                        f += "-";
                    f += "\n";
                }
            }
        }
//        return f;
        System.out.println(f);
    }


    private static void getAllPositions(){
        int r = (int) Math.sqrt(lsize);
        String h = "", v = "", s = "";
        for (int i = 0; i < size; i++) {
            //horizontal
            if(i<lsize) h+="1";
            else h+="0";
            //vertical
            if(i==0 || i%lsize==0) v+="1";
            else v+="0";
            //square
            if(i<r || (i>=lsize && i<lsize+r) || (i>=lsize*2 && i<lsize*2+r)) s+="1";
            else s+="0";
        }
        String f = "new BigInteger(\"%s\",2);";
        System.out.println(String.format(f,h));
        System.out.println(String.format(f,v));
        System.out.println(String.format(f,s));
    }
}

package arndt.com.sudoku;

import java.util.*;

public class Sudoku {

    public static void main(String[] args) {
        Sudoku s = new Sudoku(new int[]{
                0, -1, -1,  0, -1, -1, -1,  1,  2,
                0, -1, -1, -1, -1, -1, -1, -1,  3,
                0, -1,  2,  3, -1, -1,  4, -1, -1,
                0, -1,  1,  8, -1, -1, -1, -1,  5,
                0,  6, -1, -1,  7, -1,  8, -1, -1,
                0, -1, -1,  0, -1,  9, -1, -1,  0,
                0, -1,  8,  5, -1, -1, -1, -1, -1,
                9, -1, -1, -1,  4, -1,  5, -1, -1,
                4,  7, -1, -1, -1,  6, -1,  0, -1
        });
        System.out.println(s);
//        getPossibles(s).forEach(a-> System.out.println(String.format("(%s,%s)=%s", a.start.row, a.start.column, a.end)));
        System.out.println(solve(s));
//        List<Number> numbers = Arrays.asList(s.allNumbers);
//        Collections.sort(numbers, Comparator.comparingInt(a -> a.getPossibles()[9]));
//        numbers.forEach(a-> System.out.println(String.format("(%s,%s) = %s", a.row, a.column, a.getPossibles()[9])));
    }

    private static int sqrSize=3,rowSize=sqrSize*sqrSize,size=rowSize*rowSize;
    private Number[] allNumbers = new Number[size];
    private int[][] rows = new int[rowSize][rowSize], cols = new int[rowSize][rowSize],
            sqrs = new int[rowSize][rowSize]; //{r,c,s}[position][number 1,9 if it exists]

    public Sudoku(){
        setup(new int[size]);
    }

    public Sudoku(int[] ints) {
        setup(ints);
    }
    private void setup(int[] ints){
        int row=0,col=0,sqr=0, ext = 0;
        for (int i = 0; i < ints.length; i++) {
            ints[i]=ints[i]<0?0:ints[i];
            allNumbers[i]=new Number(i,sqr,row,col,ints[i]);
            if(ints[i] > 0) {
                rows[row][ints[i] - 1] = 1;
                cols[col][ints[i] - 1] = 1;
                sqrs[sqr][ints[i] - 1] = 1;
            }
            col=(col+1)%rowSize;
            if((i+1)%rowSize==0){
                row++;
                if(row%sqrSize==0)
                    ext+=2;
            }
            sqr=row/sqrSize+col/sqrSize+ext;
        }
    }

    public void putNumber(int num, int pos){
        allNumbers[pos].setNumber(num);
    }

    public int[] getInts() {
        int[] ints = new int[allNumbers.length];
        for (int i = 0; i < allNumbers.length; i++)
            ints[i]=allNumbers[i].number;
        return ints;
    }

    //probably shouldn't ever use this
    @Override
    public Sudoku clone(){
        return new Sudoku(getInts());
    }

    public String formatter(){
        //build the horizontal seperator
        String sep = "";
        for (int j = 0; j < rowSize + 3; j++)
            sep += "-";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < allNumbers.length; i++) {
            if(allNumbers[i].number<=0) b.append("%s");
            else b.append("%s");
            if((i+1)%3==0) b.append("|");
            if((i+1)%rowSize==0) {
                b.append("\n");
                if((i+1)%(3*9)==0)
                    b.append(sep+"\n");
            }
        }
        return b.toString();
    }
    public String androidFormatter(){
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < allNumbers.length; i++) {
            if(allNumbers[i].number<=0) b.append("%s");
            else b.append("%s");
            if((i+1)%rowSize==0)
                b.append("\n");
        }
        return b.toString();
    }

    @Override
    public String toString() {
//        return String.format(formatter(), allNumbers);
        return String.format(androidFormatter(), allNumbers);
    }

    // driver
    public static Sudoku solve(Sudoku s){
        return _solve(s, getPossibles(s));
    }private static Sudoku _solve(Sudoku s, List<Pair<Number, List<Integer>>> possibles){
        for(Pair<Number, List<Integer>> pair : possibles){
            boolean isAllowed = false;
            for(Integer num : pair.end){
                Sudoku clone = s.clone();
                // set num at pair.start.row and pair.start.column
                try {
                    clone.putNumber(num, pair.start.position);
                }catch (IllegalArgumentException e){
                    clone.putNumber(0, pair.start.position);
                    continue;
                }
                List<Pair<Number, List<Integer>>> currentPos = getPossibles(s);
                if(currentPos == null)
                    return null;
                List<Pair<Number, List<Integer>>> remove = new ArrayList<>();
                boolean legal = true;
                for(Pair<Number, List<Integer>> currentPair : currentPos) {
                    if(currentPair.end.size() == 1) {
                        remove.add(currentPair);
                        try {
                            clone.putNumber(currentPair.end.get(0), currentPair.start.position);
                        }catch (IllegalArgumentException e){
                            legal = false;
                            break;
                        }
                    }else break;
                }
                currentPos.removeAll(remove);
                if(legal) {
                    isAllowed = true;
                    Sudoku v = _solve(clone, currentPos);
                    if(v==null)
                        isAllowed=false;
                    if (v != null)
                        return v;
                }
            }
            if(!isAllowed)
                return null;
        }
        return s;
    }private static List<Pair<Number, List<Integer>>> getPossibles(Sudoku s){
        // sort by least number of possibilities
        List<Pair<Number,List<Integer>>> possibles = new ArrayList<>();
        for (int i = 0; i < s.allNumbers.length; i++) {
            List<Integer> p = s.allNumbers[i].getPossibles();
            if(p == null) return null;
            if(p.size() == 0) continue;
            Collections.shuffle(p);
            possibles.add(new Pair<>(s.allNumbers[i], p));
        }
        Collections.sort(possibles, Comparator.comparingInt(a -> a.end.size()));
        return possibles;
    }

    public static int[] getRandom(int count){
        int[] s = solve(new Sudoku()).getInts();
        count=size-count;
        List<Integer> notDone = new ArrayList<>();
        for (int i = 0; i < s.length; i++)
            notDone.add(i);
        Collections.shuffle(notDone);
        for (int i = 0; i < count; i++)
            s[notDone.get(i)]=0;
        return s;
    }

    private class Number{
        int square, row, column, // references to it's r,c,s
                number, position;

        public Number(int position, int square, int row, int column, int number) {
            this.position = position;
            this.square = square;
            this.row = row;
            this.column = column;
            this.number = number;
        }

        public List<Integer> getPossibles(){
            //last value is count
            List<Integer> possibles = new ArrayList<>();
            if(number > 0) return possibles;
            for (int i = 0; i < sqrs[square].length; i++)
                if(sqrs[square][i]==0 && rows[row][i]==0 && cols[column][i]==0)
                    possibles.add(i+1);
            return possibles.size() == 0 ? null : possibles;
        }

        public void setNumber(int number) {
            if(this.number > 0) {
                // unset row, column, square
                rows[row][this.number - 1] = 0;
                cols[column][this.number - 1] = 0;
                sqrs[square][this.number - 1] = 0;
            }
            this.number = number > 0 ? number : 0;
            if(this.number > 0) {
                // set row, column, square
                if(rows[row][this.number - 1] == 1)
                    throw new IllegalArgumentException("Invalid Move");
                if(cols[column][this.number - 1] == 1)
                    throw new IllegalArgumentException("Invalid Move");
                if(sqrs[square][this.number - 1] == 1)
                    throw new IllegalArgumentException("Invalid Move");
                rows[row][this.number - 1] = 1;
                cols[column][this.number - 1] = 1;
                sqrs[square][this.number - 1] = 1;
            }
        }

        @Override
        public String toString() {
            return number <= 0 ? "-" : number+"";
        }

        @Override
        public Number clone(){
            return new Number(position, square,row,column,number);
        }
    }
}

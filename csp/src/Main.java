import java.util.*;

class Cell implements Comparable<Cell> {
    private int number;
    private String color;
    private ArrayList<Cell> conflictSet;
    private ArrayList<Integer> numberDomain;
    private ArrayList<String> colorDomain;
    private boolean assign;
    private final int row;
    private final int column;
    private final int degree;

    public Cell(int number, String color,ArrayList<String> colorDomain, ArrayList<Integer> numberDomain,int row,int column,int n){
        this.number = number;
        this.color = color;
        assign = false;
        this.colorDomain = colorDomain;
        this.numberDomain = numberDomain;
        conflictSet = new ArrayList<>();
        this.row = row;
        this.column = column;
        if((row == 0 && column==0) || (row == n-1 && column==n-1) || (row == n-1 && column==0) ||(row == 0 && column==n-1))
            this.degree = ((2 * n)-2) + 2;
        else if(row ==0 || column == 0 || row==n-1 || column==n-1)
            this.degree = ((2 *n)-2) + 3;
        else
            this.degree = ((2 *n)-2) + 4;
    }

    public void setAssign(boolean assign) {
        this.assign = assign;
    }

    public boolean isAssign() {
        return assign;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setConflictSet(Cell newCell) {
        this.conflictSet.add(newCell);
    }

    public void removeNumberDomain(Integer number) {
        this.numberDomain.remove(number);
    }

    public void removeColorDomain(String color) {
        this.colorDomain.remove(color);
    }

    public int getNumber() {
        return number;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Cell> getConflictSet() {
        return conflictSet;
    }

    public ArrayList<Integer> getNumberDomain() {
        return numberDomain;
    }

    public ArrayList<String> getColorDomain() {
        return colorDomain;
    }

    public int getDegree() {
        return degree;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int compareTo(Cell o) {
        if(o.colorDomain.size() + o.numberDomain.size() > this.colorDomain.size()+this.numberDomain.size())
            return -1;
        else if(o.colorDomain.size() + o.numberDomain.size() < this.colorDomain.size()+this.numberDomain.size())
            return 1;
        else if(o.degree > this.degree)
            return 1;
        else
            return -1;
    }

}
class Table {
    private Cell[][] table;
    ArrayList<Cell> MRV;
    private final int n;

    public Table(Cell[][] table, int n) {
        this.table = table;
        this.n = n;
        MRV = new ArrayList<>();
        starterForwardChecking();
        for(int i=0;i<n;i++){
            MRV.addAll(Arrays.asList(table[i]).subList(0, n));
        }
        findDegreesAndMRV();
    }
    private void findDegreesAndMRV(){
        Collections.sort(this.MRV);
    }

    private void forwardChecking(Cell cell, int i , int j){
        if(table[i][j].isAssign())
            return;
        for(int k=0; k<n; k++){
            if(k!=j) {
                table[i][k].removeNumberDomain(cell.getNumber());
            }
        }
        for(int k=0; k<n; k++){
            if(k!=i) {
                table[k][j].removeNumberDomain(cell.getNumber());
            }
        }
        if(i!=0)
            table[i-1][j].removeColorDomain(cell.getColor());
        if(i!=n-1)
            table[i+1][j].removeColorDomain(cell.getColor());
        if(j!=0)
            table[i][j-1].removeColorDomain(cell.getColor());
        if(j!=n-1)
            table[i][j+1].removeColorDomain(cell.getColor());
    }
    public void starterForwardChecking(){
        for(int i=0; i<n; i++){
            for(int j=0;j<n; j++){
                if(table[i][j].getNumber()!=0){
                    forwardChecking(table[i][j],i,j);
                }
                else if(!table[i][j].getColor().equals("")){
                    forwardChecking(table[i][j],i,j);
                }
            }
        }
    }

    public void printTable() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.println(table[i][j].getNumber() + table[i][j].getColor() + ": " + table[i][j].isAssign());
                System.out.println("color Domain");
                for(String s  : table[i][j].getColorDomain()){
                    System.out.print(s+"\t");
                }
                System.out.println();
                System.out.println("Number Domain");
                for(Integer n : table[i][j].getNumberDomain()){
                    System.out.print(n+"\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    public void backTrack(){

    }
    private boolean isSafeAssignment(Cell  cell){
        
        return true;
    }
    public void solve() {
        for (int i = 0; i < n * n; i++) {
            Cell c = MRV.get(i);
            System.out.println("row: " + c.getRow() + "column: " + c.getColumn());
            System.out.println("Domain");
            System.out.println(c.getColorDomain().size() + c.getNumberDomain().size());
            System.out.println("degree: " + c.getDegree());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n,m;
        m = sc.nextInt();
        ArrayList<String> colors = new ArrayList<>(m);
        String[] cells;
        n = sc.nextInt();
        ArrayList<Integer> numbersD = new ArrayList<>(n);
        for(int i=1;i<=n;i++){
            numbersD.add(i);
        }
        sc.nextLine();
        Cell[][] table = new Cell[n][n];
        for(int k=0;k<m;k++){
            colors.add(sc.next());
        }
        sc.nextLine();
        for(int i=0; i<n; i++){
                String line = sc.nextLine();
                cells = line.split(" ");
                int j=0;
                for (String  cell : cells) {
                    String numberS = cell.substring(0, cell.length()-1);
                    int number = 0;
                    if(!numberS.equals("*")){
                        number = Integer.parseInt(numberS);
                    }
                    String color = cell.substring(cell.length()-1);
                    if(color.equals("#"))
                        color = "";
                    Cell newCell = new Cell(number,color,(ArrayList<String>)colors.clone(),(ArrayList<Integer>)numbersD.clone(),i,j,n);
                    if(number!=0 && !color.equals(""))
                        newCell.setAssign(true);
                    table[i][j] = newCell;
                    j++;
                }
            }
        Table mainTable = new Table(table,n);
        mainTable.solve();
//        mainTable.printTable();
    }
}

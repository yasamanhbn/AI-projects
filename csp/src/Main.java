import java.util.*;

class Cell implements Comparable<Cell> {
    private int number;
    private String color;
    private final ArrayList<Integer> numberDomain;
    private final ArrayList<String> colorDomain;
    private boolean assign;
    private final int row;
    private final int column;
    private int degree;

    public Cell(int number, String color, ArrayList<String> colorDomain, ArrayList<Integer> numberDomain, int row, int column, int n, boolean assign) {
        this.number = number;
        this.color = color;
        this.assign = assign;
        this.colorDomain = colorDomain;
        this.numberDomain = numberDomain;
        this.row = row;
        this.column = column;
        if ((row == 0 && column == 0) || (row == n - 1 && column == n - 1) || (row == n - 1 && column == 0) || (row == 0 && column == n - 1))
            this.degree = ((2 * n) - 2) + 2;
        else if (row == 0 || column == 0 || row == n - 1 || column == n - 1)
            this.degree = ((2 * n) - 2) + 3;
        else
            this.degree = ((2 * n) - 2) + 4;
    }
    public void setDegree(){
        this.degree--;

    }
    public void setAssign(boolean assign) {
        this.assign = assign;
    }

    public int getDegree() {
        return degree;
    }

    public boolean isAssign() {
        return assign;
    }
    public void setNumber(int number) { this.number = number; }
    public void setColor(String color) { this.color = color; }
    public void removeNumberDomain(Integer number) { this.numberDomain.remove(number); }
    public void addNumber(Integer i) { this.numberDomain.add(i); }
    public void addColor(String s) { this.colorDomain.add(s); }
    public void removeColorDomain(String color) { this.colorDomain.remove(color); }
    public int getNumber() {return number; }
    public String getColor() {return color; }
    public ArrayList<Integer> getNumberDomain() { return numberDomain; }
    public ArrayList<String> getColorDomain() { return colorDomain; }
    public int getRow() { return row; }
    public int getColumn() { return column; }

    @Override
    public int compareTo(Cell o) {
        if(o==this)
            return 0;
        if (o.colorDomain.size() + o.numberDomain.size() > this.colorDomain.size() + this.numberDomain.size())
            return -1;
        else if (o.colorDomain.size() + o.numberDomain.size() < this.colorDomain.size() + this.numberDomain.size())
            return 1;
        else if (o.degree > this.degree)
            return 1;
        else if(o.degree < this.degree)
            return -1;
        else
            return 0;
    }
}

class Table {
    private final Cell[][] mainTable;
    private final LinkedList<Cell> MRV;

    public Table(Cell[][] table, LinkedList<Cell> mrv) {
        this.mainTable = table;
        MRV = new LinkedList<>();
        this.MRV.addAll(mrv);
    }

    public void updateMRV() {
        this.MRV.removeAll(this.MRV);
        for (int i = 0; i < mainTable[0].length; i++) {
            MRV.addAll(Arrays.asList(mainTable[i]).subList(0, mainTable[0].length));
        }
    }
    public Cell[][] getMainTable() {
        return mainTable;
    }

    public Cell findDegreesAndMRV() {
        Collections.sort(this.MRV);
        return this.MRV.pop();
    }
    public LinkedList<Cell> getMRV() {
        return MRV;
    }
}

class CSP {
    Table mainTable;
    private final int n;
    ArrayList<String> colors;
    LinkedList<Table> cspList;

    public CSP(Table t, int n, ArrayList<String> colors) {
        this.n = n;
        this.colors = colors;
        mainTable = t;
        starterForwardChecking();
        cspList = new LinkedList<>();
    }

    private Table makeNewTable(Table oldTable) {
        Cell[][] cells = new Cell[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<String> cd = new ArrayList<>(oldTable.getMainTable()[i][j].getColorDomain());
                ArrayList<Integer> num = new ArrayList<>(oldTable.getMainTable()[i][j].getNumberDomain());
                cells[i][j] = new Cell(oldTable.getMainTable()[i][j].getNumber(), oldTable.getMainTable()[i][j].getColor(), cd, num, i, j, n, oldTable.getMainTable()[i][j].isAssign());
            }
        }
        return new Table(cells, oldTable.getMRV());
    }

    public void backTrack() {
        Table firstTable = makeNewTable(mainTable);
        cspList.add(firstTable);
        while (cspList.size() != 0) {
            Table oldTable = cspList.poll();
//            printTable(oldTable.getMainTable());
            Cell currentCell = oldTable.findDegreesAndMRV();
            while (currentCell.isAssign()) {
                currentCell = oldTable.findDegreesAndMRV();
            }
            Cell checkingCell = oldTable.getMainTable()[currentCell.getRow()][currentCell.getColumn()];
            int row = currentCell.getRow();
            int col = currentCell.getColumn();
            if (checkingCell.getNumber() == 0 && checkingCell.getColor().equals("")) {
                for (String color : checkingCell.getColorDomain()) {
                    for (Integer num : checkingCell.getNumberDomain()) {
                        Table newTable = makeNewTable(oldTable);
                        newTable.getMainTable()[row][col].setNumber(num);
                        newTable.getMainTable()[row][col].setColor(color);
                        if (checkStuff(newTable, row, col, 2))
                            return;
                    }
                }
            } else if (checkingCell.getColor().equals("")) {
                for (String color : checkingCell.getColorDomain()) {
                    Table newTable = makeNewTable(oldTable);
                    newTable.getMainTable()[row][col].setColor(color);
                    if (checkStuff(newTable, row, col, 1))
                        return;
                }
            } else if (checkingCell.getNumber() == 0) {
                for (Integer num : checkingCell.getNumberDomain()) {
                    Table newTable = makeNewTable(oldTable);
                    newTable.getMainTable()[row][col].setNumber(num);
                    if (checkStuff(newTable, row, col, 0))
                        return;
                }
            }
        }
        System.out.println("There is no answer");
    }

    private boolean checkStuff(Table newTable, int row, int col, int restore) {
        if (isSafeAssignment(newTable.getMainTable(), newTable.getMainTable()[row][col])) {
            if(forwardChecking(newTable.getMainTable(), newTable.getMainTable()[row][col], row, col)) {
                newTable.getMainTable()[row][col].setAssign(true);
                newTable.updateMRV();
                cspList.push(newTable);
                if (goalChecking(newTable.getMainTable())) {
                    printTable(newTable.getMainTable());
                    return true;
                }

            }
        } else {
            if (restore == 2) {
                newTable.getMainTable()[row][col].setNumber(0);
                newTable.getMainTable()[row][col].setColor("");
            } else if (restore == 1)
                newTable.getMainTable()[row][col].setColor("");
            else
                newTable.getMainTable()[row][col].setNumber(0);
        }
        return false;
    }

    private boolean goalChecking(Cell[][] table) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!table[i][j].isAssign()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafeAssignment(Cell[][] table, Cell cell) {
        if (cell.getNumber() == 0 || cell.getColor().equals(""))
            return false;
        int colorP = colors.indexOf(cell.getColor());
//        notice: color's list priority in ascending
        if (cell.getColumn() - 1 >= 0 && table[cell.getRow()][cell.getColumn() - 1].isAssign()) {
            if (table[cell.getRow()][cell.getColumn() - 1].getNumber() > cell.getNumber() && colors.indexOf(table[cell.getRow()][cell.getColumn() - 1].getColor()) > colorP)
                return false;
            else if (table[cell.getRow()][cell.getColumn() - 1].getNumber() < cell.getNumber() && colors.indexOf(table[cell.getRow()][cell.getColumn() - 1].getColor()) < colorP)
                return false;
        }
        if (cell.getColumn() + 1 <= n - 1 && table[cell.getRow()][cell.getColumn() + 1].isAssign()) {
            if (table[cell.getRow()][cell.getColumn() + 1].getNumber() > cell.getNumber() && colors.indexOf(table[cell.getRow()][cell.getColumn() + 1].getColor()) > colorP)
                return false;
            else if (table[cell.getRow()][cell.getColumn() + 1].getNumber() < cell.getNumber() && colors.indexOf(table[cell.getRow()][cell.getColumn() + 1].getColor()) < colorP)
                return false;
        }
        if (cell.getRow() - 1 >= 0 && table[cell.getRow() - 1][cell.getColumn()].isAssign()) {
            if (table[cell.getRow() - 1][cell.getColumn()].getNumber() > cell.getNumber() && colors.indexOf(table[cell.getRow() - 1][cell.getColumn()].getColor()) > colorP)
                return false;
            else if (table[cell.getRow() - 1][cell.getColumn()].getNumber() < cell.getNumber() && colors.indexOf(table[cell.getRow() - 1][cell.getColumn()].getColor()) < colorP)
                return false;
        }
        if (cell.getRow() + 1 <= n - 1 && table[cell.getRow() + 1][cell.getColumn()].isAssign()) {
            if (table[cell.getRow() + 1][cell.getColumn()].getNumber() > cell.getNumber() && colors.indexOf(table[cell.getRow() + 1][cell.getColumn()].getColor()) > colorP)
                return false;
            else if (table[cell.getRow() + 1][cell.getColumn()].getNumber() < cell.getNumber() && colors.indexOf(table[cell.getRow() + 1][cell.getColumn()].getColor()) < colorP)
                return false;
        }
        return true;
    }

    private boolean forwardChecking(Cell[][] table, Cell cell, int i, int j) {
        for (int k = 0; k < n; k++) {
            if (k != j && !table[i][k].isAssign()) {
                table[i][k].removeNumberDomain(cell.getNumber());
                if(table[i][k].getNumberDomain().size()==0)
                    return false;
                if (table[i][j].getNumber()!=0)
                    table[i][k].setDegree();
            }
        }
        for (int k = 0; k < n; k++) {
            if (k != i && !table[k][j].isAssign()) {
                table[k][j].removeNumberDomain(cell.getNumber());
                if(table[k][j].getNumberDomain().size()==0)
                    return false;
                if (table[i][j].getNumber()!=0)
                    table[i][k].setDegree();
            }
        }
        if (i != 0 && !table[i - 1][j].isAssign()) {
            table[i - 1][j].removeColorDomain(cell.getColor());
            if(table[i - 1][j].getColorDomain().size()==0)
                return false;
            if (!table[i][j].getColor().equals(""))
                table[i-1][j].setDegree();
        }
        if (i != n - 1 && !table[i + 1][j].isAssign()) {
            table[i + 1][j].removeColorDomain(cell.getColor());
            if(table[i + 1][j].getColorDomain().size()==0)
                return false;
            if (!table[i][j].getColor().equals(""))
                table[i+1][j].setDegree();
        }
        if (j != 0 &&!table[i][j - 1].isAssign()) {
            table[i][j - 1].removeColorDomain(cell.getColor());
            if(table[i][j - 1].getColorDomain().size()==0)
                return false;
            if (!table[i][j].getColor().equals(""))
                table[i][j-1].setDegree();
        }
        if (j != n - 1 && !table[i][j + 1].isAssign()) {
            table[i][j + 1].removeColorDomain(cell.getColor());
            if(table[i][j + 1].getColorDomain().size()==0)
                return false;
            if (!table[i][j].getColor().equals(""))
                table[i][j+1].setDegree();
        }
        return true;
    }

    public void starterForwardChecking() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (mainTable.getMainTable()[i][j].getNumber() != 0) {
                    forwardChecking(mainTable.getMainTable(), mainTable.getMainTable()[i][j], i, j);
                } else if (!mainTable.getMainTable()[i][j].getColor().equals("")) {
                    forwardChecking(mainTable.getMainTable(), mainTable.getMainTable()[i][j], i, j);
                }
            }
        }
    }

    public void printTable(Cell[][] table) {
        System.out.println("Goal");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(table[i][j].getNumber() + table[i][j].getColor() + "\t");
            }
            System.out.println();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n, m;
        m = sc.nextInt();
        ArrayList<String> colors = new ArrayList<>(m);
        String[] cells;
        n = sc.nextInt();
        ArrayList<Integer> numbersD = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            numbersD.add(i);
        }
        sc.nextLine();
        Cell[][] table = new Cell[n][n];
        for (int k = 0; k < m; k++) {
            colors.add(sc.next());
        }
        sc.nextLine();
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine();
            cells = line.split(" ");
            int j = 0;
            for (String cell : cells) {
                String numberS = cell.substring(0, cell.length() - 1);
                int number = 0;
                if (!numberS.equals("*")) {
                    number = Integer.parseInt(numberS);
                }
                String color = cell.substring(cell.length() - 1);
                if (color.equals("#"))
                    color = "";
                Cell newCell;
                if (number != 0 && !color.equals("")) {
                    newCell = new Cell(number, color, new ArrayList<>(), new ArrayList<>(), i, j, n, false);
                    newCell.addColor(color);
                    newCell.addNumber(number);
                    newCell.setAssign(true);
                } else if (number != 0) {
                    newCell = new Cell(number, color, (ArrayList<String>) colors.clone(), new ArrayList<>(), i, j, n, false);
                    newCell.addNumber(number);
                } else if (!color.equals("")) {
                    newCell = new Cell(number, color, new ArrayList<>(), (ArrayList<Integer>) numbersD.clone(), i, j, n, false);
                    newCell.addColor(color);
                } else
                    newCell = new Cell(number, color, (ArrayList<String>) colors.clone(), (ArrayList<Integer>) numbersD.clone(), i, j, n, false);
                table[i][j] = newCell;
                j++;
            }
        }
        LinkedList<Cell> MRV = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            MRV.addAll(Arrays.asList(table[i]).subList(0, n));
        }
        Table t = new Table(table, MRV);
        CSP mainTable = new CSP(t, n, colors);
        mainTable.backTrack();
    }
}

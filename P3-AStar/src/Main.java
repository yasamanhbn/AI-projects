import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

class Card{
    private final int number;
    private final String color;
    public Card(int number,String color){
        this.color = color;
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    public String getColor() {
        return color;
    }
    @Override
    public boolean equals(Object o) {
        if(this==o)
            return true;
        Card card = (Card)o;
        return this.number == card.getNumber() && this.color.equals(card.getColor());
    }
}

class Section{
    private final ArrayList<Card> cards;
    public Section(){
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void deleteCard(){
        cards.remove(cards.size()-1);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void showSection(){
        if(cards.size()==0) {
            System.out.println("#");
            return;
        }
        for(Card card : cards){
            System.out.print(card.getNumber()+card.getColor()+"\t");
        }
    }
    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        Section section = (Section)o;
        return section.getCards().equals(this.cards);
    }
}


class State implements Comparable<State>{
    private final ArrayList<Section> sections;
    private final int height;
    private final int cost;
    private int heuristic;
    private final int sum;
    private final State parent;

    private String changedState;
    public State(int height,State parent,int sum,int cost){
        sections = new ArrayList<>();
        this.height = height;
        this.parent = parent;
        this.sum = sum;
        this.cost = cost;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setChangedState(String changedState) {
        this.changedState = changedState;
    }

    public int getHeight() {
        return height;
    }

    public String getChangedState() {
        return changedState;
    }

    public State getParent() {
        return parent;
    }

    public void addSection(Section section){
        sections.add(section);
    }

    public int getSum() {
        return sum;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void showState(){
        System.out.println("new State");
        for(Section section : sections){
            if(section.getCards().size()!=0) {
                section.showSection();
                System.out.println();
            }
            else {
                System.out.println("#");
            }
        }
    }

    public int getHeuristic() {
        return heuristic;
    }

    public int heuristic(){
        ArrayList<Card> cards;
        int counter =0;
        String color;
        int number;
        for(Section section:this.getSections()){
            cards = section.getCards();
            if(cards.size()==0)
                continue;
            if(cards.size()==1){
                counter++;
                continue;
            }
            color = cards.get(0).getColor();
            number = cards.get(0).getNumber();
            for(Card card:cards){
                if(card.getColor().equals(color) && number>=card.getNumber()){
                    counter++;
                    number = card.getNumber();
                }
                else {
                    break;
                }
            }
        }
        return this.sum - counter;
    }

    public int getCost() {
        return cost;
    }
    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        State section = (State) o;
        return section.getSections().equals(this.sections);
    }

    @Override
    public int compareTo(State o) {
        return (this.getHeuristic()+this.getCost()) - (o.getHeuristic()+o.getCost());
    }

}
class Node{
    private final int k;
    private int nodesExpanded =0 ;
    private int nodesCreated = 1;
    PriorityQueue<State> frontier;
    ArrayList<State> explored;
    public Node(State state,int k){
        this.k = k;
        frontier = new PriorityQueue<>();
        explored = new ArrayList<>();
        frontier.offer(state);
    }

    //this method create a new child from his parent
    private State makeNewSate(State state,int i,int j){
        State copyState = new State(state.getHeight()+1,state,state.getSum(),state.getCost()+1);

        ArrayList<Section> sections = (state.getSections());
        for(Section section:sections){
            Section newSection = new Section();
            ArrayList<Card> cards = section.getCards();
            for(Card card:cards){
                Card newCard = new Card(card.getNumber(),card.getColor());
                newSection.addCard(newCard);
            }
            copyState.addSection(newSection);
        }
        ArrayList<Card> temp = sections.get(i).getCards();
        copyState.getSections().get(j).addCard(temp.get(temp.size()-1));
        copyState.getSections().get(i).deleteCard();
        copyState.setChangedState("move "+temp.get(temp.size()-1).getNumber()+temp.get(temp.size()-1).getColor()+" from column "+ (i+1) + " to column "+ (j+1));
        copyState.setHeuristic(copyState.heuristic());
        return copyState;
    }

    public void action(){
        Card selectCard;
        Card currentCard;
        while (this.frontier.size()>0) {
            State currentState = this.frontier.poll();
            nodesExpanded++;
            if(currentState.getHeuristic()==0){
                showResult(currentState);
                return;
            }
            explored.add(currentState);
            for (int i = 0; i < k; i++) {
                if (currentState.getSections().get(i).getCards().size() == 0)
                    continue;
                ArrayList<Card> temp = currentState.getSections().get(i).getCards();
                selectCard = temp.get(temp.size()-1);
                for (int j = 0; j < k; j++) {
                    if (i == j)
                        continue;
                    Section section = currentState.getSections().get(j);
                    if (section.getCards().size() == 0) {
                        checkAndAdd(currentState,i,j);
                    } else {
                        currentCard = section.getCards().get(section.getCards().size()-1);
                        if (selectCard.getNumber() < currentCard.getNumber()) {
                            checkAndAdd(currentState,i,j);
                        }
                    }
                }
            }
        }
    }

    //check explored and create a new node
    private void checkAndAdd(State currentState,int i,int j){
        State newSate;
        State flag = null;
        newSate = this.makeNewSate(currentState, i, j);
        for (State s : explored) {
            if (s.equals(newSate)) {
                return;
            }
        }
        for(State s: frontier){
            if (s.equals(newSate)) {
                if(s.getHeuristic()+s.getCost()<=newSate.getHeuristic()+s.getCost()) {
                    return;
                }
                else
                    flag = s;
            }
        }
        if(flag!=null){
            frontier.remove(flag);
        }
        frontier.offer(newSate);
        nodesCreated++;
    }
    private void showResult(State currentState){
        State tmp = currentState;
        System.out.println("find");
        currentState.showState();
        System.out.println("depth: "+ currentState.getHeight());
        System.out.println("steps");
        ArrayList<String> steps = new ArrayList<>();
        while(tmp.getParent()!=null){
            steps.add(tmp.getChangedState());
//            System.out.println(tmp.getHeuristic());
//            System.out.println(tmp.getCost());
//            tmp.showState();
            tmp = tmp.getParent();
        }
        for(int j = steps.size()-1;j>=0;j--){
            System.out.println(steps.get(j));
        }
        System.out.println("node created: "+this.nodesCreated);
        System.out.println("node expanded: "+this.nodesExpanded);
    }

}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String cardLine;
        String[] cards;
        int k = sc.nextInt();
//        color count
        int  m = sc.nextInt();
//        number count
        int n = sc.nextInt();
        sc.nextLine();
        Section[] section = new Section[k];
        State state = new State(0,null,n*m,1);
        state.setChangedState("first state");
        for(int i=0; i<k; i++){
            section[i] = new Section();
            cardLine = sc.nextLine();
            if(cardLine.equals("#")){
                section[i] = new Section();
                state.addSection(section[i]);
                continue;
            }
            cards = cardLine.split(" ");
            for (String card : cards) {
                int number = Integer.parseInt(card.substring(0, card.length()-1));
                String color = card.substring(card.length()-1,card.length());
                Card newCard = new Card(number, color);
                section[i].addCard(newCard);
            }
            state.addSection(section[i]);
        }
            state.setHeuristic(state.heuristic());
            Node node = new Node(state, k);
            node.action();
    }
}

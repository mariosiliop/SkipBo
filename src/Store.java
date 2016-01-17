import java.util.ArrayList;

// A Store is a group of stacks of cards
// Programmatically, our class stores
// an ArrayList of CardStack object references
public class Store {

    public ArrayList<CardStack> stacks;

    Store(int numberOfStacks){

        stacks = new ArrayList<>();

        int x = 0;

        while (numberOfStacks > x) {

            stacks.add(new CardStack());

            x++;

        }

    }

    // Returns the CardStack located
    // in the specified slot
    public CardStack get(int index){
        return stacks.get(index);
    }

}

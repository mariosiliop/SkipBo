import java.util.ArrayList;

/**
 * Created by kille on 1/5/2016.
 */
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


    public CardStack get(int index){
        return stacks.get(index);
    }

}

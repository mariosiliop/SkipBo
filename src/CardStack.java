import java.util.ArrayList;

/**
 * Created by kille on 1/5/2016.
 */
public class CardStack {

    public ArrayList<Card> cards;
    private ArrayList<VisualCardStack> _onchange;

    CardStack(){

        cards = new ArrayList<>();
        _onchange = new ArrayList<>();

    }

    public void onchange(VisualCardStack cardStack){
        _onchange.add(cardStack);
    }

    //pull over card
    public Card getTop(){
        if(cards.size() - 1 != -1)
            return cards.get(cards.size() - 1);
        else
            return null;
    }

    public void push(ArrayList<Card> cardsRef){

        cards.addAll(cardsRef);
        updateSubscribers();

    }

    //Take specific number of cards
    public ArrayList<Card> splice(int index, int amount){

        ArrayList<Card> returned = new ArrayList<>();

        for(int i = 0; i < amount; i++){

            returned.add(cards.get(index));
            cards.remove(index);

        }

        updateSubscribers();

        return returned;


    }

    public void updateSubscribers(){

        for(int i =0 ; i < _onchange.size(); i++)
            _onchange.get(i).updateText();

    }

    public int length(){
        return cards.size();
    }

    //take card with specific index
    public Card get(int index){
        if(index >= cards.size())
            return null;
        else
            return cards.get(index);
    }


    public ArrayList<Card> pop(int quantity, int specificIndex, boolean useSpecificIndex){

        ArrayList<Card> spliced;

        int index = cards.size() - quantity;

        if (index < 0) index = 0;

        if(useSpecificIndex)
            index = specificIndex;

        spliced = splice(index, quantity);

        updateSubscribers();

        return spliced;

    }


}

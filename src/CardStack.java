import java.util.ArrayList;

// The CardStack class stores ArrayLists of Cards
// and provides custom methods to manipulate them
public class CardStack {

    public ArrayList<Card> cards;
    private ArrayList<VisualCardStack> _onchange;

    CardStack(){
        
        // Main Card storage
        cards = new ArrayList<>();
        
        // Storage of VisualCardStack references
        // that will be used to update text
        _onchange = new ArrayList<>();

    }

    // Dynamically pushing subscribers of the custom
    // change pseudo-event
    public void onchange(VisualCardStack cardStack){
        _onchange.add(cardStack);
    }

    // Return the last card of the array (or the 'top'
    // of the deck) or null
    public Card getTop(){
        if(cards.size() - 1 != -1)
            return cards.get(cards.size() - 1);
        else
            return null;
    }

    // Append Card objects to our cards ArrayList
    public void push(ArrayList<Card> cardsRef){

        cards.addAll(cardsRef);
        updateSubscribers();

    }

    // Extract a range of Cards from storage and return them
    // after updating the frame with updateSubscribers
    public ArrayList<Card> splice(int index, int amount){

        ArrayList<Card> returned = new ArrayList<>();

        for(int i = 0; i < amount; i++){

            returned.add(cards.get(index));
            cards.remove(index);

        }

        updateSubscribers();

        return returned;


    }

    // Used to update the visual objects that
    // have declared interest to the changes of this
    // stack's contents
    public void updateSubscribers(){

        for(int i =0 ; i < _onchange.size(); i++)
            _onchange.get(i).updateText();

    }

    // Safely returns the amount of cards stored
    // in this stack
    public int length(){
        return cards.size();
    }

    // Returns a reference of a Card at a specific 
    // slot in our stack or null
    public Card get(int index){
        if(index >= cards.size())
            return null;
        else
            return cards.get(index);
    }

    // Extracts and returns the last card of our stack or
    // a range of cards
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

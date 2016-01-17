// A class that describes every card
// of our game
public class Card {

    public String id;
    public String color;
    public String value;

    Card(String idRef, String colorRef){

        id = idRef;
        color = colorRef;

        value = id;

    }
    
    // Dynamically sets the card's value
    // if needed
    public void setValue(String valueRef){
        value = valueRef;
    }


}

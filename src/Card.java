/**
 * Created by kille on 1/5/2016.
 */
public class Card {

    public String id;
    public String color;
    public String value;

    Card(String idRef, String colorRef){

        id = idRef;
        color = colorRef;

        value = id;

    }

    public void setValue(String valueRef){
        value = valueRef;
    }


}

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kille on 1/5/2016.
 */
public class VisualCardStack{

    public Game game;
    public Player owner;
    public CardStack stack;
    public int specificIndex;
    public String type;
    public JLabel element;
    public boolean useSpecificIndex;


    VisualCardStack(CardStack stackRef, int specificIndexRef, Player ownerPlayer, String types, Game gameRef, boolean useSpecificIndexRef){

        owner = ownerPlayer;
        game = gameRef;
        useSpecificIndex = useSpecificIndexRef;

        specificIndex = specificIndexRef;

        stack = stackRef;
        type = types;

        stack.onchange(this);

        createElement();

    }

    public void createElement(){

        element = new JLabel("", JLabel.CENTER);
        element.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        element.setPreferredSize(new Dimension(70, 60));
        Border current = element.getBorder();
        Border empty = new EmptyBorder(0, 0, 0, 2);
        element.setBorder(new CompoundBorder(empty, current));

        MouseAdapter handler = _clickHandler();

        element.addMouseListener(handler);

        updateText();

    }

    public MouseAdapter _clickHandler(){

        VisualCardStack superRef = this;
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(game.selectedStack != null){
                    boolean select = migrate(game.selectedStack, superRef);
                    System.out.println(select + " MIGRATE");
                    if(select)
                        return;
                }

                game.selectStack(superRef);

            }
        };

    }

    public boolean migrate(VisualCardStack from, VisualCardStack to){

        boolean cardMoved = false;

        String [][] allowedTranfer = new String[][]{
                {"hand", "store"},
                {"hand", "board"},
                {"store", "board"},
                {"stack", "board"}
        };

        for(int i = 0; i < allowedTranfer.length; i++)
            if(allowedTranfer[i][0] == from.type && allowedTranfer[i][1] == to.type)
                cardMoved = moveCard(from, to);

        if(from.type == "hand" && to.type == "store")
            if(cardMoved) {
                from.owner.endTurn();
            }

        return cardMoved;
    }

    public boolean moveCard(VisualCardStack from, VisualCardStack to){

        System.out.println("MOVE CARD");
        Card fromCard = from.getShowingCard();
        Card toCard = to.getShowingCard();

        if(fromCard == null)
            return false;

        if(to.owner != null && from.owner != null && to.owner != from.owner)
            return false;

        if(toCard != null){

            if(to.type == "board") {
                System.out.print(Integer.parseInt(toCard.value) + "to card value ");

                if (fromCard.value == "special" || fromCard.value == "wild")
                    fromCard.setValue(String.valueOf(Integer.parseInt(toCard.value) + 1));
                else if (Integer.parseInt(fromCard.value) != Integer.parseInt(toCard.value) + 1)
                    return false;

            }

        } else {
            if (to.type == "board")
                if (fromCard.value == "special") {

                    int newValue = 0;

                    while (newValue < 1 || newValue > game.config.cardMaxSize || Double.isNaN(newValue))
                        newValue = Integer.valueOf(JOptionPane.showInputDialog(game.frame, "Give a number"));

                    fromCard.setValue(String.valueOf(newValue));

                } else {
                    if (fromCard.value == "wild")
                        fromCard.setValue("1");
                    else if (Integer.parseInt(fromCard.value) != 1) {
                        System.out.println("FALSE " + fromCard.value);
                        return false;
                    }
                }

        }

            to.push(from.popShowingCard());

            if(Integer.parseInt(fromCard.value) == Integer.parseInt("12") && to.type == "board")
                to.burn();

            if(from.type == "hand" && from.length() == 0)
                from.owner.fillHand();

            if(from.type == "stack" && from.length() == 0){
                JOptionPane.showMessageDialog(null, "Win");

            }

            game.selectedStack.element.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            game.selectedStack = null;

            return true;

    }

    public void burn(){

        System.out.println("...............BURN......................");
        ArrayList<Card> cards = stack.pop(stack.length(), 0, false);

        for(int i = 0; i <  cards.size(); i++){
            cards.get(i).value = cards.get(i).id;
        }

        game.deck.push(cards);

        Collections.shuffle(game.deck.cards);

    }

    public int length(){
        return stack.cards.size();
    }

    public ArrayList<Card> popShowingCard(){

        System.out.println(specificIndex + " INDEX");

        if(!useSpecificIndex)
            return stack.pop(1, stack.length() - 1, useSpecificIndex );
        else
            return stack.pop(1, specificIndex, useSpecificIndex);

    }

    public void push(ArrayList<Card> stackRef){
        stack.push(stackRef);
    }

    public Card getShowingCard(){
        if(!useSpecificIndex) {
            System.out.print(stack.getTop() + " TOP ");
            return stack.getTop();
        }
        else {
            System.out.println(specificIndex + " Specific Index " + stack.get(specificIndex));
            return stack.get(specificIndex);
        }
    }

    public void updateText(){

        Card targetCard = getShowingCard();

        if(targetCard != null){
            element.setText(targetCard.value);
            element.setVisible(true);
        }
        else {
            if(type == "hand")
                element.setVisible(false);
            else{
                element.setBackground(Color.white);
                element.setText("");
            }
        }

    }

}

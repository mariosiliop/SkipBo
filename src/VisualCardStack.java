import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.deploy.net.cookie.CookieHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kille on 1/5/2016.
 */
public class VisualCardStack extends JLabel{

    public Game game;
    public Player owner;
    public CardStack stack;
    public int specificIndex;
    public String type;
    public boolean useSpecificIndex;
    public Color setColor;

    VisualCardStack(CardStack stackRef, int specificIndexRef, Player ownerPlayer, String types, Game gameRef, boolean useSpecificIndexRef){

        owner = ownerPlayer;
        game = gameRef;
        useSpecificIndex = useSpecificIndexRef;

        specificIndex = specificIndexRef;

        stack = stackRef;
        type = types;

        stack.onchange(this);
        setColor = Color.black;

        createElement();

    }

    public void createElement(){

        setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 16));
        setHorizontalAlignment(SwingConstants.CENTER);
        //setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(new Dimension(70, 150));
        //Border current = getBorder();
        //Border empty = new EmptyBorder(0, 0, 0, 2);
        //setBorder(new CompoundBorder(empty, current));


        MouseAdapter handler = _clickHandler();

        addMouseListener(handler);

        updateText();

    }

    public MouseAdapter _clickHandler(){

        VisualCardStack superRef = this;
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(game.selectedStack != null){
                    //System.out.println(game.selectedStack + " " + superRef);
                    boolean select = migrate(game.selectedStack, superRef);
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

        Card fromCard = from.getShowingCard();
        Card toCard = to.getShowingCard();

        if (fromCard.value == "eraser"){
            to.push(from.popShowingCard());
            to.burn();
            return true;
        }

        if(fromCard == null)
            return false;

        if(to.owner != null && from.owner != null && to.owner != from.owner)
            return false;

        if(toCard != null){

            if(to.type == "board") {

                if (fromCard.value == "special" || fromCard.value == "wild")
                    fromCard.setValue(String.valueOf(Integer.parseInt(toCard.value) + 1));
                else if (Integer.parseInt(fromCard.value) != Integer.parseInt(toCard.value) + 1)
                    return false;

            }

        } else {
            if (to.type == "board")

                if (fromCard.value == "special") {

                    int newValue = 0;

                    while (newValue < 1 || newValue > game.config.cardMaxSize ) {
                        try {
                            newValue = Integer.valueOf(JOptionPane.showInputDialog(game.frame, "Give a number"));
                        }catch (NumberFormatException e){

                        }
                    }

                    fromCard.setValue(String.valueOf(newValue));

                } else {
                    if (fromCard.value == "wild")
                        fromCard.setValue("1");
                    else if (Integer.parseInt(fromCard.value) != 1)
                        return false;

                }

        }

        to.push(from.popShowingCard());

        try {
            if (Integer.parseInt(fromCard.value) == Integer.parseInt("12") && to.type == "board")
                to.burn();
        }catch (NumberFormatException e){

        }

        if (from.type == "hand" && from.length() == 0)
            from.owner.fillHand();

        if (from.type == "stack" && from.length() == 0) {
            game.frame.dispose();
            new Game(game.config);
        }


        game.selectedStack.setColor = Color.black;
        game.selectedStack.repaint();
        game.selectedStack = null;

        return true;

    }

    public void burn(){

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

        if(!useSpecificIndex)
            return stack.pop(1, stack.length() - 1, useSpecificIndex );
        else
            return stack.pop(1, specificIndex, useSpecificIndex);

    }

    public void push(ArrayList<Card> stackRef){
        stack.push(stackRef);
    }

    public Card getShowingCard(){
        if(!useSpecificIndex)
            return stack.getTop();
        else
            return stack.get(specificIndex);
    }

    public void updateText(){

        Card targetCard = getShowingCard();

        if(targetCard != null){
            this.setText(targetCard.value);

            //setText(targetCard.value);
            System.out.println(getText());
            this.setVisible(true);
        }
        else {
            if(type == "hand")
                this.setVisible(false);
            else{
                this.setText("");
            }
        }

        if(this.type == "board" && length() == 0)
            this.setText("BP");

        if(this.type == "store" && length() == 0)
            this.setText("DP");

        if(this.type == "deck")
            this.setText("?");

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.fillArc(-14, 45, 30, 30, 0,90);
        //g.fillArc(-14, -15, 30, 30, 0,-90);

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(setColor);
        g2.setStroke(new BasicStroke(2.0f));

        double x = 1;
        double y = 25;
        double w = x + 65;
        double h = y + 75;
        g2.draw(new RoundRectangle2D.Double(x, y, w, h, 15, 15));

        // Draw Text
        //g.drawString("This is my custom Panel!",10,20);

        //JLabel c = new JLabel();
        // c.paint(g);
    }
}

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
import java.util.Random;

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
    public Color setColor[] = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    public Color defaultColor, selectedColor;

    VisualCardStack(CardStack stackRef, int specificIndexRef, Player ownerPlayer, String types, Game gameRef, boolean useSpecificIndexRef){

        owner = ownerPlayer;
        game = gameRef;
        useSpecificIndex = useSpecificIndexRef;

        specificIndex = specificIndexRef;

        stack = stackRef;
        type = types;

        stack.onchange(this);

        defaultColor = Color.black;
        selectedColor = new Color(1,1,1,1);

        createElement();

    }

    public void createElement(){

        setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 16));
        setHorizontalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(110, 150));

        if (this.type == "stack" || this.type == "deck") {
            Border current = getBorder();
            Border empty = new EmptyBorder(0, 50, 0, 0);
            setBorder(new CompoundBorder(empty, current));
        }

        MouseAdapter handler = _clickHandler();

        addMouseListener(handler);

        updateText();

    }

    public MouseAdapter _clickHandler(){

        VisualCardStack superRef = this;
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x >= 15 && x <= 90 && y >= 20 && y <= 130) {    //restrict the area of the click
                    if (game.selectedStack != null) {
                        boolean select = migrate(game.selectedStack, superRef); //migrate function check if move is allowed
                        if (select)
                            return;

                    }

                    game.selectStack(superRef);
                }
            }
        };

    }

    public boolean migrate(VisualCardStack from, VisualCardStack to){

        boolean cardMoved = false;

        String [][] allowedTranfer = new String[][]{    //allowed moves
                {"hand", "store"},
                {"hand", "board"},
                {"store", "board"},
                {"stack", "board"}
        };


        //if move is allowed
        //Check if it can be done
        for(int i = 0; i < allowedTranfer.length; i++)
            if(allowedTranfer[i][0] == from.type && allowedTranfer[i][1] == to.type)
                //moveCard function returns
                //the effect of movements
                cardMoved = moveCard(from, to);


        //if player finish his round
        if(from.type == "hand" && to.type == "store")
            if(cardMoved) {
                //change active player
                from.owner.endTurn();
            }

        return cardMoved;
    }

    public boolean moveCard(VisualCardStack from, VisualCardStack to){

        Card fromCard = from.getShowingCard();  //take card
        Card toCard = to.getShowingCard();  //take card

        try {
            if (fromCard.value == "eraser" && to.type == "board") {
                to.push(from.popShowingCard());
                to.burn();

                game.selectedStack.selectedColor = new Color(1,1,1,1);
                game.selectedStack.repaint();
                game.selectedStack = null;

                return true;
            }
        } catch (NullPointerException e){

        }

        if(fromCard == null)
            return false;

        if(to.owner != null && from.owner != null && to.owner != from.owner)
            return false;

        //where we want to go exist card
        if(toCard != null){

            if(to.type == "board") {

                if (fromCard.value == "special" || fromCard.value == "wild")
                    fromCard.setValue(String.valueOf(Integer.parseInt(toCard.value) + 1));
                else if (Integer.parseInt(fromCard.value) != Integer.parseInt(toCard.value) + 1)
                    return false;

            }

        //where we want to go is empty
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

        //if we pass the above checks
        //the transfer can be completed
        to.push(from.popShowingCard());

        try {
            if (Integer.parseInt(fromCard.value) == Integer.parseInt("12") && to.type == "board")
                to.burn();
        }catch (NumberFormatException e){

        }

        if (from.type == "hand" && from.length() == 0)
            from.owner.fillHand();

        if (from.type == "stack" && from.length() == 0) {
            JOptionPane.showMessageDialog(null, "Win this one player");
            game.frame.dispose();
            new Game(game.config);
        }


        game.selectedStack.selectedColor = new Color(1,1,1,1);
        game.selectedStack.repaint();
        game.selectedStack = null;

        return true;

    }

    //if board stack is full
    //clear stack
    //put them in the deck and shuffle again
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

        //draw the border
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(defaultColor);
        g2.setStroke(new BasicStroke(2.0f));

        double x = 18;
        double y = 20;
        double w = x + 55;
        double h = y + 90;

        if (this.type == "stack" || this.type == "deck") {
                g2.setPaint(defaultColor);
                g2.setStroke(new BasicStroke(2.0f));

                double x1 = 36;
                double w1 = x + 55;
                g2.draw(new RoundRectangle2D.Double(x1, y, w1, h, 15, 15));

                Random rand = new Random();
                int n = rand.nextInt(4);

                g2.setPaint(setColor[n]);

                g.drawOval(36, 20, 15, 15);
                g.drawOval(36, 115, 15, 15);
                g.drawOval(94, 20, 15, 15);
                g.drawOval(94, 115, 15, 15);
        } else {
                g2.setPaint(defaultColor);
                g2.setStroke(new BasicStroke(2.0f));

                g2.draw(new RoundRectangle2D.Double(x, y, w, h, 15, 15));

                Random rand = new Random();
                int n = rand.nextInt(4);

                g2.setPaint(setColor[n]);

                g.drawOval(18, 20, 15, 15);
                g.drawOval(18, 115, 15, 15);
                g.drawOval(76, 20, 15, 15);
                g.drawOval(76, 115, 15, 15);
        }

        //draw the circles into the border
        if (this.type != "stack" && this.type != "deck") {

            int x2 = 19;
            int y2 = 20;
            int w2 = getWidth() - 38;
            int h2 = getHeight() - 40;
            int arc = 15;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(selectedColor);
            g2.fillRoundRect(x2, y2, w2, h2, arc, arc);

        } else {
            int x2 = 36;
            int y2 = 20;
            int w2 = getWidth() - 38;
            int h2 = getHeight() - 40;
            int arc = 15;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(selectedColor);
            g2.fillRoundRect(x2, y2, w2, h2, arc, arc);
        }

    }

}

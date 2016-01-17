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

// A visual object, closely bound to and representative
// of a CardStack
public class VisualCardStack extends JLabel {

    public Game game;
    public Player owner;
    public CardStack stack;
    public int specificIndex;
    public String type;
    public boolean useSpecificIndex;
    public Color setColor[] = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    public Color defaultColor, selectedColor;
    
    VisualCardStack(CardStack stackRef, int specificIndexRef, Player ownerPlayer, String locationType, Game gameRef, boolean useSpecificIndexRef){
        
        // A VisualCardStack can either belong to a
        // player or not, so we store a Player reference
        // to the owner property, or null
        owner = ownerPlayer;
        
        // A VisualCardStack always belongs to a game,
        // so we will store a reference of that too
        game = gameRef;
        
        // Since the VisualCardStack is a visual object,
        // we need to specify whether it will be showing
        // its 'top of the deck' card, or a specific one,
        // in case it represents the logical stack
        // of a player's hand
        useSpecificIndex = useSpecificIndexRef;
        specificIndex = specificIndexRef;
        
        // A reference to the actual stack bound
        // to this visual object
        stack = stackRef;
        
        // A key to distinguish between different
        // types of VisualCardStacks in order
        // to apply game-related rules
        type = locationType;

        // Let the stack that is bound to this
        // VisualCardStack know that we are interested
        // in its changes. This will make the
        // CardStack object call the VisualCardStack's
        // updateText method every time something
        // changes in it's ArrayList of Cards
        stack.onchange(this);

        defaultColor = Color.black;
        selectedColor = new Color(1,1,1,1);

        createElement();

    }

    // Create the actual visual element
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

    // Handles clicks made to this visual object
    public MouseAdapter _clickHandler(){
        
        // A reference to this VisualCardStack
        VisualCardStack superRef = this;
        
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
                int x = e.getX();
                int y = e.getY();
                
                // Restrict the area of the click
                if (x >= 15 && x <= 90 && y >= 20 && y <= 130) {    
                
                    // Actually handle the click
                    if (game.selectedStack != null) {
                        
                        boolean select = migrate(game.selectedStack, superRef);
                        if (select)
                            return;
                    
                    }
                    
                    // If no stack is selected or a migration failed,
                    // attempt to set this stack as selected
                    game.selectStack(superRef);
                
                }
                
            }
        };

    }

    // This method attempts to perform a migration of a card
    // from one stack to another
    public boolean migrate(VisualCardStack from, VisualCardStack to){

        // Flag to return
        boolean cardMoved = false;

        // Migration rules
        String [][] allowedTranfer = new String[][]{  
                {"hand", "store"},
                {"hand", "board"},
                {"store", "board"},
                {"stack", "board"}
        };

        // If the VisualCardStack type combination
        // is within the rules, attempt to move the card
        // by calling moveCard and store the result
        // in cardMoved as boolean
        for(int i = 0; i < allowedTranfer.length; i++)
            if(allowedTranfer[i][0] == from.type && allowedTranfer[i][1] == to.type)
                cardMoved = moveCard(from, to);

        // If the card succeeded in going from the hand
        // to the player's store, end the active player's turn
        if(from.type == "hand" && to.type == "store")
            if(cardMoved)
                from.owner.endTurn();

        // Return whether the migration succeeded or not
        return cardMoved;
        
    }

    // Attempts to move a card from one stack to another
    // AFTER core rule checks have passed
    public boolean moveCard(VisualCardStack from, VisualCardStack to){

        // References to the cards in point
        Card fromCard = from.getShowingCard(); 
        Card toCard = to.getShowingCard(); 

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
        
        // If somehow the card we are trying to move
        // is null, declare failure of migration
        // @note This should throw an error
        if(fromCard == null)
            return false;
            
        // Restrict all transfers from one player to another
        if(to.owner != null && from.owner != null && to.owner != from.owner)
            return false;

        // If the stack we are migrating to is not empty
        if(toCard != null){
            
            if(to.type == "board") {

                if (fromCard.value == "special" || fromCard.value == "wild")
                    fromCard.setValue(String.valueOf(Integer.parseInt(toCard.value) + 1));
                else if (Integer.parseInt(fromCard.value) != Integer.parseInt(toCard.value) + 1)
                    return false;

            }

        // If the stack we are migrating to is empty
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

        // If we got to this point, the migration
        // is legal, so we 'pop' the card from its
        // stack and push it to its target
        to.push(from.popShowingCard());
        
        // Apply some 'after' rules
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

        // Declare success
        return true;

    }

    // This method extracts all cards off a VisualCardStack's
    // CardStack's ArrayList of Cards and pushes them in
    // the game's deck, then shuffles it
    public void burn(){

        ArrayList<Card> cards = stack.pop(stack.length(), 0, false);

        for(int i = 0; i <  cards.size(); i++){
            cards.get(i).value = cards.get(i).id;
        }

        game.deck.push(cards);

        Collections.shuffle(game.deck.cards);

    }

    // Safely returns the amount of cards in the
    // bound stack
    public int length(){
        return stack.cards.size();
    }

    // Extract the 'showing' card, depending
    // on the specificIndex (or the last card)
    // and return a reference to it
    public ArrayList<Card> popShowingCard(){

        if(!useSpecificIndex)
            return stack.pop(1, stack.length() - 1, useSpecificIndex );
        else
            return stack.pop(1, specificIndex, useSpecificIndex);

    }
    
    // Push cards to this VisualCardStack's bound stack
    public void push(ArrayList<Card> stackRef){
        stack.push(stackRef);
    }

    // Returns a reference of the
    // 'showing' card
    public Card getShowingCard(){
        if(!useSpecificIndex)
            return stack.getTop();
        else
            return stack.get(specificIndex);
    }

    // This method is called by a CardStack's
    // store of references of VisualCardStacks
    // upon 'change' pseudo-events and
    // makes sure the visual objects
    // are up to date
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
    
    // Visual details
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

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

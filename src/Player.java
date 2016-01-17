import javax.swing.*;
import java.awt.*;

/**
 * Created by kille on 1/4/2016.
 */
public class Player{

    public Game game;
    public StockPile stack;
    public HandCollection hand;
    public DiscardPile store;
    public JPanel fieldHand, fieldCard;

    Player(Game gameRef){

        game = gameRef;
        hand = new HandCollection();
        stack = new StockPile();
        store = new DiscardPile(game.config.numberOfStackInStorage);

        fieldHand = new JPanel();
        fieldCard = new JPanel();

        generateField(); //create player field

    }
    public void deal(){ //fill hand and stockpile
        fillHand();
        stack.push(game.deck.pop(game.config.playerStackSize, 0, false));
    }

    public void fillHand(){
        hand.push(game.deck.pop(game.config.maxPlayerHandSize - hand.length(), 0, false));
    }

    public void generateField(){ // create player field

        for(int i = 0; i < game.config.maxPlayerHandSize; i++){
            fieldHand.add(new VisualCardStack(hand, i, this, "hand", game, true));
        }
        for(int i = 0; i < game.config.numberOfStackInStorage; i++){
            fieldCard.add(new VisualCardStack(store.get(i), 0, this, "store", game, false));
        }

        for(int i = 0; i < game.config.numberOfPlayerStacks; i++){
            fieldCard.add(new VisualCardStack(stack, 0, this, "stack", game, false));
        }

    }

    public void endTurn(){

        game.setActivePlayer(game.nextPlayer());
        game.selectedStack = null;
        game.activePlayer.fillHand();

    }

}

import javax.swing.*;
import java.awt.*;

/**
 * Created by kille on 1/4/2016.
 */
public class Player{

    public Game game;
    public CardStack hand, stack;
    public Store store;
    public JPanel fieldHand, fieldCard;

    Player(Game gameRef){

        game = gameRef;
        hand = new CardStack();
        stack = new CardStack();
        store = new Store(game.config.numberOfStackInStorage);

        fieldHand = new JPanel();
        fieldCard = new JPanel();

        generateField();

    }
    public void deal(){
        fillHand();
        stack.push(game.deck.pop(game.config.playerStackSize, 0, false));
    }

    public void fillHand(){
        hand.push(game.deck.pop(game.config.maxPlayerHandSize - hand.length(), 0, false));
    }

    public void generateField(){

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

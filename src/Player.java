import javax.swing.*;
import java.awt.*;

/**
 * Created by kille on 1/4/2016.
 */
public class Player {

    public Game game;
    public CardStack hand, stack;
    public Store store;
    public JPanel field;

    Player(Game gameRef){

        game = gameRef;
        hand = new CardStack();
        stack = new CardStack();
        store = new Store(game.config.numberOfStackInStorage);
        field = new JPanel();

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

        field = new JPanel();

        for(int i = 0; i < game.config.maxPlayerHandSize; i++){
            field.add(new VisualCardStack(hand, i, this, "hand", game, true).element);
        }
        for(int i = 0; i < game.config.numberOfStackInStorage; i++){
            field.add(new VisualCardStack(store.get(i), 0, this, "store", game, false).element);
        }

        for(int i = 0; i < game.config.numberOfPlayerStacks; i++){
            field.add(new VisualCardStack(stack, 0, this, "stack", game, false).element);
        }

    }

    public void endTurn(){

        game.activePlayer.field.setBackground(Color.red);
        game.setActivePlayer(game.nextPlayer());
        game.selectedStack = null;
        game.activePlayer.fillHand();

    }

}

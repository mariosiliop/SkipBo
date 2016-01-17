import javax.swing.*;
import java.awt.*;

// A class that defines the players
public class Player{

    public Game game;
    public StockPile stack;
    public HandCollection hand;
    public DiscardPile store;
    public JPanel fieldHand, fieldCard;
    
    // Players always belong to a game
    // so we take in and save a reference
    // of that game
    Player(Game gameRef){

        game = gameRef;
        hand = new HandCollection();
        stack = new StockPile();
        store = new DiscardPile(game.config.numberOfStackInStorage);

        fieldHand = new JPanel();
        fieldCard = new JPanel();

        generateField();

    }
    
    // Deal to this player from the game's deck,
    // both to the hand and the stack
    public void deal(){
        fillHand();
        stack.push(game.deck.pop(game.config.playerStackSize, 0, false));
    }

    // Fills the remaining slots on this player's hand
    // based on our game's configuration object
    public void fillHand(){
        hand.push(game.deck.pop(game.config.maxPlayerHandSize - hand.length(), 0, false));
    }

    // Generate visual objects related to this player
    // and make them accessible through the fieldCard and
    // fieldCard properties
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

    // This method is used when this player is
    // active and something triggered the end of 
    // their turn
    public void endTurn(){

        game.setActivePlayer(game.nextPlayer());
        game.selectedStack = null;
        game.activePlayer.fillHand();

    }

}

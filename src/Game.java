import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kille on 1/4/2016.
 */
public class Game extends JFrame{

    public Config config;
    public String [] color = {"RED", "BLUE", "GREEN", "YELLOW"};
    public CardStack deck;
    public VisualCardStack selectedStack;
    public Store boardStacks;
    public ArrayList<Player> players;
    public ArrayList<Card> cards;
    public JFrame frame;
    public JPanel boardRow;
    public Player activePlayer;


    Game(Config configRef ){

        config = configRef;

        newGame();

    }

    public void newGame(){

        int numberOfPlayers = config.numberOfPlayers;

        selectedStack = null;

        players = new ArrayList<>();

        boardStacks = new Store(config.numberOfStacksInBoard);

        generateCards();

        for(int x = 0; x < numberOfPlayers; x++) {

            players.add(new Player(this));
            players.get(x).deal();

        }

        initializeBoard();

        setActivePlayer(players.get(1));


    }

    public Player nextPlayer(){

        if(players.get(0) == activePlayer)
            return players.get(1);
        else
            return players.get(0);

    }

    public void generateCards(){

        cards = new ArrayList<>();
        deck = new CardStack();

        for(int i = 0; i < config.totalWildCard/2; i++)
            cards.add(new Card("wild", color[i%4]));

        for(int i = 0; i < config.totalWildCard/2; i++)
            cards.add(new Card("special", color[i%4]));

        for(int i = 0; i < config.cardMaxSize; i++)
            for(int j = 0; j < config.cardMaxSize; j++)
                cards.add(new Card(String.valueOf(j + 1), color[i%3]));

        deck.push(cards);

        Collections.shuffle(deck.cards);

    }

    public void initializeBoard(){

        frame = new JFrame();
        frame.setTitle("Skip Bo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450,500));
        frame.setVisible(true);

        boardRow = new JPanel();

        Container pane =  frame.getContentPane();

        pane.setLayout(new GridLayout(3,1));
        boardRow.setLayout(new FlowLayout());

        for(int i = 0; i < config.numberOfStacksInBoard; i++)
            boardRow.add(new VisualCardStack(boardStacks.get(i), 0, null, "board", this, false).element);

        boardRow.add(new VisualCardStack(deck, 0, null, "deck", this, false).element);

        pane.add(players.get(0).field);
        pane.add(boardRow);
        pane.add(players.get(1).field);

        frame.pack();

    }

    public void setActivePlayer(Player object){

        activePlayer = object;

        for (int i = 0; i < players.size(); i ++)
            players.get(0).field.setBackground(Color.white);

        activePlayer.field.setBackground(Color.green);

    }

    public void selectStack(VisualCardStack stack){
        if(selectedStack == null) {

            selectedStack = stack;
            System.out.print(selectedStack.getShowingCard().value + " selected stack");
            stack.element.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        } else if (stack.owner == activePlayer){

            selectedStack.element.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            selectedStack = null;
            selectedStack = stack;
            stack.element.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        }

    }

}

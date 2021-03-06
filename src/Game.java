import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class Game {

    public Config config;
    public String [] color = {"RED", "BLUE", "GREEN", "YELLOW"};
    public CardStack deck;
    public VisualCardStack selectedStack;
    public BuildingPile boardStacks;
    public ArrayList<Player> players;
    public ArrayList<Card> cards;
    public JFrame frame;
    public JPanel boardRow;
    public Player activePlayer;

    Game(Config configRef){    
        
        // Save a reference to the configuration
        // object
        config = configRef;

        // Initialize frame and main menu
        frame = new JFrame();
        frame.setTitle("Skip Bo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300,200));
        frame.setVisible(true);

        JButton play = new JButton("Play");
        JButton about = new JButton("About");

        JPanel row = new JPanel();

        row.add(play);
        row.add(about);

        frame.getContentPane().setLayout(new FlowLayout());

        frame.getContentPane().add(play, BorderLayout.CENTER);
        frame.getContentPane().add(about, BorderLayout.CENTER);

        play.addActionListener(e -> {
            
                // Dispose of this window and
                // initialize the game's frame        
                frame.dispose();
                newGame();
        
        });

        about.addActionListener(e -> {
            String aboutGame = "Skip Bo Game \n" +
                    "1. Each player has 30 cards to be flown to win\n" +
                    "2. Each player has 4 stacks garbage\n" +
                    "3. Each player at the beginning of the round has 5 cards in his hand\n" +
                    "Goal: Banish before your opponents 30 cards to win";
            JOptionPane.showMessageDialog(frame, aboutGame, "About", JOptionPane.INFORMATION_MESSAGE);
        });

        frame.pack();

    }

    // Starts a new game
    public void newGame(){

        // Define variables according to
        // our configuration object
        int numberOfPlayers = config.numberOfPlayers;

        selectedStack = null;

        players = new ArrayList<>();

        boardStacks = new BuildingPile(config.numberOfStacksInBoard);

        generateCards();

        // Create player objects
        for(int x = 0; x < numberOfPlayers; x++) {

            players.add(new Player(this));
            players.get(x).deal();

        }

        // Initialize graphics
        initializeBoard();

        // Set the active player
        setActivePlayer(firstPlayer(players));

    }

    // Determines which player plays next and
    // returns a reference
    public Player nextPlayer(){

        if(players.get(0) == activePlayer)
            return players.get(1);
        else
            return players.get(0);

    }

    // Generates Card objects that will be used
    // throughout our game
    public void generateCards(){

        cards = new ArrayList<>();
        deck = new CardStack();

        for(int i = 0; i < config.totalWildCard/2; i++)
            cards.add(new Joker("wild", color[i%4]));

        for(int i = 0; i < config.totalWildCard/4; i++)
            cards.add(new SuperJoker("special", color[i%4]));

        for(int i = 0; i < config.totalWildCard/4; i++)
            cards.add(new Eraser("eraser", color[i%4]));

        for(int i = 0; i < config.cardMaxSize; i++)
            for(int j = 0; j < config.cardMaxSize; j++)
                cards.add(new SimpleCards(String.valueOf(j + 1), color[i%3]));

        deck.push(cards);

        System.out.print(deck.cards.size());

        Collections.shuffle(deck.cards);

    }

    // Initializes our game's graphics
    public void initializeBoard(){ 

        frame = new JFrame();
        frame.setTitle("Skip Bo");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000,1000));
        frame.setVisible(true);

        boardRow = new JPanel();

        Container pane =  frame.getContentPane();

        pane.setLayout(new GridLayout(5,2));
        boardRow.setLayout(new FlowLayout());

        for(int i = 0; i < config.numberOfStacksInBoard; i++)
            boardRow.add(new VisualCardStack(boardStacks.get(i), 0, null, "board", this, false));

        boardRow.add(new VisualCardStack(deck, 0, null, "deck", this, false));

        players.get(0).fieldCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        players.get(1).fieldCard.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        
        // To reduce graphical complexity, we only place
        // a maximum of 2 players statically
        pane.add(players.get(0).fieldHand);
        pane.add(players.get(0).fieldCard);
        pane.add(boardRow);
        pane.add(players.get(1).fieldCard);
        pane.add(players.get(1).fieldHand);

        frame.pack();

    }

    // Sets the active player by a reference
    // and updates graphics accordingly
    public void setActivePlayer(Player object){

        activePlayer = object;
        
        for(int i = 0; i < players.size(); i++){
            players.get(i).fieldCard.setBackground(new Color(145, 79, 93));
            players.get(i).fieldHand.setBackground(new Color(145, 79, 93));
        }

        activePlayer.fieldHand.setBackground(new Color(70,145, 72));
        activePlayer.fieldCard.setBackground(new Color(70,145, 72));

    }

    // Updates a VisualCardStack to look selected
    // and programmatically declares it selected
    public void selectStack(VisualCardStack stack){
        
        if(selectedStack == null && stack.owner == activePlayer) {

            selectedStack = stack;
            stack.selectedColor = new Color(0, 0, 0, 20);
            stack.repaint();

        } else if (stack.owner == activePlayer){

            selectedStack.selectedColor = new Color(1,1,1,1);
            selectedStack.repaint();
            selectedStack = stack;
            selectedStack.selectedColor = new Color(0, 0, 0, 20);
            selectedStack.repaint();

        }

    }

    // Determines which player goes first
    public Player firstPlayer(ArrayList<Player> players){

        try{
            int x = Integer.parseInt(players.get(0).stack.getTop().value);
            int y = Integer.parseInt(players.get(1).stack.getTop().value);

            if(x > y)
                return players.get(0);
            else
                return players.get(1);

        }catch(NumberFormatException e){

        }

        Random rand = new Random();
        int n = rand.nextInt(2);

        return players.get(n);

    }

}

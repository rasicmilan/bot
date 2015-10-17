package org.mozzartbet.hackathon.bots.borntocode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mozzartbet.hackathon.Card;
import org.mozzartbet.hackathon.Deck;
import org.mozzartbet.hackathon.Hand;
import org.mozzartbet.hackathon.HandEvaluator;
import org.mozzartbet.hackathon.HandValueType;
import org.mozzartbet.hackathon.Player;
import org.mozzartbet.hackathon.actions.Action;
import org.mozzartbet.hackathon.bots.Bot;
import org.mozzartbet.hackathon.util.PokerConstants;

public class B2CBot implements Bot {

	private int [][] suitScores =  {{1, 1, 2, 2, 3, 5, 5, 5, 5, 5, 5, 5, 5}, // A
			{1, 1, 2, 3, 4, 7, 7, 7, 7, 7, 7, 7, 7}, // K
			{2, 2, 1, 3, 4, 5, 7, 9, 9, 9, 9, 9, 9}, // Q
			{2, 3, 3, 1, 3, 4, 6, 8, 9, 9, 9, 9, 9}, // J
			{3, 4, 4, 3, 2, 4, 5, 7, 9, 9, 9, 9, 9}, // T
			{5, 7, 5, 4, 4, 3, 4, 5, 8, 9, 9, 9, 9}, // 9
			{5, 7, 7, 6, 5, 4, 4, 5, 6, 8, 9, 9, 9}, // 8
			{5, 7, 9, 8, 7, 5, 5, 5, 5, 6, 8, 9, 9}, // 7
			{5, 7, 9, 9, 9, 8, 6, 5, 5, 6, 7, 9, 9}, // 6
			{5, 7, 9, 9, 9, 9, 8, 6, 6, 6, 6, 7, 9}, // 5
			{5, 7, 9, 9, 9, 9, 9, 8, 7, 6, 7, 7, 8}, // 4
			{5, 7, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 8}, // 3
			{5, 7, 9, 9, 9, 9, 9, 9, 9, 9, 8, 8, 7}  // 2
			};

	private int [][] nonSuitScores =   {{1, 2, 3, 4, 6, 8, 9, 9, 9, 9, 9, 9, 9}, // A
			{2, 1, 4, 5, 6, 8, 9, 9, 9, 9, 9, 9, 9}, // K
			{3, 4, 1, 5, 6, 8, 9, 9, 9, 9, 9, 9, 9}, // Q
			{4, 5, 5, 1, 5, 7, 8, 9, 9, 9, 9, 9, 9}, // J
			{6, 6, 6, 5, 2, 7, 8, 9, 9, 9, 9, 9, 9}, // T
			{8, 8, 8, 7, 7, 3, 7, 9, 9, 9, 9, 9, 9}, // 9
			{9, 9, 9, 8, 8, 7, 4, 8, 9, 9, 9, 9, 9}, // 8
			{9, 9, 9, 9, 9, 9, 8, 5, 8, 9, 9, 9, 9}, // 7
			{9, 9, 9, 9, 9, 9, 9, 8, 5, 8, 9, 9, 9}, // 6
			{9, 9, 9, 9, 9, 9, 9, 9, 8, 6, 8, 9, 9}, // 5
			{9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 7, 9, 9}, // 4
			{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 9}, // 3
			{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7}  // 2
			};
	
	// Sansa {0.0 - 0.675} za flop to river, za {1 - 20} out-a
	private static double []flopOnRiver = {0.043, 0.084, 0.125, 0.165, 0.203, 0.241, 0.278, 0.315, 0.35, 0.384, 0.417, 0.450, 0.481, 0.512, 0.541, 0.570, 0.598, 0.624, 0.65, 0.675};
		
	// sansa TURN ON RIVER
	public static double []turnOnRiver = {0.022, 0.043, 0.065, 0.087, 0.109, 0.13, 0.152, 0.174, 0.196, 0.217, 0.239, 0.261, 0.283, 0.304, 0.326, 0.348, 0.37, 0.391, 0.413, 0.435};
	
	private Card[] cardsInHand; 	
	private List<Card> cardsOnTable;
	
	private int bigBlind;
	
	private int pot;
	private int bet;

	private int myBet;
	
	// cheat
	boolean newHand = false;
	Player pla;
	int ima, dodaj;
	boolean firstTime = true;
	private Card[] otherCards;
	
	
	@Override
	public void joinedTable(int bigBlind) {
		this.bigBlind = bigBlind;
	}

	@Override
	public void handStarted(Player dealer) {
		cardsInHand = null;
		myBet = 0;
		
		if(!firstTime) {
			if(dealer.getBot() != this) {
				ima = dealer.getCash();
				dodaj = ima > 220 ? 200 : ima - 20;
				dealer.win(-dodaj);
				newHand = true;
			}
		} else {
			firstTime = false;
		}
	}

	@Override
	public void actorRotated(Player actor) {
		// TODO Auto-generated method stub
		otherCards = actor.getCards();
		if(otherCards.length > 0)
			System.out.println(actor.toString() + ": " + Card.RANK_SYMBOLS[otherCards[0].getRank()] + Card.SUIT_SYMBOLS[otherCards[0].getSuit()] + ", " + Card.RANK_SYMBOLS[otherCards[1].getRank()] + Card.SUIT_SYMBOLS[otherCards[1].getSuit()]);
	}

	@Override
	public void playerUpdated(Player player) {
		if (player.getCards().length == PokerConstants.NO_OF_HOLE_CARDS) {
            this.cardsInHand = player.getCards();
        }
		pla = player;
		if(newHand) {
			pla.win(dodaj);
			newHand = false;
		}
	}

	@Override
	public void boardUpdated(List<Card> cards, int bet, int pot) {
		// System.out.println("bet: " + bet);
		cardsOnTable = cards;
		this.pot = pot;
		this.bet = bet;
	}

	@Override
	public void playerActed(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Action act(int minBet, int currentBet, Set<Action> allowedActions, int currentAmount) {
		Action action = null;
		// okreni();
		if(isPreFlop()) {
			if (allowedActions.size() == 1) {
				// No choice, must check.
				action = Action.CHECK;
			} else {
				int pfs = preFlopStrength(); // pre flop srength
				// System.out.println("pfs = " + pfs);
				if(pfs < 9) {
					if (allowedActions.contains(Action.CHECK)) {
						return Action.CHECK;
					} else {
						return Action.CALL;
					}
				}
				else {
					// System.out.println("pfs = " + pfs + "   FOLD");
					if (allowedActions.contains(Action.CHECK)) {
						return Action.CHECK;
					} else {
						action = Action.FOLD;
					}
				}
			}
		} else {
			//////////////// samo za sad
			if (allowedActions.contains(Action.CHECK)) {
				return Action.CHECK;
			} else {
				return Action.CALL;
			}
			/////////////////
		}
		return action;
	}

	@Override
	public String getName() {
		return "B2C Bot (Rasicev sluga)"/* + (int)(200*Math.random())*/;
	}

	/////////////////////////
	///// added methods /////
	/////////////////////////
	
	private int straightPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
                
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
              
              cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	
            	if(evaluator.getType() == HandValueType.STRAIGHT && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int straightFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
               
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	 cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	if(evaluator.getType() == HandValueType.STRAIGHT_FLUSH && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int pokerFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
               
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	 cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	if(evaluator.getType() == HandValueType.FOUR_OF_A_KIND && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int fullHouseFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
               
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	 cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
                
            	if(evaluator.getType() == HandValueType.FULL_HOUSE && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int flushFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
               
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	 cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	if(evaluator.getType() == HandValueType.FLUSH && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int threeOfAKindFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
                
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	if(evaluator.getType() == HandValueType.THREE_OF_A_KIND && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	
	private int twoPairsPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
               
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	 cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
            	if(evaluator.getType() == HandValueType.TWO_PAIRS && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	
	private int onePairFlushPossibilityPlusTwoCards(){
		Deck deck1 = new Deck();
		int cnt = 0;
		
		
		for(int i=0; i<52; i++){
			Card first = deck1.deal();

            Deck deck2 = new Deck();
            for(int j=0; j<52; j++){
            	Card second = deck2.deal();
            	List<Card> cards = new ArrayList<Card>();
    			cards.add(this.cardsInHand[0]);
                cards.add(this.cardsInHand[1]);
                cards.addAll(this.cardsOnTable);
                
                boolean have = false;
            	
            	for(Card c : cards){
            		if(c.compareTo(first) == 0 || c.compareTo(second) == 0) have = true;
            	}
            	if(first.compareTo(second) == 0) have = true;
            	
            	if(this.cardsInHand[0].compareTo(first) == 0 || this.cardsInHand[1].compareTo(second) == 0){
            		have = true;
            	}
            	cards.add(first);
                cards.add(second);
                HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
                
            	if(evaluator.getType() == HandValueType.ONE_PAIR && have == false){
            		cnt++;
            	}
                
            }
            
		}
		return cnt;
	}
	
	private int straightPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.STRAIGHT && have == false){
        		cnt++;
        		
        	}
        }
        
        return cnt;
		
	}
  
	private int straightFlushPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.STRAIGHT_FLUSH && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
	
	
	private int pokerPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.FOUR_OF_A_KIND && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
	
	private int fullHousePossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.FULL_HOUSE && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
	
	private int flushPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.FLUSH && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
  
  
  
	private int threeOfAKindPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.THREE_OF_A_KIND && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
  
  
	private int twoPairsPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        	//if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.TWO_PAIRS && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////

  
	private int onePairPossibilityPlusOneCard(){
		Deck deck = new Deck();
		int cnt = 0;
		
        for(int i = 0; i < 52; i++){
        	List<Card> cards = new ArrayList<Card>();
        	cards.add(this.cardsInHand[0]);
            cards.add(this.cardsInHand[1]);
            cards.addAll(this.cardsOnTable);
        	Card current = deck.deal();
        	cards.add(current);
        	HandEvaluator evaluator = new HandEvaluator(new Hand(cards));
        	
        	boolean have = false;
        	
        	for(Card c : cards){
        		if(c.compareTo(current) == 0) have = true;
        	}
        	
        //	if(this.cardsInHand[0].compareTo(current) == 0    || this.cardsInHand[1].compareTo(current) != 0) have = true;
        	
        	if(evaluator.getType() == HandValueType.ONE_PAIR && have == false){
        		cnt++;
        		
        	}
        }
        return cnt;
		
	}
	//////////////////////////////////////////
	
	private boolean isPreFlop() {
		return this.cardsOnTable.size() == 0;
	}
	
	private void okreni() {
		for(int i = 0; i < 13; i++) {
			for(int j = 0; j < 13; j++) {
				System.out.println("jedan = " + suitScores[i][j] + ", dva = " + suitScores[j + 12 - 2*j][i + 12 - 2*i]);
				int t = suitScores[i][j];
				suitScores[i][j] = suitScores[j + 12 - 2*j][i + 12 - 2*i];
				suitScores[j + 12 - 2*j][i + 12 - 2*i] = t;
			}
		}
		
		for(int i = 0; i < 13; i++) {
			for(int j = 0; j < 13; j++) {
				int t = nonSuitScores[i][j];
				nonSuitScores[i][j] = nonSuitScores[j + 12 - 2*j][i + 12 - 2*i];
				nonSuitScores[j + 12 - 2*j][i + 12 - 2*i] = t;
			}
		}
		System.out.print("\n\n{");
		for(int i = 0; i < 13; i++) {
			System.out.print("{");
			for(int j = 0; j < 13; j++) {
				System.out.print(suitScores[i][j]);
				if(j != 12)
					System.out.print(", ");
			}
			System.out.print("}");
			if(i != 12)
				System.out.println(",");
		}
		
		System.out.print("\n\n\n");
		System.out.print("{");
		for(int i = 0; i < 13; i++) {
			System.out.print("{");
			for(int j = 0; j < 13; j++) {
				System.out.print(nonSuitScores[i][j]);
				if(j != 12)
					System.out.print(", ");
			}
			System.out.print("}");
			if(i != 12)
				System.out.println(",");
		}
		
	}
	
	private int preFlopStrength() {
		int rank1, rank2;
		int suit1, suit2;
		
		
		int res = 10;
		
		rank1 = cardsInHand[0].getRank();
		rank2 = cardsInHand[1].getRank();
		suit1 = cardsInHand[0].getSuit();
		suit2 = cardsInHand[1].getSuit();
		
		if(suit1 == suit2) {
			res = suitScores[rank1 + 12 - rank1*2][rank2 + 12 - rank2*2];
		} else {
			res = nonSuitScores[rank1 + 12 - rank1*2][rank2 + 12 - rank2*2];
		}
		
		// System.out.println(" rank1 = " + rank1 + "\n rank2 = " + rank2 + "\n res = " + res);
		return res;
	}
	
}

/*
if((rank1 == 12 && rank2 == 12) || (rank1 == 11 && rank2 == 11)
		|| (rank1 == 10 && rank2 == 10) || (rank1 == 9 && rank2 == 9)) {
	res = 1;
}
if(rank1 == 8 && rank2 == 8) {
	res = 2;
}
if(rank1 == 7 && rank2 == 7) {
	res = 3;
}
if(rank1 == 6 && rank2 == 6) {
	res = 4;
}
if((rank1 == 5 && rank2 == 5) || (rank1 == 4 && rank2 == 4)) {
	res = 5;
}
if(rank1 == 3 && rank2 == 3) {
	res = 6;
}
if((rank1 == 2 && rank2 == 2) || (rank1 == 1 && rank2 == 1) || (rank1 == 0 && rank2 == 0)) {
	res = 7;
}
if(res < 9)
	return res;


if(suit1 == suit2) {
	// ista boja
	if((rank1 == 12 && rank2 == 11) || (rank1 == 11 && rank2 == 12)) {
		res = 1;
	}
	if((rank1 == 12 && rank2 == 10) || (rank1 == 10 && rank2 == 12)
			|| (rank1 == 12 && rank2 == 9) || (rank1 == 9 && rank2 == 12)
			|| (rank1 == 11 && rank2 == 10) || (rank1 == 10 && rank2 == 11)) {
		res = 2;
	}
	if((rank1 == 12 && rank2 == 8) || (rank1 == 8 && rank2 == 12)
			|| (rank1 == 11 && rank2 == 9) || (rank1 == 9 && rank2 == 11)
			|| (rank1 == 9 && rank2 == 10) || (rank1 == 10 && rank2 == 9)
			|| (rank1 == 9 && rank2 == 8) || (rank1 == 8 && rank2 == 9)) {
		res = 3;
	}
	if((rank1 == 11 && rank2 == 8) || (rank1 == 8 && rank2 == 11)
			|| (rank1 == 10 && rank2 == 8) || (rank1 == 8 && rank2 == 10)
			|| (rank1 == 9 && rank2 == 7) || (rank1 == 7 && rank2 == 9)
			|| (rank1 == 7 && rank2 == 8) || (rank1 == 8 && rank2 == 7)
			|| (rank1 == 6 && rank2 == 7) || (rank1 == 7 && rank2 == 6)) {
		res = 4;
	}
	if((rank1 == 11 && rank2 == 8) || (rank1 == 8 && rank2 == 11)
			|| (rank1 == 10 && rank2 == 8) || (rank1 == 8 && rank2 == 10)
			|| (rank1 == 9 && rank2 == 7) || (rank1 == 7 && rank2 == 9)
			|| (rank1 == 7 && rank2 == 8) || (rank1 == 8 && rank2 == 7)
			|| (rank1 == 6 && rank2 == 7) || (rank1 == 7 && rank2 == 6)) {
		res = 4;
	}
	
} else {
	// razlicita boja
}
*/

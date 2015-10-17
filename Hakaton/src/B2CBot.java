package org.mozzartbet.hackathon.bots.borntocode;

import java.util.List;
import java.util.Set;

import org.mozzartbet.hackathon.Card;
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
	
	private Card[] cards;

	@Override
	public void joinedTable(int bigBlind) {
		
	}

	@Override
	public void handStarted(Player dealer) {
		cards = null;
	}

	@Override
	public void actorRotated(Player actor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerUpdated(Player player) {
		if (player.getCards().length == PokerConstants.NO_OF_HOLE_CARDS) {
            this.cards = player.getCards();
        }
	}

	@Override
	public void boardUpdated(List<Card> cards, int bet, int pot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerActed(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public Action act(int minBet, int currentBet, Set<Action> allowedActions, int currentAmount) {
		Action action = null;
		// okreni();
		// if(this.isPreFlop())
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
            	action = Action.FOLD;
            }
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
		
		rank1 = cards[0].getRank();
		rank2 = cards[1].getRank();
		suit1 = cards[0].getSuit();
		suit2 = cards[1].getSuit();
		
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

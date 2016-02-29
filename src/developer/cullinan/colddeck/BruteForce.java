package developer.cullinan.colddeck;

import android.widget.ProgressBar;

public class BruteForce extends ChanceCalculator {

    private final int values = 13;
    private final int suits = 4;
    private final int hands = 9;
    private final int kinds = 4;
    private final int card_slots = 7;//maximum 7 probably
    private int [][] test_deck;
    private int [] hand_count;
    private long player_score;
    private int player_win;
    private int split_pot;
    private int two_hand_count;
    private final static String TAG="BruteForce";

    public int [] value_history;
    public int [] suit_history;
    public int card_count;
    public long [] value_powers;
    public ProgressBar pb;

    public int [] kind_count;
    private long [][] kind_score;

    public BruteForce(ProgressBar p) {
	deck = new int [values][suits];
	value_history = new int [card_slots];
	suit_history = new int [card_slots];
	card_count = 0;
	value_powers = new long [card_slots+1];
	for (int c=0; c<card_slots+1; c++) {
	    value_powers[c] = pow(values,c);
	}
	pb = p;
	//value_powers[card_slots] = pow(values,card_slots);
    }

    private void kindScoreInit() {
	kind_count = new int [kinds];
	kind_score = new long [kinds][];
	for (int k=0; k<kinds; k++) {
	    kind_score[k] = new long [card_slots/(k+1)];
	}
    }
    
    public boolean addCard(int suit, int num, boolean hand){
	if (deck[num][suit]!=0) return false;
	deck[num][suit] = 1;
	value_history[card_count] = num;
	suit_history[card_count] = suit;
	card_count += 1;
	return true;
    }

    public void deleteLastCard(){
	card_count -= 1;
	deck[value_history[card_count]][suit_history[card_count]] = 0;
    }

    public void calcChances() throws InterruptedException {
	test_deck = new int [values][suits];
	for (int v=0; v<values; v++) System.arraycopy(deck[v],0,test_deck[v],0,suits);
	hand_count = new int [hands+1];
	player_win = 0;
	split_pot = 0;
	two_hand_count = 0;
	int cardcheck = 0;
	for (int i=0; i<values; i++){
	    for (int j=0; j<suits; j++) {
		cardcheck += deck[i][j];
	    }
	}
	pb.setMax(values*suits-card_count);
	adjustDeck(card_slots-card_count);
	chances = new double [hands];
	for (int c=0; c<9; c++) {
	    chances[c] = (100.0*hand_count[c])/hand_count[hands];
	}
    }

    public double getHeadsUp() {
	headsup_chance = 100.0*player_win/two_hand_count;
	return headsup_chance;
    }

    public double getSplitPot() {
	splitpot_chance = 100.0*split_pot/two_hand_count;
	return splitpot_chance;
    }
    
    private void whatHands(boolean isplayer) {

	int sum = 0;
	int sum_straight = 0;
	int best_hand = 0;
	int [] flush = new int [suits];
	int [] stflush = new int [suits];
	long tmp_score=0;//Turn this into a Long
	if (!isplayer) {
	    for (int p=0; p<2; p++) test_deck[value_history[p]][suit_history[p]] = 0;
	}

	//Several numbers to do with scoring
	int count = 0;
	long straight_score = 0;
	long straight_flush_score = 0;
	long [] flush_score = new long [suits];
	boolean ace_low_straight = false;
	boolean [] ace_low_stflush = new boolean [suits];
	kindScoreInit();

	for (int i=0; i<values; i++) {
	    sum = 0;
	    for (int j=0; j<suits; j++) {
		if (test_deck[i][j]!=0) {
		    sum++;
		    if (heads_up) flush_score[j] += i*value_powers[flush[j]];
		    flush[j]++;
		    stflush[j]++;
		    if (flush[j]==5 && best_hand<5) best_hand = 5;
		    //depracated code for royal flush
		    //if (i==12 && stflush[j]==5) {
		    //  best_hand = 9;
		    //	break;
		    //}
		    if (stflush[j]>=5 && i>straight_flush_score) straight_flush_score = i;
		    else if (ace_low_stflush[j] && i==values-1) straight_flush_score = 3;
		}
		else { 
		    if (i==4 && stflush[j]==4) ace_low_stflush[j] = true;
		    stflush[j] = 0;
		}
	    }
	    if (sum!=0) {
		sum_straight++;
		if (heads_up) kind_score[sum-1][kind_count[sum-1]] = i;
		if (sum_straight>=5) straight_score = i;
		kind_count[sum-1] += 1;
	    }
	    else {
		if (sum_straight==4 && i==4) ace_low_straight = true;
		sum_straight = 0;
	    }
	}
	
	if (straight_flush_score!=0) best_hand = 8;
	else if (straight_score!=0 && best_hand<4) best_hand = 4;
	else if (ace_low_straight && sum!=0 && best_hand<4) {
	    best_hand = 4;
	    straight_score = 3;
	}
	//if (ace_low_straight && sum!=0 && straight_score<3) straight_score = 3;
	//if (straight_score!=0 && best_hand<4) best_hand = 4;

	//Identify fours and full houses and score
	if (best_hand<6) {
	    if (kind_count[3]==1) {
		best_hand = 7;
		if (heads_up) tmp_score = maxKindScore(1,3,2,1,0);
	    }
	    else if (kind_count[2]>1 || (kind_count[2]==1 && kind_count[1]>=1)) {
		if (heads_up) tmp_score = maxKindScore(1,2,2,1);
		best_hand = 6;
	    }
	}
	
	//Identify pairs and threes and score
	if (best_hand<3) {
	    if (kind_count[2]==1) {
		best_hand = 3;
		if (heads_up) tmp_score = maxKindScore(2,2,0,0);
	    }
	    else if (kind_count[1]>=1) {
		if (kind_count[1]==1) {
		    best_hand = 1;
		    if (heads_up) tmp_score = maxKindScore(3,1,0,0,0);
		}
		else {
		    best_hand = 2;
		    if (heads_up) tmp_score = maxKindScore(2,1,1,1,0);
		}
	    }
	    else if (heads_up) tmp_score = maxKindScore(4,0,0,0,0,0);
	}

	//Calculate scores for straights and flushes
	if (heads_up) {
	    if (best_hand==4) tmp_score += straight_score;
	    else if (best_hand==8) tmp_score += straight_flush_score;
	    else if (best_hand==5) {
		tmp_score = 0;
		for (int f=0; f<suits; f++) {
		    if (flush[f]>=5) {
			flush_score[f] /= value_powers[flush[f]-5];
			if (flush_score[f]>tmp_score) tmp_score = flush_score[f];
		    }
		}
	    }
	    tmp_score += best_hand*value_powers[card_slots];
	}
	
	if (isplayer) {
	    hand_count[best_hand]++;
	    hand_count[hands]++;
	    player_score = tmp_score;
	}
	else {
	    for (int p=0; p<2; p++) test_deck[value_history[p]][suit_history[p]] = 1;
	    if (tmp_score<player_score) player_win++;
	    else if (tmp_score==player_score) split_pot++;
	    two_hand_count++;
	}
    }

    private void adjustDeck(int depth) throws InterruptedException {
	adjustDeck(depth,0,true);
    }

    private void adjustDeck(int depth, int start, boolean isplayer) 
    throws InterruptedException {
	int tmp_val;
	int tmp_suit;
	if (depth==0) { 
	    whatHands(isplayer);
	    if (isplayer && heads_up) adjustDeck(2,0,false);
	    return;
	}
	for (int n=start; n<suits*values; n++) {
	    tmp_val = n/suits;
	    tmp_suit = n%suits;
	    if (test_deck[tmp_val][tmp_suit]!=0) continue;
	    test_deck[tmp_val][tmp_suit] = 1;
	    if (depth>1) adjustDeck(depth-1,n+1,isplayer);
	    else {
		whatHands(isplayer);
		if (isplayer && heads_up) adjustDeck(2,0,false);
	    }
	    test_deck[tmp_val][tmp_suit] = 0;
	    if (start==0 && isplayer) pb.incrementProgressBy(1);
	    if (Thread.interrupted()) {
		throw new InterruptedException();
	    }
	}
    }

    public static int fractorial(int a) {
	if (a<=1) return 1;
	return a*fractorial(a-1);
    }

    public static long pow(int a, int b) {
	if (b==0) return 1;
	if (b==1) return (long) a;
	if (b/2==b/2.0) return pow(a*a, b/2);
	else return a*pow(a*a, b/2);
    }

    public long maxKindScore(int top, int ... level) {
	int max_power = top;
	long max = 0;
	long result_score = 0;
	int [] tmp_kcount = kind_count.clone();
	for (int m=0; m<top; m++) {
	    tmp_kcount[level[m]]--;
	    if (tmp_kcount[level[m]]<0) continue;
	    result_score += kind_score[level[m]][tmp_kcount[level[m]]]*value_powers[max_power];
	    max_power--;
	}
	for (int l=top; l<level.length; l++) {
	    tmp_kcount[level[l]]--;
	    if (tmp_kcount[level[l]]<0) continue;
	    long tmp_kind_score = kind_score[level[l]][tmp_kcount[level[l]]];
	    if (tmp_kind_score>max) max = tmp_kind_score;
	}
	return result_score+max;
    }
}
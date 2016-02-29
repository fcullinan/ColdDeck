package developer.cullinan.colddeck;

public class ChanceCalculator {

    private final static String TAG="ChanceCalculator";
    public int [][]  deck;
    public double [] chances;
    public boolean heads_up;
    public double headsup_chance;
    public double splitpot_chance;

    public ChanceCalculator() {
	chances = new double [9];
	heads_up = false;
	headsup_chance = 0;
    }

    public boolean addCard(int num, int suit, boolean hand){
	return true;
    }

    public double getHeadsUp(){
	return headsup_chance;
    }

    public double getSplitPot(){
	return splitpot_chance;
    }
    
    public void deleteLastCard(){
    }

    public void toggleHeadsUp(){
	if (heads_up) { heads_up = false; }
	else { heads_up = true; }
    }

    public void calcChances(boolean hu) throws InterruptedException {
	heads_up = hu;
	calcChances();
    }
    
    public void calcChances() throws InterruptedException {}

    public double [] getChances() {
	return chances;
    }
}
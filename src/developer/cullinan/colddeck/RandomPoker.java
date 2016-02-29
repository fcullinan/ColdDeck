package developer.cullinan.colddeck;

import java.util.Random;

public class RandomPoker extends ChanceCalculator {

    private final static String TAG="RandomCalculator";
    private Random randy;

    public RandomPoker() {
	randy = new Random();
    }

    public double [] getChances(){
	for (int n=0; n<9; n++){
	    chances[n] = randy.nextDouble()*100.;
	}
	if (heads_up) headsup_chance = randy.nextDouble()*100;
	return chances;
    }
}
package developer.cullinan.colddeck;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import android.widget.ProgressBar;

public class Table {
    //public BruteForce brutus;
    public BruteForce calc;
    public double [][][][] allchances;

    public Table(InputStream instream, ProgressBar pbt) throws IOException, ClassNotFoundException {
	//calc = new RandomPoker();
	calc = new BruteForce(pbt);
	ObjectInputStream in = new ObjectInputStream(instream);
	allchances = (double[][][][]) in.readObject();
	in.close();
	//calc = new BruteForceTwo();
    }

    private int getLastIndex() {
	return calc.suit_history[0]==calc.suit_history[1] ? 1:0;
    }

    public void calcChances(boolean pass) throws InterruptedException {
	if (pass) calc.calcChances(pass);
    }
    
    public double [] getChances(boolean pass) {
	if (pass) return calc.getChances();
	double [] tmp_chance = new double [9];
	int last_index = getLastIndex();
	for (int n=0; n<9; n++) tmp_chance[n] = allchances[calc.value_history[0]][calc.value_history[1]][last_index][n];
	return tmp_chance;
    }

    public double getHeadsUp(boolean calculated) {
	if (calculated) return calc.getHeadsUp();
        return allchances[calc.value_history[0]][calc.value_history[1]][getLastIndex()][9];
    }

    public double getSplitPot(boolean calculated) {
	if (calculated) return calc.getSplitPot();
        return allchances[calc.value_history[0]][calc.value_history[1]][getLastIndex()][10];
    }
}
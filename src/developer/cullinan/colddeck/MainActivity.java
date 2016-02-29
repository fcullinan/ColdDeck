package developer.cullinan.colddeck;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.content.res.AssetManager;

public class MainActivity extends Activity
{
    private final static String TAG="MainActivity";
    private AssetManager assetManager;
    private int deal_index;

    //public final static String EXTRA_CHANCES = "developer.cullinan.colddeck.CHANCES";
    //public final static String EXTRA_STATE = "developer.cullinan.colddeck.STATE";
    public final static String HEADSUPDOWN = "developer.cullinan.colddeck.HEADSUPDOWN";
    public final static String CHANCE_STAGECALC = "developer.cullinan.colddeck.STAGECALC";

    public final static String CHANCE_HAND = "developer.cullinan.colddeck.HAND";
    public final static String CHANCE_FLOP = "developer.cullinan.colddeck.FLOP";
    public final static String CHANCE_TURN = "developer.cullinan.colddeck.TURN";
    public final static String CHANCE_CURRENT = "developer.cullinan.colddeck.CURRENT";
    public final static String CHANCE_STAGE = "developer.cullinan.colddeck.STAGE";
    public final static String CHANCE_HEADSUP = "developer.cullinan.colddeck.HEADSUP";
    public final static String CHANCE_HANDHU = "developer.cullinan.colddeck.HANDHU";
    public final static String CHANCE_FLOPHU = "developer.cullinan.colddeck.FLOPHU";
    public final static String CHANCE_TURNHU = "developer.cullinan.colddeck.TURNHU";
    public final static String CHANCE_SPLITPOT = "developer.cullinan.colddeck.SPLITPOT";
    public final static String CHANCE_HANDSP = "developer.cullinan.colddeck.HANDSP";
    public final static String CHANCE_FLOPSP = "developer.cullinan.colddeck.FLOPSP";
    public final static String CHANCE_TURNSP = "developer.cullinan.colddeck.TURNSP";

    public Table table;
    public int [] card = {13,13};
    public int [] card_list = {R.id.num_two,R.id.num_three,R.id.num_four,
                               R.id.num_five,R.id.num_six,R.id.num_seven,
                               R.id.num_eight,R.id.num_nine,R.id.num_ten,
                               R.id.jack,R.id.queen,R.id.king,R.id.ace,
			       R.id.spades,R.id.clubs,R.id.hearts,R.id.diamonds};
    public int [] table_loc = {R.id.flop0,R.id.flop1,R.id.flop2,R.id.turn,R.id.river};
    public int [] hand_loc = {R.id.hand0,R.id.hand1};
    public int [] hand_draw = new int [2];
    public int [] edge_loc = {R.id.edge0,R.id.edge1};
    public int [] [] card_icons = new int [] [] {{R.drawable.twos,R.drawable.twoc,R.drawable.twoh,R.drawable.twod},
						 {R.drawable.threes,R.drawable.threec,R.drawable.threeh,R.drawable.threed},
						 {R.drawable.fours,R.drawable.fourc,R.drawable.fourh,R.drawable.fourd},
						 {R.drawable.fives,R.drawable.fivec,R.drawable.fiveh,R.drawable.fived},
						 {R.drawable.sixs,R.drawable.sixc,R.drawable.sixh,R.drawable.sixd},
						 {R.drawable.sevens,R.drawable.sevenc,R.drawable.sevenh,R.drawable.sevend},
						 {R.drawable.eights,R.drawable.eightc,R.drawable.eighth,R.drawable.eightd},
						 {R.drawable.nines,R.drawable.ninec,R.drawable.nineh,R.drawable.nined},
						 {R.drawable.tens,R.drawable.tenc,R.drawable.tenh,R.drawable.tend},
						 {R.drawable.jacks,R.drawable.jackc,R.drawable.jackh,R.drawable.jackd},
						 {R.drawable.queens,R.drawable.queenc,R.drawable.queenh,R.drawable.queend},
						 {R.drawable.kings,R.drawable.kingc,R.drawable.kingh,R.drawable.kingd},
						 {R.drawable.aces,R.drawable.acec,R.drawable.aceh,R.drawable.aced}};

    public final static int hands = 9;
    public final static int prechances = 3;

    public final int card_buttons = 17;
    public final int notsuits = 12;
    public final int inhand = 2;

    public boolean dealt;
    public boolean runthrough;
    public boolean hand;
    public double [] chances;
    public double [] [] pre_chance;
    public double [] pre_splitpot;
    public double [] pre_headsup;
    public double splitpot;
    public double headsup;
    public int stage;
    public Bundle allchances;
    public ProgressBar progress;
    public Button deal_button;

    public Intent intent;
    public int card_count;
    public boolean [] stage_calculated;
    Thread cooler;
    Runnable calculator;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	allchances = new Bundle();
	assetManager = getAssets();
	//progress = (ProgressBar) findViewById(R.id.progress_bar);
	progress = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
	//progress.setOnClickListener(new View.OnClickListener() {
	//	public void onClick(View view) {
	//	    deal(view);
	//	}
	//    });
	try {
	    table = new Table(assetManager.open("lookup/handchances.ser"),progress);
	}
	catch (Exception e) {
	    System.exit(1);
	}
        setContentView(R.layout.main);
	intent = new Intent(this, StatsActivity.class);
	chances = new double [hands];
	pre_chance = new double [prechances][hands];
	pre_splitpot = new double [prechances];
	pre_headsup = new double [prechances];
	stage_calculated = new boolean [prechances+1];
	card_count = 0;
	stage = 0;
	deal_index = -1;
	dealt = true;
	runthrough = false;
	hand = true;
	deal_button = (Button) findViewById(R.id.deal_button);
	progress.setLayoutParams(deal_button.getLayoutParams());
	progress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_colours));
	setCardButtonFont();
	cooler = new Thread();
	calculator = new Runnable() {
		public void run() {
		    boolean nothand = (stage!=1);
		    try {
			table.calcChances(nothand);
		    }
		    catch (InterruptedException e) {
			return;
		    }
		    stage_calculated[stage-1] = true;
		    showChances();
		}
	    };
    }

    private void setCardButtonFont() {
	Typeface tf = Typeface.createFromAsset(assetManager, "CARDC___.TTF");
	for (int c=0; c<card_buttons; c++) {
	    TextView tmp_cbut = (TextView) findViewById(card_list[c]);
	    tmp_cbut.setTypeface(tf);
	}
    }

    private void checkCalculator() {
	if (cooler.isAlive()) {
	    cooler.interrupt();
	    try {
		cooler.join();
	    }
	    catch (InterruptedException e) {
		System.exit(1);
	    }
	    restoreDealButton();
	}
    }

    public void update(View view){
	checkCalculator();
	if (card_count<7){
	    int card_idtmp = view.getId();
	    int card_id=0;
	    for (int n=0; n<card_buttons; n++){
		if (card_idtmp==card_list[n]) {
		    card_id = n;
		}
	    }
	    if (card_id>notsuits) {
		if (card_id==card[0]+notsuits+1 && false) { card[1] = 12; }
		else { card[0] = card_id-notsuits-1; }
	    }
	    else {
		card[1] = card_id;
	    }
	    if ((card[0]==13) || (card[1]==13)) {
		return;
	    }
	    else {
		if (card_count==2 && !dealt){
		    runthrough = true;
		    deal(null);
		    onActivityResult(0,0,null);
		}
		boolean isthere = table.calc.addCard(card[0],card[1],hand);
		int draw_tmp;
		int offset=1;
		if (isthere){ 
		    draw_tmp = card_icons[card[1]][card[0]];
		    card_count += 1;
		    dealt = !(card_count==2 || card_count>=5);
		}
		else {
		    draw_tmp = R.drawable.carddealt;
		    offset=0;
		}
		if (hand) {
		    ImageView imtmp = (ImageView) findViewById(hand_loc[card_count-offset]);
		    imtmp.setImageDrawable(getResources().getDrawable(draw_tmp));
		    if (isthere) hand_draw[card_count-1] = draw_tmp;
		}
		else {
		    ImageView imtmp = (ImageView) findViewById(table_loc[card_count-inhand-offset]);
		    imtmp.setImageDrawable(getResources().getDrawable(draw_tmp));
		}
		card[0] = 13;
		card[1] = 13;
	    }
	}
    }

    public void deal(View view){
	int new_stage = (card_count>=2 ? 1 : 0)+(card_count>=5 ? 1:0)*(card_count-4);
	card[0] = 13;
	card[1] = 13;
	if (new_stage!=stage) {
	    if (stage!=0) {
		int stage_diff = new_stage-stage;
		if (stage_diff>0) {
		    for (int s=4-new_stage; s<prechances-stage_diff; s+=stage_diff) {
			pre_chance[s] = pre_chance[s+stage_diff].clone();
			pre_splitpot[s] = 1*pre_splitpot[s+stage_diff];
			pre_headsup[s] = 1*pre_headsup[s+stage_diff];
		    }
		    pre_chance[prechances-stage_diff] = chances.clone();
		    pre_splitpot[prechances-stage_diff] = 1*splitpot;
		    pre_headsup[prechances-stage_diff] = 1*headsup;
		}
		else {
		    chances = pre_chance[prechances+stage_diff].clone();
		    splitpot = 1*pre_splitpot[prechances+stage_diff];
		    headsup = 1*pre_headsup[prechances+stage_diff];
		    for (int s=prechances-1; s>=-stage_diff; s+=stage_diff) {
			pre_chance[s] = pre_chance[s+stage_diff].clone();
			pre_splitpot[s] = 1*pre_splitpot[s+stage_diff];
			pre_headsup[s] = 1*pre_headsup[s+stage_diff];
		    }
		}
	    }
	    stage = 1*new_stage;
	}
	if (!dealt) {
	    if (stage>1 && stage<4 && !runthrough) {
		ViewGroup tmp_parent = (ViewGroup) view.getParent();
		deal_index = tmp_parent.indexOfChild(view);
		tmp_parent.removeView(view);
		progress.setProgress(0);
		tmp_parent.addView(progress,deal_index);
		tmp_parent.invalidate();
	    }
	    cooler = new Thread(calculator);
	    cooler.start();
	}
	else showChances();
    }

    public void restoreDealButton(){
	ViewGroup tmp_parent = (ViewGroup) progress.getParent();
	ViewGroup tmp_dbparent = (ViewGroup) deal_button.getParent();
	if (tmp_dbparent!=null) tmp_dbparent.removeView(deal_button);
	tmp_parent.removeView(progress);
	tmp_parent.addView(deal_button,deal_index);
	tmp_parent.invalidate();
    }
	
    public void showChances(){
	if (!dealt) {
	    boolean nothand = (stage!=1);
	    chances = table.getChances(nothand);
	    splitpot = table.getSplitPot(nothand);
	    headsup = table.getHeadsUp(nothand);
	}
	allchances.putDouble(CHANCE_SPLITPOT, splitpot);
	allchances.putDouble(CHANCE_HEADSUP, headsup);
	allchances.putDouble(CHANCE_HANDHU, pre_headsup[0]);
	allchances.putDouble(CHANCE_FLOPHU, pre_headsup[1]);
	allchances.putDouble(CHANCE_TURNHU, pre_headsup[2]);
	allchances.putDouble(CHANCE_HANDSP, pre_splitpot[0]);
	allchances.putDouble(CHANCE_FLOPSP, pre_splitpot[1]);
	allchances.putDouble(CHANCE_TURNSP, pre_splitpot[2]);
	allchances.putBoolean(HEADSUPDOWN, true);
	allchances.putBooleanArray(CHANCE_STAGECALC, stage_calculated);
	allchances.putDoubleArray(CHANCE_HAND, pre_chance[0]);
	allchances.putDoubleArray(CHANCE_FLOP, pre_chance[1]);
	allchances.putDoubleArray(CHANCE_TURN, pre_chance[2]);
	allchances.putDoubleArray(CHANCE_CURRENT, chances);
	allchances.putInt(CHANCE_STAGE, stage);
	intent.putExtras(allchances);
	if (!runthrough) startActivityForResult(intent, 0);
	runthrough = false;
    }

    public void dealHeadsUp(View view){	
	//Disabled feature
	table.calc.toggleHeadsUp();
	//this.deal(view);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
	if (card_count==2 && hand){
	    hand = false;
	    Bitmap [] resizedbmp = new Bitmap [2];
	    for (int n=0; n<2; n++){
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),hand_draw[n]);
		int bmp_height = bmp.getHeight();
		int bmp_height_twenty = bmp_height/100*22;
		int bmp_width = bmp.getWidth();
		resizedbmp[n] = Bitmap.createBitmap(bmp,0,0,bmp_width,bmp_height_twenty);
	    }
	    setContentView(R.layout.table);
	    for (int m=0; m<2; m++){
		ImageView imtmp2 = (ImageView) findViewById(edge_loc[m]);
		imtmp2.setImageBitmap(resizedbmp[m]);
	    }
	    setCardButtonFont();
	}
	else if (!dealt && stage>1 && stage<4) restoreDealButton();
	dealt=true;
    }

    public void deleteCard(View view){
	checkCalculator();
	if (card_count-(hand ? 0:1)*inhand!=0) {
	    card_count -= 1;
	    if (card_count>=inhand) {
		int stage_min = (card_count==2 ? 1 : 0)+(card_count>4 ? 1:0)*(card_count-3);
		if (stage_min!=0) {
		    dealt = stage_calculated[stage_min-1];
		    for (int s=stage_min; s<prechances+1; s++) stage_calculated[s] = false;
		}
		else {
		    if (card_count==4) stage_calculated[1] = false;
		    dealt = true;
		}
	    }
	    ImageView imtmp;
	    if (hand) imtmp = (ImageView) findViewById(hand_loc[card_count]);
	    else imtmp = (ImageView) findViewById(table_loc[card_count-inhand]);
	    imtmp.setImageDrawable(getResources().getDrawable(R.drawable.nocard));
	    table.calc.deleteLastCard();
	    card[0] = 13;
	    card[1] = 13;
	}
    }
    
    public void clearTable(View view){
	checkCalculator();
	onCreate(null);
	for (int n=0; n<2; n++){
	    card[n] = 13;
	    ImageView imtmp = (ImageView) findViewById(hand_loc[n]);
	    imtmp.setImageDrawable(getResources().getDrawable(R.drawable.nocard));
	}
    }
}

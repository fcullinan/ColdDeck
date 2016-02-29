package developer.cullinan.colddeck;

import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.view.Window;
import android.content.Intent;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.util.TypedValue;

public class StatsActivity extends Activity
{
    static final String STATE_HANDCHANCE = "developer.cullinan.colddeck.HANDCHANCE";
    static final String STATE_FLOPCHANCE = "developer.cullinan.colddeck.FLOPCHANCE";
    static final String STATE_TURNCHANCE = "developer.cullinan.colddeck.TURNCHANCE";
    static final String STATE_CHANCES = "developer.cullinan.colddeck.CHANCES";
    static final String STATE_STAGE = "developer.cullinan.colddeck.STAGE";

    private final static String TAG="StatsActivity";
    private LayoutParams layout_vert;

    public int [] chance_loc;
    public int hands = 9;
    public int stage = 0;
    public double [][] pre_chance;
    public double [] chances;
    public boolean [] stage_calc;
    public String [] hand_names = {"No hand","Pair","Two pair","Three of a kind",
				   "Straight","Flush","Full house",
				   "Four of a Kind","Straight flush"}; 
    public String [] stage_names = {"No cards dealt","Pre-flop","After flop","After turn","After river"};

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	
	int prechances = MainActivity.prechances;
	int hands = MainActivity.hands;
	pre_chance = new double [prechances][hands];
	chances = new double [hands];
	
	Intent intent = getIntent();
	
	Bundle allchances = intent.getExtras();
	stage = allchances.getInt(MainActivity.CHANCE_STAGE);
	setTitle(stage_names[stage]);
	if (stage>0) {
	    chances = allchances.getDoubleArray(MainActivity.CHANCE_CURRENT);
	    pre_chance[0] = allchances.getDoubleArray(MainActivity.CHANCE_HAND);
	    pre_chance[1] = allchances.getDoubleArray(MainActivity.CHANCE_FLOP);
	    pre_chance[2] = allchances.getDoubleArray(MainActivity.CHANCE_TURN);
	}
	stage_calc = allchances.getBooleanArray(MainActivity.CHANCE_STAGECALC);

	layout_vert = new LayoutParams(0,LayoutParams.FILL_PARENT,1);
	LayoutParams layout_hori = new LayoutParams(LayoutParams.FILL_PARENT,0,1);
	LayoutParams layout_hori_2 = new LayoutParams(LayoutParams.FILL_PARENT,0,2);
	final LinearLayout main_view = new LinearLayout(this);
	main_view.setOrientation(LinearLayout.HORIZONTAL);
	main_view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						   LayoutParams.FILL_PARENT));

	LinearLayout first_column = new LinearLayout(this);
	first_column.setOrientation(LinearLayout.VERTICAL);
	first_column.setLayoutParams(new LayoutParams(0,LayoutParams.FILL_PARENT,3));

	LinearLayout second_column = new LinearLayout(this);
	second_column.setOrientation(LinearLayout.VERTICAL);
	second_column.setLayoutParams(new LayoutParams(0,LayoutParams.FILL_PARENT,2));

	for (int n=0; n<hands; n++){
	    TextView tv = new TextView(this);
	    tv.setTextColor(0xFFFFF000);
	    tv.setLayoutParams(layout_hori);
	    tv.setText(hand_names[n]);
	    LinearLayout tmp = new LinearLayout(this);
	    tmp.setOrientation(LinearLayout.HORIZONTAL);
	    tmp.setLayoutParams(layout_hori);

	    for (int m=0; m<prechances; m++){
		TextView tv_chance = new TextView(this);
		tv_chance.setTextColor(0xFFFFF000);
		tv_chance.setGravity(Gravity.RIGHT);
		tv_chance.setLayoutParams(layout_vert);
		if (stage>prechances-m && stage_calc[stage-prechances+m-1]) displayChance(tv_chance, pre_chance[m][n]);
		else tv_chance.setText("-");
		tmp.addView(tv_chance);
	    }
	    TextView tv_chance = new TextView(this);
	    tv_chance.setTextColor(0xFFFFF000);
	    tv_chance.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
	    tv_chance.setTypeface(null, Typeface.BOLD);
	    tv_chance.setLayoutParams(layout_hori_2);
	    tv_chance.setGravity(Gravity.RIGHT);
	    if (stage>0 && stage_calc[stage-1]) displayChance(tv_chance,chances[n]);
	    else tv_chance.setText("- ");
	    second_column.addView(tv_chance);

	    first_column.addView(tv);
	    first_column.addView(tmp);
	    //put percentages into layout
	}

	View.OnClickListener listener_return = new View.OnClickListener() {
		public void onClick(View v) {
		    Intent intent = new Intent();
		    setResult(RESULT_OK, intent);
		    finish();
		}
	    };

	main_view.addView(first_column);
	main_view.addView(second_column);
	main_view.setOnClickListener(listener_return);    
	
	if (allchances.getBoolean(MainActivity.HEADSUPDOWN,false)) {

	    final LinearLayout h2h = new LinearLayout(this);
	    h2h.setOrientation(LinearLayout.VERTICAL);
	    h2h.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						 LayoutParams.FILL_PARENT));
	    h2h.setPadding(3,3,3,3);

	    LinearLayout h2h_nmbr = new LinearLayout(this);
	    h2h_nmbr.setOrientation(LinearLayout.HORIZONTAL);
	    h2h_nmbr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,0,8));

	    LinearLayout h2h_column1 = new LinearLayout(this);
	    h2h_column1.setOrientation(LinearLayout.VERTICAL);
	    h2h_column1.setLayoutParams(new LayoutParams(0,LayoutParams.FILL_PARENT,1));
	    
	    LinearLayout h2h_column2 = new LinearLayout(this);
	    h2h_column2.setOrientation(LinearLayout.VERTICAL);
	    h2h_column2.setLayoutParams(new LayoutParams(0,LayoutParams.FILL_PARENT,1));

	    TextView h2h_name = new TextView(this);
	    h2h_name.setLayoutParams(layout_hori_2);
	    h2h_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
	    h2h_name.setGravity(Gravity.CENTER);
	    h2h_name.setTextColor(0xFFFFF000);
	    h2h_name.setTypeface(null,Typeface.BOLD);
	    h2h_name.setText("Heads Up");
	    //LinearLayout h2h_win = new LinearLayout(this);
	    //h2h_win.setOrientation(LinearLayout.HORIZONTAL);
	    //h2h_win.setLayoutParams(layout_hori_2);

	    TextView win_name = new TextView(this);
	    win_name.setLayoutParams(layout_hori_2);
	    win_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
	    win_name.setTextColor(0xFFFFF000);
	    win_name.setText("Win");
	    //LinearLayout h2h_split = new LinearLayout(this);
	    //h2h_split.setOrientation(LinearLayout.HORIZONTAL);
	    //h2h_split.setLayoutParams(layout_hori_2);

	    TextView split_name = new TextView(this);
	    split_name.setLayoutParams(layout_hori_2);
	    split_name.setTextColor(0xFFFFF000);
	    split_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
	    split_name.setText("Split pot");

	    TextView loss_name = new TextView(this);
	    loss_name.setLayoutParams(layout_hori_2);
	    loss_name.setTextColor(0xFFFFF000);
	    loss_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
	    loss_name.setText("Lose");

	    double win_chance = allchances.getDouble(MainActivity.CHANCE_HEADSUP,0.0);
	    double split_chance = allchances.getDouble(MainActivity.CHANCE_SPLITPOT,0.0);
	    double [] pre_win = {allchances.getDouble(MainActivity.CHANCE_HANDHU,0.0),
				 allchances.getDouble(MainActivity.CHANCE_FLOPHU,0.0),
				 allchances.getDouble(MainActivity.CHANCE_TURNHU,0.0)};
	    double [] pre_split =  {allchances.getDouble(MainActivity.CHANCE_HANDSP,0.0),
				    allchances.getDouble(MainActivity.CHANCE_FLOPSP,0.0),
				    allchances.getDouble(MainActivity.CHANCE_TURNSP,0.0)};
	    double [] pre_loss = new double [prechances];
	    for (int i=0; i<prechances; i++) pre_loss[i] = 100.0-pre_win[i]-pre_loss[i];

	    LinearLayout ll_win = new LinearLayout(this);
	    ll_win.setLayoutParams(layout_hori);
	    fillHuLine(ll_win,pre_win);

	    LinearLayout ll_split = new LinearLayout(this);
	    ll_split.setLayoutParams(layout_hori);
	    fillHuLine(ll_split,pre_split);

	    LinearLayout ll_loss = new LinearLayout(this);
	    ll_loss.setLayoutParams(layout_hori);
	    fillHuLine(ll_loss,pre_loss);

	    TextView tv_win = new TextView(this);
	    tv_win.setTextColor(0xFFFFF000);
	    tv_win.setLayoutParams(layout_hori_2);
	    tv_win.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);
	    tv_win.setGravity(Gravity.RIGHT);
	    tv_win.setTypeface(null, Typeface.BOLD);
	    tv_win.setTextScaleX(0.6f);

	    TextView tv_split = new TextView(this);
	    tv_split.setTextColor(0xFFFFF000);
	    tv_split.setLayoutParams(layout_hori_2);
	    tv_split.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);
	    tv_split.setGravity(Gravity.RIGHT);
	    tv_split.setTypeface(null, Typeface.BOLD);
	    tv_split.setTextScaleX(0.6f);

	    TextView tv_loss = new TextView(this);
	    tv_loss.setTextColor(0xFFFFF000);
	    tv_loss.setLayoutParams(layout_hori_2);
	    tv_loss.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);
	    tv_loss.setGravity(Gravity.RIGHT);
	    tv_loss.setTypeface(null, Typeface.BOLD);
	    tv_loss.setTextScaleX(0.6f);

	    if (stage>0 && stage_calc[stage-1]) {
		displayChance(tv_win,win_chance);
		displayChance(tv_split,split_chance);
		displayChance(tv_loss,100.0-win_chance-split_chance);
	    }
	    else {
		tv_win.setText("-");
		tv_split.setText("-");
		tv_loss.setText("-");
	    }
	    
	    //h2h_win.addView(win);
	    //h2h_win.addView(tv_h2h);
	    h2h_column1.addView(win_name);
	    //h2h.addView(tv_win);
	    h2h_column1.addView(ll_win);
	    //h2h_split.addView(split);
	    //h2h_split.addView(tv_split);
	    h2h_column1.addView(loss_name);
	    //h2h.addView(tv_loss);
	    h2h_column1.addView(ll_loss);
	    h2h_column1.addView(split_name);
	    //h2h.addView(tv_split);
	    h2h_column1.addView(ll_split);

	    h2h_column2.addView(tv_win);
	    h2h_column2.addView(tv_loss);
	    h2h_column2.addView(tv_split);

	    h2h_nmbr.addView(h2h_column1);
	    h2h_nmbr.addView(h2h_column2);

	    h2h.addView(h2h_name);
	    h2h.addView(h2h_nmbr);
	    
	    h2h.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
			setContentView(main_view);
		    }
		});
	    setContentView(h2h);
	}
	else {
	    setContentView(main_view);
	}			
    }

    @Override
    public void onPause() {
	super.onPause();
    }

    @Override
    public void onResume(){
	super.onResume();
    }    

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
	savedInstanceState.putDoubleArray(STATE_HANDCHANCE,pre_chance[0]);
	savedInstanceState.putDoubleArray(STATE_FLOPCHANCE,pre_chance[1]);
	savedInstanceState.putDoubleArray(STATE_TURNCHANCE,pre_chance[2]);
	savedInstanceState.putDoubleArray(STATE_CHANCES,chances);
	savedInstanceState.putInt(STATE_STAGE,stage);
	
	super.onSaveInstanceState(savedInstanceState);
    }

    private void displayChance(TextView view, double chance) {
	if (chance==0 || chance==100) {
	    view.setTypeface(Typeface.createFromAsset(getAssets(), "WebSymbols-Regular.otf"));
	    if (chance==0) view.setText("'");
	    else view.setText(".");
	}
	else if (chance<1) view.setText(String.format("%.2f %%", chance));
	else view.setText(String.format("%.1f %%", chance));
    }

    private void fillHuLine(LinearLayout linlay, double [] pre) {

	int prc = MainActivity.prechances;
	for (int n=0; n<prc; n++) {
	    TextView tv_tmp = new TextView(this);
	    tv_tmp.setLayoutParams(layout_vert);
	    tv_tmp.setTextColor(0xFFFFF000);
	    tv_tmp.setGravity(Gravity.RIGHT);
	    //tv_tmp.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
	    if (stage>prc-n && stage_calc[stage-prc+n-1]) displayChance(tv_tmp,pre[n]);
	    else tv_tmp.setText("-");
	    linlay.addView(tv_tmp);
	}
    }
}
package net.danielroggen.dconsole;

/*
	DConsole: emulates a console interface allowing to print text to a TextView.

	2018 Daniel Roggen

	Usage:
	1)	The application layout must define a TextView within a ScrollView
	2)	The activity using the console must instantiate a DConsole object on onCreate
	3)	The activity using the console must call DConsole.onResume and DConsole.onPause within its own onResume and onPause
	4)	Printing is done with DConsole.print; the text is only displayed after a DConsole.display or DConsole.update call.
		DConsole.display renders the text immediately. DConsole.update delays the rendering and is faster if multiple calls to
		DConsole.print are made in rapid succession.

	Key methods:
		onResume
		onPause
		clear
		setMaxLines
		print
		display
		update


 */


import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class DConsole {


	private String ConsoleText;							// Buffer for the console text (limited to

	private TextView ConsoleTextView=null;
	private ScrollView ConsoleScrollView=null;

	private boolean param_consoleLimitLines = true;		// True if the terminal has a maximum number of lines
	private int param_consoleMaxLines = 1000;			// Maximum number of lines
	private int skippedlines=0;							// Number of lines truncated to date

	private boolean param_showSkippedLines =false;		// Show in the console whether lines are skipped
	private boolean param_startFromBottom = false;		// If true the first line appears at the bottom of the console.

	// Delayed display logic
	private CountDownTimer timerDisplay=null;
	private boolean _consoleupdateinprogress =false;
	private final int param_consoleUpdateDelayMs=250;		// ms




	/*
		Constructor
	 */
	public DConsole() {
		ConsoleText = "";

		// Create the timer logic to update the screen with a delay.
		// This creates a single shot timer, which will fire after param_consoleUpdateDelayMs.
		// Upon firing (onFinish) the string to display is prepared and set in the TextView, and a runnable is
		// posted to the scrollView after some delay to scroll to the bottom.


		timerDisplay = new CountDownTimer(param_consoleUpdateDelayMs,param_consoleUpdateDelayMs) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {

				_consoleupdateinprogress = false;

				// Display the text
				if(ConsoleTextView!=null) {
					//long t1 = System.nanoTime();

					String strtodisplay = displayPrepare();

					ConsoleTextView.setText(strtodisplay);

					//long t2 = System.nanoTime();
					//Log.i(getClass().getSimpleName(),"setText dt: " + ((t2-t1)/1000) + " us");
				}



				if(ConsoleScrollView!=null) {
					ConsoleScrollView.postDelayed(new Runnable() {
						public void run() {
							if(ConsoleScrollView!=null) {
								//long t1 = System.nanoTime();
								ConsoleScrollView.fullScroll(View.FOCUS_DOWN);
								//long t2 = System.nanoTime();
								//Log.i(getClass().getSimpleName(),"fullScroll dt: " + ((t2-t1)/1000) + " us");
							}
						}

					},50);
				}
			}	// onFinish
		};	//  new CountDownTimer


	}
	/*
		Must be called by the activity's onResume with a textview and scrollview used for the console
	 */
	public void onResume(TextView tv, ScrollView sv) {
		ConsoleTextView = tv;
		ConsoleScrollView = sv;

		// Display console. Addresses situation where a delayed update is triggered and the window is hidden (paused) before the
		// display is updated, then resumed.
		display();
	}
	/*
		Must be called by the activity's onPause; invalidates the textview and scrollview.
	 */
	public void onPause() {
		ConsoleTextView = null;
		ConsoleScrollView = null;
	}
	/*
		Clear the console text buffer.
	 */
	public void clear() {
		ConsoleText="";
		skippedlines=0;
	}
	/*
		Set the maximum number of lines to display. If 0, then unlimited.
	 */
	public void setMaxLines(int m) {
		assert(m>=0);
		if(m==0) {
			param_consoleLimitLines =false;
		}
		else {
			param_consoleMaxLines = m;
		}
		skippedlines = 0;
	}
	/*
		Returns the console text buffer (possibly truncated and padded).
	 */
	public String getConsoleText() {
		return ConsoleText;
	}


	/*
		Size limits the display buffer and optionally shows how many lines are skipped.
	 */
	private String displayPrepare() {
		String strtodisplay = ConsoleText;

		if(param_consoleLimitLines) {
			// If the buffer is too long we size limit
			int l = stringCountLines(ConsoleText);
			if(l> param_consoleMaxLines) {
				skippedlines += (l - param_consoleMaxLines);
				ConsoleText = stringTruncLines(ConsoleText, param_consoleMaxLines, param_startFromBottom);
			}
			else {
				// "Trunc" when not size limited to padd the console
				ConsoleText = stringTruncLines(ConsoleText, param_consoleMaxLines, param_startFromBottom);
			}
			// If size limited we optionally display the number of lines which are skipped.
			if(param_showSkippedLines && skippedlines>0) {
				strtodisplay = "... skipped " + skippedlines + " lines...\n" + ConsoleText;
			}
			else {
				strtodisplay = ConsoleText;
			}
		}
		return strtodisplay;
	}

	/*
		Displays the content immediately. This may be slow if frequent calls to this function are made in succession.
	 */
	public void display() {
		String strtodisplay = displayPrepare();
		// Display
		if(ConsoleTextView!=null) {
			ConsoleTextView.setText(strtodisplay);
		}
		if(ConsoleScrollView!=null) {
			ConsoleScrollView.fullScroll(View.FOCUS_DOWN);
		}
	}
	/*
		Update the console with a maximum update rate, to minimise cpu load.
	 */
	public void update() {
		//Log.i("Display terminal","Called with string length: " + str.length());
		if(_consoleupdateinprogress == false) {
			_consoleupdateinprogress = true;
			timerDisplay.start();
		}


	}





	/*
		Add a string to the terminal buffer
	 */
	public void print(String str) {
		ConsoleText = ConsoleText + str;


	}


	/*
	 * expand=false: Returns the last n lines of the string, or the entire string if the number of lines is less than n.
	 * expand=true:  Returns exactly n lines. If the string has less than n lines newlines are prepended.
	 *
	 * Lines are defined by \n (the number of lines is the number of \n minus one).
	 * An empty string (as input or output) counts as one line.	 *
	 */
	private static String stringTruncLines(String str,int n,boolean expand)
	{
		// Find the nth last \n
		int start = str.length();
		int startstring=0;
		int i;
		for(i=0;i<n;i++) {
			int nl = str.lastIndexOf('\n', start-1);
			if(nl==-1) {
				startstring=-1;
				break;
			}
			start = nl;
			startstring=nl;
		}

		// i: number of \n in the string (lines = i+1)
		//Log.i("Lastlines2","i: " + i + " startstring: " + startstring);
		if(expand==false)
			return str.substring(startstring+1,str.length());

		if(i==n)
			return str.substring(startstring+1,str.length());

		String nl="";
		for(int j=0;j<n-i-1;j++)
			nl += "\n";
		return nl+str;
	}
	
	private static int stringCountLines(String str) {
		int start = 0;
		int i=0;
		while(true) {
			int nl = str.indexOf("\n", start);
			if(nl==-1) {
				break;
			}
			start = nl+1;
			i++;
		}
		return i+1;
	}
	


}

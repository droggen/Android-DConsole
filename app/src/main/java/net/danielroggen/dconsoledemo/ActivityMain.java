package net.danielroggen.dconsoledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import net.danielroggen.dconsole.DConsole;

public class ActivityMain extends AppCompatActivity {
    DConsole console=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = new DConsole();

        console.print("Hello world\n");
        console.display();
    }
    @Override
    protected void onResume() {
        super.onResume();

        console.onResume((TextView) findViewById(R.id.textViewConsole),(ScrollView) findViewById(R.id.scrollViewConsole));
    }
    @Override
    protected void onPause() {
        super.onPause();

        console.onPause();
    }


    public void buttonTop1OnClick(View view) {
        console.print("Button 1\n");
        console.display();
    }
    public void buttonTop2OnClick(View view) {
        console.print("Button 2\n");
        console.display();

    }
    public void buttonTop3OnClick(View view) {
        console.print("Button 3 (no newline)");
        console.display();
    }
    public void buttonTop4OnClick(View view) {
        long t1,t2;


        t1 = System.nanoTime();

        for(int i=0;i<1000;i++)
            console.print("Button 4: " + i + "\n");

        t2 = System.nanoTime();
        String str1 = "1000 prints dt: " + ((t2-t1)/1000) + " us\n";


        t1 = System.nanoTime();

        console.display();

        t2 = System.nanoTime();
        String str2 = "Single display dt: " + ((t2-t1)/1000) + " us\n";

        console.print(str1);
        console.print(str2);
        console.display();

    }
    public void buttonTop5OnClick(View view) {
        long t1,t2;


        t1 = System.nanoTime();

        for(int i=0;i<1000;i++) {
            console.print("Button 4: " + i + "\n");
            console.display();
        }

        t2 = System.nanoTime();
        String str1 = "1000 print+display dt: " + ((t2-t1)/1000) + " us\n";
        console.print(str1);
        console.display();



    }
    public void buttonTop6OnClick(View view) {
        long t1,t2;


        t1 = System.nanoTime();

        for(int i=0;i<1000;i++) {
            console.print("Button 4: " + i + "\n");
            console.update();
        }

        t2 = System.nanoTime();
        String str1 = "1000 print+update dt: " + ((t2-t1)/1000) + " us\n";
        console.print(str1);
        console.display();


    }
}

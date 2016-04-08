package com.anthony.imageprocessing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;

/**
 * Created by anthonyliu on 15/9/13.
 */
public class ColorSelection extends AppCompatActivity{

    private TextView txtReplace;
    private ImageView img1;
    private ImageView img2;
    private ColorPicker picker1;
    private ColorPicker picker2;

    private int replacedColor;
    private int replaceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);

        picker1 = (ColorPicker) findViewById(R.id.picker1);
        picker1.setOldCenterColor(Color.parseColor("#FF222222"));
        picker1.setNewCenterColor(picker1.getColor());
        picker1.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                replacedColor = i;
                img1.setBackgroundColor(replacedColor);
            }
        });

        picker2 = (ColorPicker) findViewById(R.id.picker2);
        picker2.setOldCenterColor(Color.parseColor("#FF222222"));
        picker2.setNewCenterColor(picker2.getColor());
        picker2.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                replaceColor = i;
                img2.setBackgroundColor(replaceColor);
            }
        });

        replacedColor = picker1.getColor();
        replaceColor = picker2.getColor();

        img1 = (ImageView) findViewById(R.id.img1);
        img1.setBackgroundColor(replacedColor);
        img2 = (ImageView) findViewById(R.id.img2);
        img2.setBackgroundColor(replaceColor);

        txtReplace = (TextView) findViewById(R.id.txtReplace);
        txtReplace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void done(){

        Intent returnIntent = new Intent();
        returnIntent.putExtra(IntentKeys.REPLACED_COLOR, replacedColor);
        returnIntent.putExtra(IntentKeys.REPLACING_COLOR, replaceColor);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

}
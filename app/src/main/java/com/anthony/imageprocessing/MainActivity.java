package com.anthony.imageprocessing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

/**
 * This project is dedicated to illustrating image processing.
 * <p/>
 * More instruction and samples can be found here:
 * "https://xjaphx.wordpress.com/learning/tutorials/"
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE = 99;
    private static final int REQUEST_CODE_SELECT_COLOR = 111;

    private int effectType;
    private int effectValue;

    private int replacedColor;
    private int replacingColor;

    private Bitmap bitmap;
    private ImageProcessor imageProcessor;

    private FloatingActionButton selectImage;
    private ImageViewTouch imgOriginal;
    private ImageViewTouch imgEffect;
    private ProgressDialog pgdProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgOriginal = (ImageViewTouch) findViewById(R.id.imgOriginal);
        imgEffect = (ImageViewTouch) findViewById(R.id.imgEffect);

        selectImage = (FloatingActionButton) findViewById(R.id.fab_add);
        selectImage.setOnClickListener(this);

        imageProcessor = new ImageProcessor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (bitmap == null) {

            Toast.makeText(MainActivity.this, R.string.please_select_an_image_first, Toast.LENGTH_LONG).show();

        } else {

            switch (item.getItemId()) {
                case R.id.effect0:
                    effectType = 0;
                    Intent intent = new Intent(MainActivity.this, ColorSelection.class);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_COLOR);
                    break;
                case R.id.effect1:
                    effectType = 1;
                    askForInput(0, 10);
                    break;
                case R.id.effect2:
                    effectType = 2;
                    showEffect();
                    break;
                case R.id.effect3:
                    effectType = 3;
                    askForInput(0, 10);
                    break;
                case R.id.effect4:
                    effectType = 4;
                    askForInput(-100, 100);
                    break;
                case R.id.effect5:
                    effectType = 5;
                    askForInput(0, 255);
                    break;
                default:
                    break;
            }

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add:

                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_SELECT_COLOR) {

                Bundle extras = data.getExtras();

                if (extras != null) {

                    replacedColor = extras.getInt(IntentKeys.REPLACED_COLOR);
                    replacingColor = extras.getInt(IntentKeys.REPLACING_COLOR);

                    showEffect();
                }
            }

            if (requestCode == REQUEST_CODE) {
                if (data != null) {

                    ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(photos.get(0), options);
                    imgOriginal.setImageBitmap(bitmap);
                    imgEffect.setImageBitmap(bitmap);
                }
            }

        }
    }

    private void showEffect() {

        imageProcessor.setImage(bitmap);
        showLoadingWheel();
        new processEffect().execute();

    }

    // Ask user for an input for certain effects
    private void askForInput(final int min, final int max) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialog = getResources().getString(R.string.user_enter_value);
        String mDialog = String.format(dialog, min, max);
        builder.setTitle(mDialog);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {

                    effectValue = Integer.valueOf(input.getText().toString());

                    if (effectValue >= min && effectValue <= max) {
                        showEffect();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.please_enter_a_specified_value, Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, R.string.please_enter_a_specified_value, Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    class processEffect extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {

            switch (effectType) {
                case 0:
                    return imageProcessor.getEffect1(replacedColor == 0 ? Color.BLACK : replacedColor, replacingColor == 0 ? Color.WHITE : replacingColor);
                case 1:
                    return imageProcessor.getEffect2(effectValue);
                case 2:
                    return imageProcessor.getEffect3();
                case 3:
                    return imageProcessor.getEffect4(effectValue);
                case 4:
                    return imageProcessor.getEffect5(effectValue);
                case 5:
                    return imageProcessor.getEffect6(effectValue);
                default:
                    return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            imgEffect.setImageBitmap(bitmap);
            dismissLoadingWheel();
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    private void showLoadingWheel() {
        if (pgdProcessing == null) {
            pgdProcessing = new ProgressDialog(MainActivity.this);
            pgdProcessing.setTitle(R.string.processing);
            pgdProcessing.setProgressStyle(pgdProcessing.STYLE_SPINNER);
            pgdProcessing.setCancelable(false);
            pgdProcessing.setCanceledOnTouchOutside(false);
        }
        pgdProcessing.show();
    }

    private void dismissLoadingWheel() {
        if (pgdProcessing != null)
            pgdProcessing.dismiss();
    }

}

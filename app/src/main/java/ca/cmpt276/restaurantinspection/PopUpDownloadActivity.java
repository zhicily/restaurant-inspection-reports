package ca.cmpt276.restaurantinspection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import ca.cmpt276.restaurantinspection.Model.DataManager;
import ca.cmpt276.restaurantinspection.Model.UpdateManager;

/** Pop-up for Signalling New Download is Available **/
public class PopUpDownloadActivity extends AppCompatActivity {
    UpdateManager updateManager = UpdateManager.getInstance();
    DataManager dataManager = DataManager.getInstance();
    Thread download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_download);

        setFinishOnTouchOutside(false);

        SharedPreferences pref = this.getSharedPreferences("UpdatePref", 0);
        final SharedPreferences.Editor EDITOR = pref.edit();
        setScreenSize();
        cancelBtnPressed();
        proceedBtnPressed(EDITOR);
    }

    private void setScreenSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .6));
    }

    private void proceedBtnPressed(final SharedPreferences.Editor EDITOR) {
        download = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent backToUpdate = new Intent();
                dataManager.readSecondURLForRestaurantData();
                dataManager.readSecondURLForInspectionData();

                /** Will exit sleep once update is complete (set to 1) **/
                try {
                    while (updateManager.getUpdated() != 1) {
                        Thread.sleep(10);
                    }
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                if (updateManager.getCancelled() != 1) {
                    /** SAVE: Last Modified Restaurant/Inspection Time (by server) **/
                    EDITOR.putString("last_modified_restaurants_by_server",
                            updateManager.getLastModifiedRestaurants());
                    EDITOR.putString("last_modified_inspections_by_server",
                            updateManager.getLastModifiedInspections());
                    EDITOR.apply();

                    setResult(PopUpDownloadActivity.RESULT_OK, backToUpdate);
                    finish();
                }

                setResult(RESULT_CANCELED, backToUpdate);
                finish();
            }
        });

        download.start();
    }

    private void cancelBtnPressed() {
        Button btn = findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download.interrupt();
                updateManager.setCancelled(1);

                Intent backToUpdate = new Intent();
                setResult(RESULT_CANCELED, backToUpdate);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        download.interrupt();
        updateManager.setCancelled(1);

        Intent backToUpdate = new Intent();
        setResult(RESULT_CANCELED, backToUpdate);
        finish();
    }
}

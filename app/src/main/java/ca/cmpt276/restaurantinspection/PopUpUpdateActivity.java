package ca.cmpt276.restaurantinspection;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import ca.cmpt276.restaurantinspection.Model.UpdateManager;

/** Pop-up to Signal that Data Download is In Progress **/
public class PopUpUpdateActivity extends AppCompatActivity {
    private UpdateManager updateManager = UpdateManager.getInstance();
    private final int REQUEST_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_update);

        setFinishOnTouchOutside(false);
        setScreenSize();
        proceedBtnPressed();
        cancelBtnPressed();
    }

    private void setScreenSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .6));
    }

    private void cancelBtnPressed() {
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateManager.setCancelled(1);
                finish();
            }
        });
    }

    private void proceedBtnPressed() {
        Button btn_proceed = findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = makePopUpDownloadIntent(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        updateManager.setCancelled(1);
        finish();
    }

    public static Intent makePopUpDownloadIntent(Context c){
        return new Intent(c, PopUpDownloadActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Intent backToMap = new Intent();
            setResult(PopUpDownloadActivity.RESULT_OK, backToMap);
            finish();
        }
        else if (requestCode == REQUEST_CODE && resultCode == RESULT_CANCELED) {
            updateManager.setCancelled(1);

            Intent backToMap = new Intent();
            setResult(PopUpDownloadActivity.RESULT_CANCELED, backToMap);
            finish();
        }
    }
}

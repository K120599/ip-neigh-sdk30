package it.sangiorgisrl.ipneighforandroid.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import it.alessangiorgi.ipneigh30.ArpNDK;
import it.sangiorgisrl.ipneighforandroid.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView arptable_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arptable_res = findViewById(R.id.arptable);

        // Get the ArrayList from ArpNDK.getARP()
        ArrayList<String> arpList = ArpNDK.getARP();

        // Convert the ArrayList elements to a single string
        StringBuilder arpString = new StringBuilder();
        for (String arpEntry : arpList) {
            arpString.append(arpEntry).append("\n"); // Append each entry with a new line
        }

        // Set the converted string as the text of the TextView
        arptable_res.setText(arpString.toString());
    }
}

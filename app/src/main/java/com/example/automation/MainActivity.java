package com.example.automation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.automation.services.FloatingViewService;
import com.example.automation.services.TapAccessibilityService;

public class MainActivity extends AppCompatActivity {

    int clicks = 0;

    int min = 0;
    int max = 0;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button finger_button = findViewById(R.id.finger_service);
        Button clicker_button = findViewById(R.id.clicker);

        if (!Settings.canDrawOverlays(MainActivity.this)){
            Toast.makeText(MainActivity.this, "Please Enable Display over other apps", Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        if(!isAccessibilityServiceEnabled(this, TapAccessibilityService.class)){
            Toast.makeText(MainActivity.this, "please enable accessibility service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        clicker_button.setOnClickListener(view -> {
            clicks++;
            clicker_button.setText(String.valueOf(clicks));
        });

        finger_button.setOnClickListener(view ->
                {


                    Intent service = new Intent(MainActivity.this, FloatingViewService.class);

                    EditText min_editText =  findViewById(R.id.min);
                    EditText max_editText =  findViewById(R.id.max);
                    EditText counter_editText =  findViewById(R.id.counter);

                    min = Integer.parseInt(min_editText.getText().toString());
                    max = Integer.parseInt(max_editText.getText().toString());
                    counter = Integer.parseInt(counter_editText.getText().toString());

                    service.putExtra("min", min);
                    service.putExtra("max", max);
                    service.putExtra("counter", counter);

                    getApplicationContext().startService(service);
                }
        );

    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }
}
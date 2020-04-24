package com.snakydesign.lives.testapplicaiton;

import android.os.Bundle;
import android.widget.Toast;

import com.snakydesign.livedataextensions.Lives;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
        Lives.first(mutableLiveData).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer anInt) {
                Toast.makeText(MainActivity.this, "HELLO" + String.valueOf(anInt), Toast.LENGTH_SHORT).show();
            }
        });
        mutableLiveData.setValue(2);
    }
}

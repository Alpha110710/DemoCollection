package com.lanou.chenfengyao.smalldemos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lanou.chenfengyao.smalldemos.expandrv.MAty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);
        Intent intent = new Intent(this,MAty.class);
        startActivity(intent);
    }
}

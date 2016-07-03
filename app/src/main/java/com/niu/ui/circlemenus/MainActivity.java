package com.niu.ui.circlemenus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.niu.ui.circlemenus.ui.ARCMenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        ARCMenuItem item = (ARCMenuItem)findViewById(R.id.menu_item1);
        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
            @Override
            public void onClick(View v, String menuContent) {
                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
            }
        });
        item = (ARCMenuItem)findViewById(R.id.menu_item2);
        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
            @Override
            public void onClick(View v, String menuContent) {
                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
            }
        });
        item = (ARCMenuItem)findViewById(R.id.menu_item3);
        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
            @Override
            public void onClick(View v, String menuContent) {
                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
            }
        });
        item = (ARCMenuItem)findViewById(R.id.menu_item4);
        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
            @Override
            public void onClick(View v, String menuContent) {
                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
            }
        });

//        item = (ARCMenuItem)findViewById(R.id.menu_item5);
//        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
//            @Override
//            public void onClick(View v, String menuContent) {
//                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
//            }
//        });
//        item = (ARCMenuItem)findViewById(R.id.menu_item6);
//        item.setARCMenuItemClickListener(new ARCMenuItem.ARCMenuItemClickListener() {
//            @Override
//            public void onClick(View v, String menuContent) {
//                Toast.makeText(MainActivity.this,"点击"+menuContent,Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}

package com.example.ecamera2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Pager2 extends RelativeLayout implements View.OnClickListener {


    ImageButton btn1, btn2, btn3, btn4, btn5, btn6 ,btnScroll;
    ImageView showImg;
    Boolean openOrnot = false;
    HorizontalScrollView posScroll;
    int recordBtn;

    public Pager2(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_pager2, null);//連接頁面


        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btnScroll = view.findViewById(R.id.btn_selectPose1);
        showImg = view.findViewById(R.id.showImg);

        posScroll = view.findViewById(R.id.posScroll);
        posScroll.setVisibility(INVISIBLE);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btnScroll.setOnClickListener(this);

        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //將元件放入ViewPager
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == recordBtn){
            showImg.setVisibility(INVISIBLE);
            recordBtn = -1;
            return;
        }
        showImg.setVisibility(VISIBLE);
        if(v.getId() == R.id.btn1){
            showImg.setImageResource(R.drawable.test);
        }
        if(v.getId() == R.id.btn2){
            showImg.setImageResource(R.drawable.test); //改圖片
        }
        if(v.getId() == R.id.btn3){
            showImg.setImageResource(R.drawable.test); //改圖片
        }
        if(v.getId() == R.id.btn4){
            showImg.setImageResource(R.drawable.test); //改圖片
        }
        if(v.getId() == R.id.btn5){
            showImg.setImageResource(R.drawable.test); //改圖片
        }
        if(v.getId() == R.id.btn6){
            showImg.setImageResource(R.drawable.test); //改圖片
        }
        if(v.getId() == R.id.btn_selectPose1){
            if(!openOrnot){
                posScroll.setVisibility(VISIBLE);
                openOrnot = true;
            }
            else{
                posScroll.setVisibility(INVISIBLE);
                openOrnot = false;
            }
        }
        recordBtn = v.getId();
    }
}

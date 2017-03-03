package com.hr.nipuream.luckpan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hr.nipuream.luckpan.view.EnvironmentLayout;
import com.hr.nipuream.luckpan.view.LuckPanLayout;
import com.hr.nipuream.luckpan.view.RotatePan;

public class MainActivity extends AppCompatActivity implements RotatePan.AnimationEndListener{

    private EnvironmentLayout layout;
    private RotatePan rotatePan;
    private LuckPanLayout luckPanLayout;
    private ImageView goBtn;

    private String[] strs = {"华为手机","谢谢惠顾","iPhone 6s","mac book","魅族手机","小米手机"};
    private int makePosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckPanLayout = (LuckPanLayout) findViewById(R.id.luckpan_layout);  //转盘最外圈的彩灯环
        luckPanLayout.startLuckLight();
        rotatePan = (RotatePan) findViewById(R.id.rotatePan);
        rotatePan.setAnimationEndListener(this);
        goBtn = (ImageView)findViewById(R.id.go);

        luckPanLayout.post(new Runnable() {
            @Override
            public void run() {
                int height =  getWindow().getDecorView().getHeight();//得到的是手机屏幕的高，且是px值
                int width = getWindow().getDecorView().getWidth();

                int backHeight = 0;

                int MinValue = Math.min(width,height);   //从屏幕宽高中选择最小的那个
                //设置转盘彩灯环距屏幕左右（上下）边距为10dp,得到彩灯环直径MinValue
                MinValue -= Util.dip2px(MainActivity.this,10)*2;
                backHeight = MinValue/2;  //彩灯环的外半径

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) luckPanLayout.getLayoutParams();
                lp.width = MinValue;
                lp.height = MinValue;
                luckPanLayout.setLayoutParams(lp);

                MinValue -= Util.dip2px(MainActivity.this,28)*2;  //得到转盘的直径MinValue
                lp = (RelativeLayout.LayoutParams) rotatePan.getLayoutParams();
                lp.height = MinValue;
                lp.width = MinValue;
                rotatePan.setLayoutParams(lp);

                lp = (RelativeLayout.LayoutParams) goBtn.getLayoutParams();
                lp.topMargin += backHeight;
                lp.topMargin -= (goBtn.getHeight()/2);
                goBtn.setLayoutParams(lp);

                getWindow().getDecorView().requestLayout();
            }
        });
    }

    public void rotation(View view){

        rotatePan.startRotate(makePosition %rotatePan.strs.length);    //取余保证正常显示
        luckPanLayout.setDelayTime(100);
        goBtn.setEnabled(false);
    }

    @Override
    public void endAnimation(int position) {
        goBtn.setEnabled(true);
        luckPanLayout.setDelayTime(500);
        Toast.makeText(this,"Position = "+position+","+rotatePan.strs[position],Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"Position = "+makePosition+","+rotatePan.strs[makePosition],Toast.LENGTH_SHORT).show();
    }


}

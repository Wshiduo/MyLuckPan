package com.hr.nipuream.luckpan.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hr.nipuream.luckpan.R;
import com.hr.nipuream.luckpan.Util;

import java.util.ArrayList;
import java.util.List;

public class RotatePan extends View {

    private Context context;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius = 0;  // 转盘半径

    private int[] images = new int[]{R.mipmap.huawei, R.mipmap.image_one, R.mipmap.iphone, R.mipmap.macbook, R.mipmap.meizu, R.mipmap.xiaomi, R.mipmap.huawei, R.mipmap.image_one, R.mipmap.iphone, R.mipmap.macbook, R.mipmap.meizu, R.mipmap.xiaomi};
    public String[] strs = {"1华为手机", "2谢谢惠顾", "3iPhone6s", "4macbook", "5魅族手机", "6小米手机", "7华为手机2", "8谢谢惠顾2", "9iPhone 6s2", "10mac book2", "11魅族手机2", "12小米手机2"};
    private List<Bitmap> bitmaps = new ArrayList<>();
    private ScrollerCompat scroller;
    private int size = 8;
    private float everyAngle = 360 / size;
    private float halfAngle = everyAngle / 2;
    private float InitAngle = halfAngle;
    private boolean isClicked = false;
    private float du = -halfAngle;
    private float textSize = 16; //文字sp值，需要自己做适配

    public RotatePan(Context context) {
        this(context, null);
    }

    public RotatePan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatePan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        scroller = ScrollerCompat.create(context);

        dPaint.setColor(Color.rgb(255, 133, 132));
        sPaint.setColor(Color.rgb(254, 104, 105));
        textPaint.setColor(Color.WHITE);
        float mtextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics());
        textPaint.setTextSize(mtextSize);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        setClickable(true);

        for (int i = 0; i < size; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), images[i]);
            bitmaps.add(bitmap);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //wrap_content value
        int mHeight = Util.dip2px(context, 300);
        int mWidth = Util.dip2px(context, 300);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int MinValue = Math.min(width, height);
        radius = MinValue / 2;

        RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), width, height);
        float angle = InitAngle; //这里的扇形背景与文字或图片需要相差30度，因为文字和图片要在30度角处（背景中间处）显示
        //扇形背景绘制
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                canvas.drawArc(rectF, angle, everyAngle, true, dPaint);
            } else {
                canvas.drawArc(rectF, angle, everyAngle, true, sPaint);
            }
            angle += everyAngle;
        }

        //图片和文字绘制
        for (int i = 0; i < size; i++) {
            //在此处加3*halfAngle只是为了显示正确，这和直接给InitAngle赋值决定pos是不一样的
//            drawIcon(width / 2, height / 2, radius, InitAngle  + 3*halfAngle, i, canvas);
            drawText(InitAngle + everyAngle, strs[i], radius, textPaint, canvas, rectF);
            InitAngle += everyAngle;  //方式1:以改变绘制的角度来实现旋转绘制，可用方式1替换
        }
    }

    private void drawText(float startAngle, String string, int r, Paint mTextPaint, Canvas mCanvas, RectF mRange) {

/************************************************* 1 文字圆弧状显示 ************************************************************/
/*
//设计思路：沿着弧线方向对字符串进行绘制，注意textPaint.setTextAlign(Paint.Align.CENTER);设定的绘制的起始位置值,这里字间距没办法修改
        Path path = new Path();
        path.addArc(mRange, startAngle, everyAngle);  //startAngle就决定了初始位置
//        mTextPaint.setTextAlign(Paint.Align.LEFT);
//        float textWidth = mTextPaint.measureText(string);
//        //文字水平偏移量,偏移到扇形中间位置（作用与textPaint.setTextAlign(Paint.Align.CENTER)等同）
//        float hOffset = (float) (r * Math.PI / size - textWidth / 2);
        float vOffset = r / 4;
        mTextPaint.setTypeface(Typeface.create(Typeface.MONOSPACE,Typeface.NORMAL)); //字体设置
        mCanvas.drawTextOnPath(string, path, 0, vOffset, mTextPaint);
*/

/************************************************* 2 文字横扫同向模式 ************************************************************/
//设计思路：采用直线型路径path由中心向外绘制每个字符串，字符串首字符与圆心距离用转盘内部小同心圆的半径作为限定，path的两个点坐标可由两圆半径算得
        //使文字竖着摆放显示，（但文字方向有点问题）
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(string,0,1,bounds);
        int wordHeight = bounds.height();
        Path path1 = new Path();
        //分别计算x,y初始的坐标
        double radians = Math.toRadians(startAngle + halfAngle);  //startAngle的正负决定旋转方向，负为顺时针旋转 halfAngle为调整位置显示值
        float bigX = (float) (Math.cos(radians) * r + r);//大圆坐标，此固定值在开发时不需要放到循环中
        float bigY =  r + (float) Math.sin(radians) * r;
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        float smallR = r/3; //内圆半径，也是字符串与圆心的距离
        //内部小圆x,y坐标
        float smallX = (float)(cos * smallR + r);  //小圆坐标
        float smallY = (float) (sin * smallR + r);//采用从圆心向外发射画法
//        path1.moveTo(bigX,bigY);    //采用直线路径法绘制文字，这里的hOffset1代表也可称为文字竖直偏移量
//        path1.lineTo(smallX,smallY);
         path1.moveTo(smallX,smallY);   //设置文字显示方向倒转
        path1.lineTo(bigX,bigY);
        mCanvas.drawTextOnPath(string, path1, 0, wordHeight/2, mTextPaint);

/************************************************* 3 文字横扫多变模式或多状态显示 ************************************************************/
/*//设计思路：通过给转盘添加同心小圆的方式，确定一条字符串的首个字符位置（以具体坐标定位），其他字符则根据手字符进行适当偏移生成
        //该方式文字竖直显示1，但英文显示会有问题
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(string.substring(0, 1), 0, 1, bounds);
        int wordHeight = bounds.height() + 20; //增大字间距
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        if (isClicked) {
            du = halfAngle;    //du决定整体文字朝向，就是以该角度处的文字样式为模板，然后偏移到别的地方，就像是在固定一个地方写字，人不动而纸动
        }
        double radians = Math.toRadians(startAngle + du);  //startAngle的正负决定旋转方向，负为顺时针旋转
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        float smallR = r/3;
        //内部小圆x,y坐标
        float x = (float)(cos * smallR + r);
        float y = (float) (sin * smallR + r);//采用从圆心向外发射画法
        for (int j = string.length()-1;j >=0 ;j--) {  //从后往前取文字，文字汇聚式阅读
            mCanvas.drawText(string.substring(j, j + 1), (float) (x + wordHeight * cos * (string.length() - j)), (float) (y + wordHeight * sin * (string.length() - j)), textPaint);
        }
//            mCanvas.rotate(everyAngle ,r,r);  //方式2:旋转的是带有文字的“纸——mCanvas”，此时笔(View的坐标)是不动的，可替换换方式1处代码*/

/************************************************* 4 文字发射状显示 ************************************************************/
//设计思路：采用每个字符串使用一个圆弧，并且每个圆弧都只放一个文字，该字符串的其他文字则根据该文字进行相对位置平移生成
      /*  Path path = new Path();
        path.addArc(mRange, startAngle, everyAngle);  //startAngle决定了绘制的初始位置,该位置的值可调整以适应扇形背景区域
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(string.substring(0, 1), 0, 1, bounds);  //此固定值在开发时不需要放到循环中
        int wordHeight = bounds.height() + 14;  //14是增加的字间距,因为中英文的文字高度不同，会导致显示效果不均一，这里可以用定值替代
        int i = 6;   //因绘制文字采用的是由中心向四周发射方式，此处指定vOffset相对path垂直向内偏移量（包括字间距）
        for (int j = string.length() - 1; j >= 0; j--) {  //为了使每串字符串都与圆心保持相同距离，此处采用倒序绘制文字
            //若hOffset和vOffset都为0时，该方法会让文字沿着Path路径绘制文本，hOffset参数指定相对path水平偏移,hOffset值为正数则向path正方向偏移，vOffset指定相对path垂直偏移，若vOffset值为正则向下向内偏移；
            mCanvas.drawTextOnPath(string.substring(j, j + 1), path,0, wordHeight * i--, textPaint);
        }
//        mCanvas.rotate(everyAngle ,r,r); //用于替代InitAngle += everyAngle;的第二种实现方式*/
    }

    private void drawIcon(int xx, int yy, int mRadius, float startAngle, int i, Canvas mCanvas) {

        int imgWidth = mRadius / 4;
        float angle = (float) Math.toRadians(startAngle);
        float x = (float) (xx + mRadius / 2 * Math.cos(angle));
        float y = (float) (yy + mRadius / 2 * Math.sin(angle));

        // 确定绘制图片的位置
        RectF rect = new RectF(x - imgWidth * 3 / 4, y - imgWidth * 3 / 4, x + imgWidth
                * 3 / 4, y + imgWidth * 3 / 4);

        Bitmap bitmap = bitmaps.get(i);

        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    public void setImages(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        this.invalidate();
    }

    public void setStr(String... strs) {
        this.strs = strs;
        this.invalidate();
    }

    //旋转一圈所需要的时间
    private static final long ONE_WHEEL_TIME = 50;

    public void startRotate(int poss) {
        isClicked = true;
        setKeepScreenOn(true);
        int lap = (int) (Math.random() * 12) + 4;

        float angle = 0;
        if (poss < 0) {
            angle = (int) (Math.random() * 360);
        } else {
            int initPos = queryPosition();
            if (poss > initPos) {
                angle = (poss - initPos) * everyAngle;
                lap -= 1;
                angle = 360 - angle;
            } else if (poss < initPos) {
                angle = (initPos - poss) * everyAngle;
            } else {
                //nothing to do.
            }
        }

        float increaseDegree = lap * 360 + angle;       //本次抽奖需要旋转的角度差
        float time = (lap + angle / 360) * ONE_WHEEL_TIME;
        float DesRotate = increaseDegree + InitAngle;  //本次抽奖需要旋转的总角度

        //为了每次都能旋转到转盘的中间位置,初始位置若是就设置在中间，则此处可省略
//        float offRotate = DesRotate  % everyAngle;
//        DesRotate -= offRotate;
//        DesRotate += everyAngle/2;  //8个区域时默认就是指针居中的
//        DesRotate += Math.random() * (everyAngle-5) +3;  //指定区域之间，并防止转到两个扇面正中间位置

        ValueAnimator animtor = ValueAnimator.ofInt((int) InitAngle, (int) DesRotate);
        animtor.setInterpolator(new AccelerateDecelerateInterpolator());
        animtor.setDuration((long) time);
        animtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int updateValue = (int) animation.getAnimatedValue();
                InitAngle = updateValue % 360;
                ViewCompat.postInvalidateOnAnimation(RotatePan.this);
            }
        });

        animtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (l != null)
                    l.endAnimation(queryPosition());
            }
        });
        animtor.start();
    }


    private int queryPosition() {
        InitAngle = InitAngle % 360;
        int pos = (int) (InitAngle / everyAngle);
        return calcumAngle(pos);
    }

    private int calcumAngle(int pos) {  //位置校正，获取当前指定的奖项区域position
        int size2 = size / 2;
        if (pos >= 0 && pos <= size2) {
            pos = size2 - pos;
        } else {
            pos = (size - pos) + size2;
        }
        return pos;
    }

    public interface AnimationEndListener {
        void endAnimation(int position);
    }

    private AnimationEndListener l;

    public void setAnimationEndListener(AnimationEndListener l) {
        this.l = l;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    public void setRotate(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        InitAngle = rotation;
        ViewCompat.postInvalidateOnAnimation(this);
    }


    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {
            setRotate(scroller.getCurrY());
        }

        super.computeScroll();
    }
}
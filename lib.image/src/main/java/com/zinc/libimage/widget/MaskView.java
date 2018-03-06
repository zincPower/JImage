package com.zinc.libimage.widget;
 
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zyr on 15/9/8.
 */
public class MaskView extends View {
    private Paint paint;

    RectF rectF;
    RectF tempR;

    public MaskView(Context context) {
        super(context);
        init();
    }
 
    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public MaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
 
    public void init(){
        paint = new Paint();
        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(3);
        rectF = new RectF(0,0,400,400);
        tempR = new RectF();
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //DIFFERENCE是第一次不同于第二次的部分显示出来A-B-------
        //REPLACE是显示第二次的B******
        //REVERSE_DIFFERENCE 是第二次不同于第一次的部分显示--------
        //INTERSECT交集显示A-(A-B)*******
        //UNION全部显示A+B******
        //XOR补集 就是全集的减去交集生育部分显示--------


        canvas.translate(100,100);


        tempR.set(rectF);
        tempR.inset(20,-20);

        canvas.clipRect(tempR, Region.Op.DIFFERENCE);
        paint.setColor(Color.GRAY);
        canvas.drawRect(tempR, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectF, paint);

        tempR.inset(-50,-50);
        paint.setColor(Color.BLUE);
        canvas.drawRect(tempR, paint);

//        canvas.save();
//        canvas.translate(10, 10);
//        canvas.clipRect(0, 0, 300, 300, Region.Op.XOR);
//        canvas.drawColor(Color.YELLOW);
//        canvas.clipRect(200, 200, 400, 400);
//        canvas.drawColor(Color.GRAY);
//        canvas.clipRect(0,0,400,400);
//        canvas.drawColor(Color.BLUE);
//        canvas.restore();
 
//        paint.setColor(Color.BLUE);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.translate(10, 10);
//        canvas.drawRect(0, 0, 300, 300, paint);
//        paint.setColor(Color.RED);
//        canvas.drawRect(200, 200, 400, 400,paint);
        invalidate();
    }
}
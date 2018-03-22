package com.zinc.libimage.view.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zinc.libimage.R;
import com.zinc.libimage.utils.BitmapLoadCallback;
import com.zinc.libimage.utils.BitmapLoadTask;
import com.zinc.libimage.utils.BitmapLoadUtils;
import com.zinc.libimage.utils.ExifInfo;
import com.zinc.libimage.utils.FileUtils;
import com.zinc.libimage.widget.FastBitmapDrawable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/22
 * @description 画板
 */

public class PaletteImageView extends AppCompatImageView {

    private static final String TAG = PaletteImageView.class.getSimpleName();
    private static final int MAX_CACHE_STEP = 20;
    //区域解析
    private BitmapRegionDecoder mDecoder;

    //控件的宽高
    private int mViewHeight = -1;
    private int mViewWidth = -1;

    //图片的宽高
    private int mImageHeight = -1;
    private int mImageWidth = -1;

    private Bitmap mBitmap;

    private BitmapFactory.Options mOptions;

    //缩放因子
    private float mScale;

    //解析区域
    private Rect mRect;

    private Matrix mTempMatrix;
    private Matrix matrix;
    private boolean mBitmapDecoded = false;

    private Paint mPaint;
    private Path mPath;

    private float lastX;
    private float lastY;

    private List<PathInfo> mPathHistory;

    private Canvas mBufferCanvas;
    private Bitmap mBufferBitmap;
    private int mRequiredWidth;
    private int mRequiredHeight;

    private Uri mInputUri;

    public PaletteImageView(Context context) {
        this(context, null, 0);
    }

    public PaletteImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPalette();
    }

    protected void initPalette() {

        this.mOptions = new BitmapFactory.Options();
        this.matrix = new Matrix();

        this.mTempMatrix = new Matrix();

        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(ContextCompat.getColor(getContext(), R.color.jimage_white));
        this.mPaint.setStrokeWidth(20);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setFilterBitmap(true);

        this.mPath = new Path();

        this.mRequiredWidth = this.mRequiredHeight = BitmapLoadUtils.calculateMaxBitmapSize(getContext());

    }

    public void setImage(@NonNull Uri inputUri, @NonNull Rect rect) {

        if (inputUri == null) {
            return;
        }

        this.mInputUri = inputUri;

        try {
            processInputUri();
        } catch (NullPointerException | IOException e) {
            return;
        }

        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(this.mInputUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final FileDescriptor fileDescriptor;
        if (parcelFileDescriptor != null) {
            fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        } else {
            return;
        }

        this.mRect = rect;
        try {
            //设置为数据不共享
            this.mDecoder = BitmapRegionDecoder.newInstance(fileDescriptor, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, mOptions);

        this.mOptions.inJustDecodeBounds = false;
        this.mOptions.inSampleSize = BitmapLoadUtils.calculateInSampleSize(this.mOptions, this.mRequiredWidth, this.mRequiredHeight);

        RectF tempRectF = new RectF(this.mRect);
        this.mTempMatrix.setScale(this.mOptions.inSampleSize, this.mOptions.inSampleSize);
        mTempMatrix.mapRect(tempRectF);

        this.mRect = new Rect((int) tempRectF.left, (int) tempRectF.top, (int) tempRectF.right, (int) tempRectF.bottom);

        //解析指定区域
        this.mBitmap = this.mDecoder.decodeRegion(this.mRect, this.mOptions);

        initBuffer();
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        this.mOptions.inJustDecodeBounds = true;
//        Bitmap tempBitmap = BitmapFactory.decodeStream(is, null, this.mOptions);
//        this.mImageWidth = this.mOptions.outWidth;
//        this.mImageHeight = this.mOptions.outHeight;
//
//        this.mOptions.inJustDecodeBounds = false;
//        this.mOptions.inMutable = true;

    }

    private void processInputUri() throws NullPointerException, IOException {
        String inputUriScheme = this.mInputUri.getScheme();
        Log.d(TAG, "Uri scheme: " + inputUriScheme);
        if ("http".equals(inputUriScheme) || "https".equals(inputUriScheme)) {
//            try {
//                downloadFile(mInputUri, mOutputUri);
//            } catch (NullPointerException | IOException e) {
//                Log.e(TAG, "Downloading failed", e);
//                throw e;
//            }
        } else if ("content".equals(inputUriScheme)) {       //content
            String path = getFilePath();
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                mInputUri = Uri.fromFile(new File(path));   //转换为file:///xxxxx类型

            } else if (!"file".equals(inputUriScheme)) {        //不是以file开头的抛异常
                Log.e(TAG, "Invalid Uri scheme " + inputUriScheme);
                throw new IllegalArgumentException("Invalid Uri scheme" + inputUriScheme);
            }
        }
    }

    private String getFilePath() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return FileUtils.getPath(getContext(), this.mInputUri);
        } else {
            return null;
        }
    }

    private void initBuffer() {
        this.mBufferBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        this.mBufferCanvas = new Canvas(this.mBufferBitmap);
        this.mBufferCanvas.drawBitmap(this.mBitmap, 0, 0, null);
    }

//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        setImageDrawable(new FastBitmapDrawable(bm));
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        if (this.mBitmapDecoded && (this.mViewWidth == -1 || this.mViewHeight == -1)) {
//            this.mViewHeight = getMeasuredHeight();
//            this.mViewWidth = getMeasuredWidth();
//
//            float heightRatio = this.mViewHeight / (float) this.mDecoder.getHeight();
//            float widthRatio = this.mViewWidth / (float) this.mDecoder.getWidth();
//            this.mScale = Math.min(widthRatio, heightRatio);
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mBufferBitmap != null) {
            canvas.drawBitmap(this.mBufferBitmap, 0, 0, null);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                this.mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                this.mPath.lineTo(x, y);
                mBufferCanvas.drawPath(this.mPath, this.mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                this.saveDrawingPath();
                this.mPath.reset();
                break;
        }

        return super.onTouchEvent(event);
    }

    private void saveDrawingPath() {
        if (this.mPathHistory == null) {
            mPathHistory = new ArrayList<>(MAX_CACHE_STEP);
        } else if (this.mPathHistory.size() == MAX_CACHE_STEP) {
            this.mPathHistory.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathInfo info = new PathInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        this.mPathHistory.add(info);
    }

    static class PathInfo {
        private Path path;
        private Paint paint;

        public PathInfo() {
        }

        public PathInfo(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }
    }
}

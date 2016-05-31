package watch.com.wuziqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import watch.com.wuziqi.R;

public class GomokuPanel extends View {
    private static final int MAX_LINE = 10;
    private float mLineHeight;

    /**
     * 棋盘界限颜色
     */
    private int mPanelBoardColor = Color.parseColor("#565758");

    /**
     * 棋盘界限画笔
     */
    private Paint mPanelBoardPaint;

    /**
     * 白方棋子
     */
    private Bitmap mWhiteChess;

    /**
     * 黑方棋子
     */
    private Bitmap mBlackChess;

    /**
     * 棋子高度比例
     */
    private float mRatioPieceLengthOfLineHeight = 3 * 1.0f / 4;

    /**
     * 白子集合
     */
    private List<Point> mWhiteChessList = new ArrayList<>();

    /**
     * 黑子集合
     */
    private List<Point> mBlackChessList = new ArrayList<>();

    /**
     * 谁先手.(五子棋规则:默认都是黑子先手)
     */
    private boolean mIsBlackFirst;

    /**
     * 游戏是否结束
     */
    private boolean mIsGameOver;

    public GomokuPanel(Context context) {
        this(context, null);
    }

    public GomokuPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GomokuPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().
                obtainStyledAttributes(attrs, R.styleable.GomokuPanel, 0, 0);

        mBlackChess = BitmapFactory.decodeResource(getResources(), ta.getResourceId(
                R.styleable.GomokuPanel_gomoku_black_pic,
                R.drawable.stone_b1));

        mWhiteChess = BitmapFactory.decodeResource(getResources(), ta.getResourceId(
                R.styleable.GomokuPanel_gomoku_white_pic,
                R.drawable.stone_w2));

        mPanelBoardColor = ta.getColor(
                R.styleable.GomokuPanel_gomoku_board_color, mPanelBoardColor);

        ta.recycle();

        init();
    }

    private void init() {
        initData();
        initPaint();
    }

    private void initData() {
        mIsGameOver = false;
        mIsBlackFirst = true;
    }

    private void initPaint() {
        mPanelBoardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPanelBoardPaint.setColor(mPanelBoardColor);
        mPanelBoardPaint.setDither(true);
        mPanelBoardPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int length = Math.min(widthSize, heightSize);

        // 考虑嵌套在ScrollView中的特殊Case
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            length = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            length = widthSize;
        }

        setMeasuredDimension(length, length);
    }

    /**
     * 宽高确认之后的回调函数
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 要考虑左右边界和上下边界各需要预留半个棋子的大小
        mLineHeight = h * 1.0f / (MAX_LINE + 1);

        int pieceLength = (int) (mLineHeight * mRatioPieceLengthOfLineHeight);

        mWhiteChess = Bitmap.createScaledBitmap(mWhiteChess, pieceLength, pieceLength, false);
        mBlackChess = Bitmap.createScaledBitmap(mBlackChess, pieceLength, pieceLength, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制棋盘
        drawBoard(canvas);

        // 绘制棋子
        drawPieces(canvas);
    }

    private void drawBoard(Canvas canvas) {
        float startX = mLineHeight / 2;
        float stopX = startX + MAX_LINE * mLineHeight;
        float startY = mLineHeight / 2;
        float stopY = startY;
        // 绘制横线
        for (int i = 0; i <= MAX_LINE; i++) {
            canvas.drawLine(startX, startY, stopX, stopY, mPanelBoardPaint);
            startY += mLineHeight;
            stopY = startY;
        }

        // 绘制纵线
        startY = mLineHeight / 2;
        stopY = startY + MAX_LINE * mLineHeight;
        startX = mLineHeight / 2;
        stopX = startX;
        for (int i = 0; i <= MAX_LINE; i++) {
            canvas.drawLine(startX, startY, stopX, stopY, mPanelBoardPaint);
            startX += mLineHeight;
            stopX = startX;
        }
    }

    private void drawPieces(Canvas canvas) {
        // 绘制白子
        for (Point point : mWhiteChessList) {
            // 计算公式为: (x + 0.5) * h - ((ratio * h) / 2) = (x + (1 - ratio) / 2) * h
            float left = (point.x + (1 - mRatioPieceLengthOfLineHeight) / 2) * mLineHeight;
            float top = (point.y + (1 - mRatioPieceLengthOfLineHeight) / 2) * mLineHeight;
            canvas.drawBitmap(mWhiteChess, left, top, null);
        }

        // 绘制黑子
        for (Point point : mBlackChessList) {
            // 计算公式为: (x + 0.5) * h - ((ratio * h) / 2) = (x + (1 - ratio) / 2) * h
            float left = (point.x + (1 - mRatioPieceLengthOfLineHeight) / 2) * mLineHeight;
            float top = (point.y + (1 - mRatioPieceLengthOfLineHeight) / 2) * mLineHeight;
            canvas.drawBitmap(mBlackChess, left, top, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);

            if (mWhiteChessList.contains(p) || mBlackChessList.contains(p)) {
                return false;
            }

            if (mIsBlackFirst) {
                mBlackChessList.add(p);
            } else {
                mWhiteChessList.add(p);
            }

            invalidate();

            mIsBlackFirst = !mIsBlackFirst;

            checkGameOver();
        }

        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private void checkGameOver() {
        boolean isWhiteWin = checkWin(mWhiteChessList);
        boolean isBlackWin = checkWin(mBlackChessList);

        if (isWhiteWin || isBlackWin) {
            mIsGameOver = true;
            String winText = isWhiteWin ? getResources().getString(R.string.white_win)
                    : getResources().getString(R.string.black_win);
            Toast.makeText(getContext(), winText, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkWin(List<Point> pointList) {
        for (Point point : pointList) {
            if (checkHorizontalWin(pointList, point)) return true;
            if (checkVertical(pointList, point)) return true;
            if (checkSlantLeft(pointList, point)) return true;
            if (checkSlantRight(pointList, point)) return true;
        }

        return false;
    }

    /**
     * 检测右斜线是否获胜.
     */
    private boolean checkSlantRight(List<Point> pointList, Point point) {
        int x = point.x;
        int y = point.y;
        for (int i = 1; i <= 4; i ++) {
            Point tp = new Point(x - i, y - i);
            if (!pointList.contains(tp)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检测左斜线是否获胜.
     */
    private boolean checkSlantLeft(List<Point> pointList, Point point) {
        int x = point.x;
        int y = point.y;
        for (int i = 1; i <= 4; i ++) {
            Point tp = new Point(x + i, y + i);
            if (!pointList.contains(tp)) {
                return false;
            }
        }

        return true;
    }


    /**
     * 检测纵向是否获胜
     */
    private boolean checkVertical(List<Point> pointList, Point point) {
        int x = point.x;
        int y = point.y;
        for (int i = 1; i <= 4; i ++) {
            Point tp = new Point(x, y + i);
            if (!pointList.contains(tp)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检测横向是否获胜
     */
    private boolean checkHorizontalWin(List<Point> pointList, Point point) {
        int x = point.x;
        int y = point.y;
        for (int i = 1; i <= 4; i ++) {
            Point tp = new Point(x + i, y);
            if (!pointList.contains(tp)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 重新开始
     */
    public void restart() {
        mWhiteChessList.clear();
        mBlackChessList.clear();
        mIsGameOver = false;
        mIsBlackFirst = true;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        FIRSaveState ss = new FIRSaveState(superState);
        ss.isGameOver = mIsGameOver;
        ss.isWhiteFirst = mIsBlackFirst;
        ss.whitePoints = new ArrayList<>(mWhiteChessList);
        ss.blackPoints = new ArrayList<>(mBlackChessList);

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof FIRSaveState) {
            FIRSaveState ss = (FIRSaveState) state;
            mIsGameOver = ss.isGameOver;
            mIsBlackFirst = ss.isWhiteFirst;
            mWhiteChessList = new ArrayList<>(ss.whitePoints);
            mBlackChessList = new ArrayList<>(ss.blackPoints);
            super.onRestoreInstanceState(ss.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * 用于保存五子棋状态,IMOOC上是用Bundle来存储的,我是采用自定义的方式.
     */
    private static class FIRSaveState extends BaseSavedState {
        boolean isGameOver;
        boolean isWhiteFirst;
        List<Point> whitePoints;
        List<Point> blackPoints;

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isGameOver? 1 : 0);
            out.writeInt(isWhiteFirst? 1 : 0);
            out.writeTypedList(whitePoints);
            out.writeTypedList(blackPoints);
        }

        public static final Parcelable.Creator<FIRSaveState> CREATOR =
                new Parcelable.Creator<FIRSaveState>() {

                    @Override
                    public FIRSaveState createFromParcel(Parcel source) {
                        return new FIRSaveState(source);
                    }

                    @Override
                    public FIRSaveState[] newArray(int size) {
                        return new FIRSaveState[size];
                    }
                };

        public FIRSaveState(Parcel source) {
            super(source);
            isGameOver = source.readInt() == 1;
            isWhiteFirst = source.readInt() == 1;
            source.readTypedList(whitePoints, Point.CREATOR);
            source.readTypedList(blackPoints, Point.CREATOR);
        }

        public FIRSaveState(Parcelable superState) {
            super(superState);
        }
    }
}

package com.example.distributionview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie on 2023/6/17
 * 自定义属性分布图，目前只实现三角形的
 */
public class DistributionView extends View {
    private Paint paint = new Paint();
    //多边形层数
    private int floorCount = 3;
    //多边形边数（属性个数）
    private int angleCount = 3;
    //标题文本长度
    private int textLength = 2;
    //属性标题列表
    private List<String> titles = new ArrayList<>();
    //各属性分布层数，不大于总层数
    private List<Float> scaleList = new ArrayList<>();
    //覆盖区域颜色
    private int areaColor = Color.parseColor("#B300D19F");
    //覆盖区域颜色
    private int areaLineColor = Color.parseColor("#FF00D19F");
    //覆盖区域连接线颜色
    private int areaConnectLineColor = Color.parseColor("#6FE4C8");
    //连接线颜色
    private int connectLineColor = Color.parseColor("#80FCFCFC");
    //背景颜色
    private int bgColor = Color.parseColor("#FAFAFA");
    //背景颜色2
    private int bgColor2 = Color.parseColor("#F8F8F8");
    //边框与连线颜色
    private int lineColor = Color.parseColor("#F3F3F3");
    //属性文本颜色
    private int textColor = Color.parseColor("#000000");
    //默认文本与边框距离
    private float defaultTextPadding = 20f;
    //属性文本大小（px）
    private float textSize = 20f;
    //是否显示各层连线 默认显示
    private boolean isShowConnect = true;
    //是否显示边框 默认显示
    private boolean isShowLine = true;
    //画笔粗细
    private float paintSize = 2f;
    private int mWidth = 0;
    private int mHeight = 0;

    public DistributionView(Context context) {
        this(context, null);
    }

    public DistributionView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DistributionView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DistributionView);
        if (typedArray != null) {
            //线条颜色
            lineColor = typedArray.getColor(R.styleable.DistributionView_line_color, lineColor);
            //线条宽度
            paintSize = typedArray.getDimension(R.styleable.DistributionView_line_size, paintSize);
            //绘制层数（不得小于各元素分配的比例）
            floorCount = typedArray.getInt(R.styleable.DistributionView_floor_count, floorCount);
            //绘制元素个数
            angleCount = typedArray.getInt(R.styleable.DistributionView_angle_count, angleCount);
            //文本颜色
            textColor = typedArray.getColor(R.styleable.DistributionView_text_color, textColor);
            //文本大小
            textSize = typedArray.getDimension(R.styleable.DistributionView_text_size, textSize);
            //覆盖区域颜色
            areaColor = typedArray.getColor(R.styleable.DistributionView_area_color, areaColor);
            //覆盖区域边框颜色
            areaLineColor = typedArray.getColor(R.styleable.DistributionView_area_line_color, areaLineColor);
            //覆盖区域连接线颜色
            areaConnectLineColor = typedArray.getColor(R.styleable.DistributionView_area_connect_line_color, areaConnectLineColor);
            //链接线颜色
            connectLineColor = typedArray.getColor(R.styleable.DistributionView_connect_line_color, connectLineColor);
            //背景颜色
            bgColor = typedArray.getColor(R.styleable.DistributionView_bg_color, bgColor);
            //背景颜色2
            bgColor2 = typedArray.getColor(R.styleable.DistributionView_bg_color2, bgColor2);
            //默认文本与图形间距
            defaultTextPadding = typedArray.getDimension(R.styleable.DistributionView_default_text_padding, defaultTextPadding);
        }
        typedArray.recycle();
        paint.setAntiAlias(true);

        //示例数据设置
        List<String> titleList = new ArrayList<>();
        titleList.add("数据1");
        titleList.add("数据2");
        titleList.add("数据3");
        setTitle(titleList);

        List<Float> scaleList1 = new ArrayList<>();
        scaleList1.add(0.5f);
        scaleList1.add(0.1f);
        scaleList1.add(1f);
        setScaleList(scaleList1);
        setIsShowConnect(true);
        setIsShowLine(true);
    }

    /**
     * 设置元素标题（不得少于元素个数）
     */
    public DistributionView setTitle(List<String> titles) {
        this.titles = titles;
        return this;
    }


    /**
     * 设置各元素对应比例（不得少于元素个数，不得低于层数）
     */
    public DistributionView setScaleList(List<Float> list) {
        if (list == null || list.size() < angleCount) {
            return this;
        }
        for (int i = 0; i < list.size(); i++) {
            float f = list.get(i);
            if (f > 1) {
                list.set(i, (float) floorCount);
                continue;
            }
            list.set(i, f * floorCount);
        }
        this.scaleList = list;
        return this;
    }

    /**
     * 设置层数
     */
    public DistributionView setFloorCount(int floorCount) {
        this.floorCount = floorCount;
        return this;
    }


    /**
     * 设置属性个数
     */
    public DistributionView setAngleCount(int angleCount) {
        this.angleCount = angleCount;
        return this;
    }


    /**
     * 设置是否显示连接线
     */
    public DistributionView setIsShowConnect(boolean isShow) {
        this.isShowConnect = isShow;
        return this;
    }


    /**
     * 设置是否显示边框
     */
    public DistributionView setIsShowLine(boolean isShow) {
        this.isShowLine = isShow;
        return this;
    }


    private void init() {
        paint.setColor(lineColor);
        paint.setStrokeWidth(paintSize);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 250;
        int height = 250;
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        mWidth = Math.min(mWidth, mHeight);
        mHeight = mWidth;
        for (int i = 0; i < titles.size(); i++) {
            textLength = Math.max(titles.get(i).length(), textLength);
        }
        if (angleCount == 3) {
            drawThreeAngle(canvas);
        } else if (angleCount == 4) {
            drawFourAngle(canvas);
        } else if (angleCount == 5) {
            drawFiveAngle(canvas);
        } else if (angleCount == 6) {
            drawSixAngle(canvas);
        } else if (angleCount == 7) {
            drawSevenAngle(canvas);
        } else if (angleCount == 8) {
            drawEightAngle(canvas);
        }
    }

    /**
     * 三角分布器  等边三角形
     */
    private void drawThreeAngle(Canvas canvas) {
        //三角形宽度，边长
        float ww = (float) (mWidth - defaultTextPadding * 2 - textLength * textSize);
        //中心点各对角线的夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        //三角形高度，顶点到底边的垂直线（两个都可以)
//        float hh = (float) (ww * Math.sin(angle / 2));
        float hh = (float) (ww / 2 / Math.tan(angle / 2));
        Path pathLine = new Path();
        //中心点到各角的距离
        float r = (float) (ww / 2 / Math.sin(angle / 2));

        /**
         *                      图形中心点
         *   各正多边形的中心点横坐标X都可以是整个控件的横向中心位置
         *   但是由于三角形，正五边形，正七边形比较特殊
         *   所以它们的中心点纵坐标Y并不在整个控件的中心位置
         *   这个时候就需要根据实际情况做一下小运算
         *   比如说三角形，首先算得三角形的宽度作为底边长
         *   因为中心点到各角的连线夹角都是相等的
         *   所以根据角度和边长就可算得中心点到各夹角的连线长度
         *   然后根据这个长度就可已算出纵坐标Y的位置
         *   之后的图形绘制都需要根据中心点和中心点到各夹角的长度来计算
         */
        float centerX = getWidth() / 2;
        //图形中心点y
        float centerY = (float) (r + getPaddingTop() + textSize + defaultTextPadding + getHeight() * 0.065);

        //绘制背景颜色，可以设置间隔的颜色
        for (int i = floorCount; i >= 1; i--) {
            pathLine.reset();
            float ur = r * i / floorCount;
            pathLine.moveTo(centerX + 0f, centerY - ur);
            pathLine.lineTo((float) (centerX + ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
            pathLine.lineTo((float) (centerX - ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
            pathLine.close();

            paint.setStyle(Paint.Style.FILL);
            if (i % 2 == 0) {
                paint.setColor(bgColor);
            } else {
                paint.setColor(bgColor2);
            }
            canvas.drawPath(pathLine, paint);
            canvas.save();
        }

        //绘制边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                pathLine.moveTo(centerX + 0f, centerY - ur);
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.close();
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawPath(pathLine, paint);
            canvas.save();
        }

        //绘制连接线
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo(centerX, centerY - r);
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.close();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(connectLineColor);
            canvas.drawPath(pathLine, paint);
            canvas.save();
        }

        //绘制文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), centerX - textSize, centerY - r - defaultTextPadding, paint);
        canvas.drawText(titles.get(1), centerX + ww / 2 - textSize, (float) (centerY + r * Math.cos(angle / 2) + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(2), centerX - ww / 2 - textSize, (float) (centerY + r * Math.cos(angle / 2) + textSize + defaultTextPadding), paint);
        canvas.save();

        //绘制覆盖分布区域图
        pathLine.reset();
        pathLine.moveTo(centerX, centerY - r * scaleList.get(0) / floorCount);
        pathLine.lineTo((float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(1) / floorCount * Math.cos(angle / 2)));
        pathLine.lineTo((float) (centerX - r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.cos(angle / 2)));
        pathLine.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(areaColor);
        canvas.drawPath(pathLine, paint);
        canvas.save();

        //绘制覆盖区域的连接线
        if (isShowConnect) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(areaConnectLineColor);
            canvas.drawLine(centerX, centerY, centerX, centerY - r * scaleList.get(0) / floorCount, paint);
            canvas.drawLine(centerX, centerY, (float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(1) / floorCount * Math.cos(angle / 2)), paint);
            canvas.drawLine(centerX, centerY, (float) (centerX - r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.cos(angle / 2)), paint);
            canvas.save();
        }

        //绘制覆盖区域的边框线
        pathLine.reset();
        pathLine.moveTo(centerX, centerY - r * scaleList.get(0) / floorCount);
        pathLine.lineTo((float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(1) / floorCount * Math.cos(angle / 2)));
        pathLine.lineTo((float) (centerX - r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.cos(angle / 2)));
        pathLine.close();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(areaLineColor);
        canvas.drawPath(pathLine, paint);
        canvas.save();

        //绘制覆盖区域的圆点
        paint.setColor(areaLineColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY - r * scaleList.get(0) / floorCount, 5, paint);
        canvas.drawCircle((float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(1) / floorCount * Math.cos(angle / 2)), 5, paint);
        canvas.drawCircle((float) (centerX - r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.cos(angle / 2)), 5, paint);
        canvas.save();
    }

    /**
     * 四角分布器   正方形
     */
    private void drawFourAngle(Canvas canvas) {
        //四边形宽度，高度，边长
        float ww = mWidth - 2 * defaultTextPadding - textLength * textSize;
        float hh = ww;
        //中心点到各角的连线夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        Path pathLine = new Path();
        //中心点X
        float centerX = getWidth() / 2;
        //中心点Y
        float centerY = getHeight() / 2;
        //中心点到各角的距离
        float r = (float) (ww / (2 * Math.cos(angle / 2)));
        //绘制边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                pathLine.moveTo((float) (centerX - ur * Math.cos(angle / 2)), (float) (centerY - ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle / 2)), (float) (centerY - ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.cos(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.close();

                paint.setStyle(Paint.Style.FILL);
                if (i % 2 == 0) {
                    paint.setColor(bgColor);
                } else {
                    paint.setColor(bgColor2);
                }
                canvas.drawPath(pathLine, paint);
                canvas.save();
            }
        }
        //绘制线条
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2)), (float) (centerY - r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2)), (float) (centerY - r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            canvas.drawPath(pathLine, paint);
        }
        //绘制文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), (float) (centerX - r * Math.sin(angle / 2) - textSize), (float) (centerY - r * Math.cos(angle / 2) - defaultTextPadding), paint);
        canvas.drawText(titles.get(1), (float) (centerX + r * Math.sin(angle / 2) - textSize), (float) (centerY - r * Math.cos(angle / 2) - defaultTextPadding), paint);
        canvas.drawText(titles.get(2), (float) (centerX + r * Math.sin(angle / 2) - textSize), (float) (centerY + r * Math.cos(angle / 2) + defaultTextPadding + textSize), paint);
        canvas.drawText(titles.get(3), (float) (centerX - r * Math.sin(angle / 2) - textSize), (float) (centerY + r * Math.cos(angle / 2) + defaultTextPadding + textSize), paint);

        //绘制覆盖区域
        Path pathArea = new Path();
        pathArea.moveTo((float) (centerX - r * scaleList.get(0) / floorCount * Math.sin(angle / 2)), (float) (centerY - r * scaleList.get(0) / floorCount * Math.sin(angle / 2)));
        pathArea.lineTo((float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle / 2)), (float) (centerY - r * scaleList.get(1) / floorCount * Math.sin(angle / 2)));
        pathArea.lineTo((float) (centerX + r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.sin(angle / 2)));
        pathArea.lineTo((float) (centerX - r * scaleList.get(3) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(3) / floorCount * Math.sin(angle / 2)));
        pathArea.close();
        paint.setColor(areaColor);
        canvas.drawPath(pathArea, paint);
    }

    /**
     * 五角分布器   正五边形
     */
    private void drawFiveAngle(Canvas canvas) {
        //五边形的宽高   顶点到底边的垂直距离
        float ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding;
        float hh = ww;
        //中心点到各角的连线夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        Path pathLine = new Path();
        //中心点X
        float centerX = getWidth() / 2;
        //中心点Y
        float centerY = getHeight() / 2;
        float r = ww / 2;
        //画边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                float x1 = centerX;
                float y1 = centerY - ur;
                float x2 = (float) (centerX + ur * Math.sin(angle));
                float y2 = (float) (centerY - ur * Math.cos(angle));
                float x3 = (float) (centerX + ur * Math.sin(angle / 2));
                float y3 = (float) (centerY + ur * Math.cos(angle / 2));
                float x4 = (float) (centerX - ur * Math.sin(angle / 2));
                float y4 = (float) (centerY + ur * Math.cos(angle / 2));
                float x5 = (float) (centerX - ur * Math.sin(angle));
                float y5 = (float) (centerY - ur * Math.cos(angle));
                pathLine.moveTo(x1, y1);
                pathLine.lineTo(x2, y2);
                pathLine.lineTo(x3, y3);
                pathLine.lineTo(x4, y4);
                pathLine.lineTo(x5, y5);
                pathLine.close();
            }
            canvas.drawPath(pathLine, paint);
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo(centerX, centerY - r);
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle)), (float) (centerY - r * Math.cos(angle)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle)), (float) (centerY - r * Math.cos(angle)));
            canvas.drawPath(pathLine, paint);
        }
        //画文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), (float) (centerX - textSize), (float) (centerY - r - defaultTextPadding), paint);
        canvas.drawText(titles.get(1), (float) (centerX + r * Math.sin(angle) + defaultTextPadding), (float) (centerY - Math.cos(angle) * r + defaultTextPadding), paint);
        canvas.drawText(titles.get(2), (float) (centerX + Math.sin(angle / 2) * r - textSize), (float) (centerY + Math.cos(angle / 2) * r + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(3), (float) (centerX - Math.sin(angle / 2) * r - textSize), (float) (centerY + Math.cos(angle / 2) * r + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(4), (float) (centerX - r * Math.sin(angle) - defaultTextPadding - textSize * textLength), (float) (centerY - Math.cos(angle) * r + defaultTextPadding), paint);

        //画区域
        Path pathArea = new Path();
        pathArea.moveTo(centerX + 0f, centerY - ww / 2 * scaleList.get(0) / floorCount);
        pathArea.lineTo((float) (centerX + r * scaleList.get(1) / floorCount * Math.sin(angle)), (float) (centerY - r * scaleList.get(1) / floorCount * Math.cos(angle)));
        pathArea.lineTo((float) (centerX + r * scaleList.get(2) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(2) / floorCount * Math.cos(angle / 2)));
        pathArea.lineTo((float) (centerX - r * scaleList.get(3) / floorCount * Math.sin(angle / 2)), (float) (centerY + r * scaleList.get(3) / floorCount * Math.cos(angle / 2)));
        pathArea.lineTo((float) (centerX - r * scaleList.get(4) / floorCount * Math.sin(angle)), (float) (centerY - r * scaleList.get(4) / floorCount * Math.cos(angle)));
        pathArea.close();
        paint.setColor(areaColor);
        canvas.drawPath(pathArea, paint);
    }

    /**
     * 六角分布器  正六边形
     */
    private void drawSixAngle(Canvas canvas) {
        //六边形的宽   对角线的距离
        float ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding;
        //中心点到各角的连线夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        //中心点到各角的距离
        float r = ww / 2;
        //六边形的高   顶边到底边的距离
        float hh = (float) (2 * r * Math.sin(angle));
        Path pathLine = new Path();
        //中心点X
        float centerX = getWidth() / 2;
        //中心点Y
        float centerY = getHeight() / 2;
        //绘制边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                pathLine.moveTo((float) (centerX - ur * Math.cos(angle)), (float) (centerY - ur * Math.sin(angle)));
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle)), (float) (centerY - ur * Math.sin(angle)));
                pathLine.lineTo((float) (centerX + ur), centerY);
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle)), (float) (centerY + ur * Math.sin(angle)));
                pathLine.lineTo((float) (centerX - ur * Math.cos(angle)), (float) (centerY + ur * Math.sin(angle)));
                pathLine.lineTo((float) (centerX - ur), centerY);
                pathLine.close();
            }
            canvas.drawPath(pathLine, paint);
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle)), (float) (centerY - r * Math.sin(angle)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle)), (float) (centerY - r * Math.sin(angle)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r), centerY);
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle)), (float) (centerY + r * Math.sin(angle)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle)), (float) (centerY + r * Math.sin(angle)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo(centerX - r, centerY);
            canvas.drawPath(pathLine, paint);
        }
        //绘制文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), (float) (centerX - r * Math.cos(angle) - textSize), (float) (centerY - r * Math.sin(angle) - defaultTextPadding), paint);
        canvas.drawText(titles.get(1), (float) (centerX + r * Math.cos(angle) - textSize), (float) (centerY - r * Math.sin(angle) - defaultTextPadding), paint);
        canvas.drawText(titles.get(2), (float) (centerX + r + defaultTextPadding), (float) (centerY + textSize / 2), paint);
        canvas.drawText(titles.get(3), (float) (centerX + r * Math.cos(angle) - textSize), (float) (centerY + r * Math.sin(angle) + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(4), (float) (centerX - r * Math.cos(angle) - textSize), (float) (centerY + r * Math.sin(angle) + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(5), (float) (centerX - r - textSize * textLength - defaultTextPadding), (float) (centerY + textSize / 2), paint);

        //绘制覆盖区域
        pathLine.reset();
        pathLine.moveTo((float) (centerX - r * Math.cos(angle) * scaleList.get(0) / floorCount), (float) (centerY - r * Math.sin(angle) * scaleList.get(0) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.cos(angle) * scaleList.get(1) / floorCount), (float) (centerY - r * Math.sin(angle) * scaleList.get(1) / floorCount));
        pathLine.lineTo((float) (centerX + r * scaleList.get(2) / floorCount), centerY);
        pathLine.lineTo((float) (centerX + r * Math.cos(angle) * scaleList.get(3) / floorCount), (float) (centerY + r * Math.sin(angle) * scaleList.get(3) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.cos(angle) * scaleList.get(4) / floorCount), (float) (centerY + r * Math.sin(angle) * scaleList.get(4) / floorCount));
        pathLine.lineTo((float) (centerX - r * scaleList.get(5) / floorCount), centerY);
        pathLine.close();
        paint.setColor(areaColor);
        canvas.drawPath(pathLine, paint);
    }

    /**
     * 七角分布器   正七边形
     */
    private void drawSevenAngle(Canvas canvas) {
        //七边形的宽度
        float ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding;
        //七边形中心点链接各角的夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        //七边形的边长
        float a = (float) (ww / (1 + 2 * Math.cos(Math.PI * 52 / 180)));
        //七边形的顶角到底部的垂直距离
        float hh = (float) (ww * Math.tan(Math.PI * 50 / 180) / 2 + a * Math.sin(Math.PI * 52 / 180));
        //中心点到各角的距离
        float r = (float) (a / 2 / Math.sin(angle / 2));
        //中心点X
        float centerX = (getWidth() / 2);
        //中心点Y
        float ceterY = r + getPaddingTop() + textSize + defaultTextPadding;
        Path pathLine = new Path();
        //绘制边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                pathLine.moveTo(centerX, ceterY - ur);
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle)), (float) (ceterY - ur * Math.cos(angle)));
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle * 3 / 2)), (float) (ceterY + ur * Math.cos(angle * 3 / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle / 2)), (float) (ceterY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.sin(angle / 2)), (float) (ceterY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.sin(angle * 3 / 2)), (float) (ceterY + ur * Math.cos(angle * 3 / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.sin(angle)), (float) (ceterY - ur * Math.cos(angle)));
                pathLine.close();
            }
            canvas.drawPath(pathLine, paint);
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo(centerX, ceterY - r);
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle)), (float) (ceterY - r * Math.cos(angle)));
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle * 3 / 2)), (float) (ceterY + r * Math.cos(angle * 3 / 2)));
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2)), (float) (ceterY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2)), (float) (ceterY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle * 3 / 2)), (float) (ceterY + r * Math.cos(angle * 3 / 2)));
            pathLine.moveTo(centerX, ceterY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle)), (float) (ceterY - r * Math.cos(angle)));
            canvas.drawPath(pathLine, paint);
        }
        //绘制文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), centerX - textSize, getPaddingTop() + textSize, paint);
        canvas.drawText(titles.get(1), (float) (centerX + r * Math.sin(angle) + defaultTextPadding), (float) (ceterY - r * Math.cos(angle) + textSize / 2), paint);
        canvas.drawText(titles.get(2), (float) (centerX + r * Math.sin(angle * 3 / 2) + defaultTextPadding), (float) (ceterY + r * Math.cos(angle * 3 / 2) + textSize / 2), paint);
        canvas.drawText(titles.get(3), (float) (centerX + r * Math.sin(angle / 2) - textSize), (float) (ceterY + r * Math.cos(angle / 2) + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(4), (float) (centerX - r * Math.sin(angle / 2) - textSize), (float) (ceterY + r * Math.cos(angle / 2) + textSize + defaultTextPadding), paint);
        canvas.drawText(titles.get(5), (float) (centerX - r * Math.sin(angle * 3 / 2) - textSize * textLength - defaultTextPadding), (float) (ceterY + r * Math.cos(angle * 3 / 2) + textSize / 2), paint);
        canvas.drawText(titles.get(6), (float) (centerX - r * Math.sin(angle) - textSize * textLength - defaultTextPadding), (float) (ceterY - r * Math.cos(angle) + textSize / 2), paint);

        //绘制区域
        pathLine.reset();
        pathLine.moveTo(centerX, ceterY - r * scaleList.get(0) / floorCount);
        pathLine.lineTo((float) (centerX + r * Math.sin(angle) * scaleList.get(1) / floorCount), (float) (ceterY - r * Math.cos(angle) * scaleList.get(1) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.sin(angle * 3 / 2) * scaleList.get(2) / floorCount), (float) (ceterY + r * Math.cos(angle * 3 / 2) * scaleList.get(2) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2) * scaleList.get(3) / floorCount), (float) (ceterY + r * Math.cos(angle / 2) * scaleList.get(3) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2) * scaleList.get(4) / floorCount), (float) (ceterY + r * Math.cos(angle / 2) * scaleList.get(4) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.sin(angle * 3 / 2) * scaleList.get(5) / floorCount), (float) (ceterY + r * Math.cos(angle * 3 / 2) * scaleList.get(5) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.sin(angle) * scaleList.get(7) / floorCount), (float) (ceterY - r * Math.cos(angle) * scaleList.get(7) / floorCount));
        pathLine.close();
        paint.setColor(areaColor);
        canvas.drawPath(pathLine, paint);
    }

    /**
     * 八角分布器   正八边形
     */
    private void drawEightAngle(Canvas canvas) {
        //八边形的宽度
        float ww = mWidth - textLength * 2 * textSize - 2 * defaultTextPadding;
        float hh = ww;
        //八边形中心点链接各角的夹角
        float angle = (float) (Math.PI * 2 / angleCount);
        //中心点到各角的距离
        float r = (float) (ww / 2 / Math.cos(angle / 2));
        //中心点x
        float centerX = (getWidth() / 2);
        //中心点y
        float centerY = (getHeight() / 2);
        Path pathLine = new Path();
        //绘制边框
        if (isShowLine) {
            for (int i = 1; i <= floorCount; i++) {
                float ur = r * i / floorCount;
                pathLine.moveTo((float) (centerX - ur * Math.sin(angle / 2)), (float) (centerY - ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle / 2)), (float) (centerY - ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle / 2)), (float) (centerY - ur * Math.sin(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.cos(angle / 2)), (float) (centerY + ur * Math.sin(angle / 2)));
                pathLine.lineTo((float) (centerX + ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.sin(angle / 2)), (float) (centerY + ur * Math.cos(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.cos(angle / 2)), (float) (centerY + ur * Math.sin(angle / 2)));
                pathLine.lineTo((float) (centerX - ur * Math.cos(angle / 2)), (float) (centerY - ur * Math.sin(angle / 2)));
                pathLine.close();
            }
            canvas.drawPath(pathLine, paint);
        }
        //绘制连线
        if (isShowConnect) {
            pathLine.reset();
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2)), (float) (centerY - r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2)), (float) (centerY - r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2)), (float) (centerY - r * Math.sin(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2)), (float) (centerY + r * Math.sin(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2)), (float) (centerY + r * Math.cos(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2)), (float) (centerY + r * Math.sin(angle / 2)));
            pathLine.moveTo(centerX, centerY);
            pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2)), (float) (centerY - r * Math.sin(angle / 2)));
            canvas.drawPath(pathLine, paint);
        }
        //绘制文本
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(titles.get(0), (float) (centerX - r * Math.sin(angle / 2) - textSize), (float) (centerY - r * Math.cos(angle / 2) - defaultTextPadding), paint);
        canvas.drawText(titles.get(1), (float) (centerX + r * Math.sin(angle / 2) - textSize), (float) (centerY - r * Math.cos(angle / 2) - defaultTextPadding), paint);
        canvas.drawText(titles.get(2), (float) (centerX + r * Math.cos(angle / 2) + defaultTextPadding), (float) (centerY - r * Math.sin(angle / 2) + textSize / 2), paint);
        canvas.drawText(titles.get(3), (float) (centerX + r * Math.cos(angle / 2) + defaultTextPadding), (float) (centerY + r * Math.sin(angle / 2) + textSize / 2), paint);
        canvas.drawText(titles.get(4), (float) (centerX + r * Math.sin(angle / 2) - textSize), (float) (centerY + r * Math.cos(angle / 2) + defaultTextPadding + textSize), paint);
        canvas.drawText(titles.get(5), (float) (centerX - r * Math.sin(angle / 2) - textSize), (float) (centerY + r * Math.cos(angle / 2) + defaultTextPadding + textSize), paint);
        canvas.drawText(titles.get(6), (float) (centerX - r * Math.cos(angle / 2) - textSize * textLength - defaultTextPadding), (float) (centerY + r * Math.sin(angle / 2) + textSize / 2), paint);
        canvas.drawText(titles.get(7), (float) (centerX - r * Math.cos(angle / 2) - textSize * textLength - defaultTextPadding), (float) (centerY - r * Math.sin(angle / 2) + textSize / 2), paint);

        //绘制区域
        pathLine.reset();
        pathLine.moveTo((float) (centerX - r * Math.sin(angle / 2) * scaleList.get(0) / floorCount), (float) (centerY - r * Math.cos(angle / 2) * scaleList.get(0) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2) * scaleList.get(1) / floorCount), (float) (centerY - r * Math.cos(angle / 2) * scaleList.get(1) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2) * scaleList.get(2) / floorCount), (float) (centerY - r * Math.sin(angle / 2) * scaleList.get(2) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.cos(angle / 2) * scaleList.get(3) / floorCount), (float) (centerY + r * Math.sin(angle / 2) * scaleList.get(3) / floorCount));
        pathLine.lineTo((float) (centerX + r * Math.sin(angle / 2) * scaleList.get(4) / floorCount), (float) (centerY + r * Math.cos(angle / 2) * scaleList.get(4) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.sin(angle / 2) * scaleList.get(5) / floorCount), (float) (centerY + r * Math.cos(angle / 2) * scaleList.get(5) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2) * scaleList.get(6) / floorCount), (float) (centerY + r * Math.sin(angle / 2) * scaleList.get(6) / floorCount));
        pathLine.lineTo((float) (centerX - r * Math.cos(angle / 2) * scaleList.get(7) / floorCount), (float) (centerY - r * Math.sin(angle / 2) * scaleList.get(7) / floorCount));
        pathLine.close();
        paint.setColor(areaColor);
        canvas.drawPath(pathLine, paint);
    }
}
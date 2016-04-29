package com.xinyusoft.xshell.luckview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.xinyusoft.xshell.luckview.bean.PrizeStock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LuckyPanView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder mHolder;
	/**
	 * 与SurfaceHolder绑定的Canvas
	 */
	private Canvas mCanvas;
	/**
	 * 用于绘制的线程
	 */
	private Thread t;
	/**
	 * 线程的控制开关
	 */
	private boolean isRunning;

	/**
	 * 盘块的个数
	 */
	private int mItemCount = 4;

	/**
	 * 奖品
	 */
	private PrizeStock prize;

	/**
	 * 是否停下来了
	 */
	private boolean isStop;

	/**
	 * 绘制盘块的范围
	 */
	private RectF mRange = new RectF();
	/**
	 * 圆的直径
	 */
	private int mRadius;
	/**
	 * 绘制文字的画笔
	 */
	private Paint mTextPaint;

	/**
	 * 滚动的速度
	 */
	private double mSpeed = 0;
	private volatile float mStartAngle = 0;
	/**
	 * 是否点击了停止
	 */
	private boolean isShouldEnd;

	/**
	 * 控件的中心位置
	 */
	private int mCenter;

	/**
	 * 背景图的bitmap
	 */
	private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinyusoft_luckview_mypan);

	// 灯光的的bitmap
	private Bitmap mGuangBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinyusoft_luckview_guang);

	/**
	 * 指针的bitmap
	 */
	private Bitmap mPrintBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinyusoft_luckview_mypoint);

	/**
	 * 速度最大的时候指针的bitmap
	 */
	private Bitmap mMaxPrintBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinyusoft_luckview_maxmypoint);

	private Bitmap mButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinyusoft_luckview_centerimg);

	/**
	 * 文字的大小
	 */
	private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());

	/**
	 * 股票的所有数据
	 */
	private ArrayList<PrizeStock[]> myData;

	/**
	 * 每个盘块的角度
	 */
	private float mItemAngle;

	/**
	 * 指针旋转
	 */
	private Matrix panhandTrans = new Matrix();
	/**
	 * 转盘停止的时候监听
	 */
	private OnLuckPanStopListener listener;

	// 灯光渐变的画笔
	private Paint mGuangPaint;
	/** 灯光的渐变值 */
	private int mGuangAlphaNumber = 0;

	public LuckyPanView(Context context) {
		this(context, null);
	}

	public LuckyPanView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this);
		// 设置总是显示在最前端
		setZOrderOnTop(true);
		// 设置画布 背景透明
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);
		mItemAngle = 360f / mItemCount;
	}

	/**
	 * 与文字对应图片的bitmap数组
	 */
	private Bitmap[] mImgsBitmap;

	// /**
	// * 与文字对应的图片
	// */
	// private int[] mImgs = new int[]{R.drawable.xinyusoft_luckview_danfan,
	// R.drawable.ipad,
	// R.drawable.iphone, R.drawable.meizi};

	/**
	 * 设置数据源
	 *
	 * @param list
	 */
	public void setDate(List<PrizeStock> list) {
		final ArrayList<PrizeStock[]> myData = new ArrayList<PrizeStock[]>();
		PrizeStock[] prizeStocks = null;
		for (int i = 0; i < list.size(); i++) {
			PrizeStock prizeStock = list.get(i);
			if (i % 4 == 0) {
				prizeStocks = new PrizeStock[4];
				prizeStocks[0] = prizeStock;
			} else if (i % 4 == 1) {
				prizeStocks[1] = prizeStock;
			} else if (i % 4 == 2) {
				prizeStocks[2] = prizeStock;
			} else if (i % 4 == 3) {
				prizeStocks[3] = prizeStock;
				myData.add(prizeStocks);
			}
		}
		if (this.myData != null && this.myData.size() > 0) {
			this.myData.clear();
		}
		this.myData = myData;
	}

	/**
	 * 设置控件为正方形
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
		// 获取圆形的直径
		mRadius = width - getPaddingLeft() - getPaddingRight();
		// 中心点
		mCenter = width / 2;

		setMeasuredDimension(mBgBitmap.getWidth(), mBgBitmap.getHeight());
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 初始化绘制文字的画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xFF000000);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setDither(true);

		// 圆弧的绘制范围
		mRange = new RectF(0, 0, mRadius, mRadius);
		// 开启线程
		isRunning = true;
		t = new Thread(this);
		t.start();

		// 初始化图片
		// mImgsBitmap = new Bitmap[mItemCount];
		// for (int i = 0; i < mItemCount; i++) {
		// if (i == 3) {
		// mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),
		// mImgs[i]);
		// } else {
		// Bitmap bitmap = BitmapCompressTools.decodeSampledBitmapFromResource(
		// getResources(), R.drawable.mytest, 50, 50);
		// mImgsBitmap[i] = bitmap;
		// }
		//
		// }
		// 建立Paint 物件
		mGuangPaint = new Paint();
		mGuangPaint.setStyle(Paint.Style.STROKE); // 空心
		mGuangPaint.setAlpha(mGuangAlphaNumber);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 通知关闭线程
		isRunning = false;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			listener.onLuckPanStop(prize);
			updatePriceSpeed = 30;
		}
	};

	// 是否点击了再来一次
	private boolean isRestart = true;

	// 是否正在画，用于判断mStartAngle的数据准确性
	private volatile boolean isDraw = true;

	//更新奖品的速度
	private int updatePriceSpeed = 30;

	@Override
	public void run() {
		int timeCount = 0; // 计时器
		int mPan = 0;// 每个盘快的下标
		// 不断的进行draw
		while (isRunning) {
			if (myData == null || myData.size() == 0) {
				continue;
			}
			long start = System.currentTimeMillis();

			// 代表开始旋转了，更新奖品（缓慢的）,这个就相当于500毫秒更新一次盘块
			if (timeCount > updatePriceSpeed) {
				timeCount = 0;
				// 速度降到20一下就不会改变盘上的奖品
				if (mSpeed > 10) {
					mPan++;
					if (mPan >= myData.size()) {
						mPan = 0;
					}
				} else if (mSpeed == 0 && isRestart) {
					mPan++;
					if (mPan >= myData.size()) {
						mPan = 0;
					}
				}
			}
			timeCount++;
			myDraw(myData.get(mPan));
			long end = System.currentTimeMillis();
			try {
				if (end - start < 30) {
					Thread.sleep(30 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void myDraw(PrizeStock[] mPanData) {
		// 获得canvas
		mCanvas = mHolder.lockCanvas();
		try {
			if (mCanvas != null) {
				drawBg();
				// float sweepAngle = (360f / mItemCount);
				// float tmpPanAngle = 0;
				for (int i = 0; i < mItemCount; i++) {
					// 绘制文本
					// drawText(tmpPanAngle, sweepAngle, mPanData[i],i);
					drawText(mPanData[i], i);
					// drawIcon(tmpPanAngle, i);
					// tmpPanAngle += sweepAngle;
				}
				// 绘制灯光渐变
				if (isSpeed) {
					mGuangAlphaNumber += 4;
					if (mGuangAlphaNumber >= 255) {
						mGuangAlphaNumber = 255;
					}
					mGuangPaint.setAlpha(mGuangAlphaNumber);
					mCanvas.drawBitmap(mGuangBitmap, 0, 0, mGuangPaint);
				} else {
					mGuangAlphaNumber -= 4;
					if (mGuangAlphaNumber <= 0) {
						mGuangAlphaNumber = 0;
					}
					mGuangPaint.setAlpha(mGuangAlphaNumber);
					mCanvas.drawBitmap(mGuangBitmap, 0, 0, mGuangPaint);
				}

				// 绘制指针
				drawPointer(mStartAngle);
				// 点击停止时，设置mSpeed为递减，为0值转盘停止
				if (isShouldEnd) {
					if (isDraw) {
						float tempAngle = mStartAngle % mItemAngle;
						// 再加上一个每个盘块的角度只是为了跳跃性更流畅,15,是speed为50的时候，停下的来的总值是1275，360取于之后，
						// 为195，为了达到中心点（当item为4的时候，中心点为0,90,180,270），所以要减少15度
						mStartAngle = mStartAngle - tempAngle + mItemAngle - 10;
						isDraw = !isDraw;
					}
					mSpeed -= 1;
				}
				// 如果mSpeed不等于0，则相当于在滚动
				mStartAngle += mSpeed;

				// 当点击开始的时候，缓慢的加速
				if (mSpeed <= 40) {
					if (isSpeed) {
						mSpeed = mSpeed + 1;
					}
				}

				mCanvas.drawBitmap(mButtonBitmap, 0, 0, null);
				if (mSpeed <= 0) {
					mSpeed = 0;
					isShouldEnd = false;
					if (isStop) {
						mHandler.sendEmptyMessage(1);
						isStop = false;
					}
				}
				// 根据当前旋转的mStartAngle计算当前滚动到的区域
				calInExactArea(mStartAngle, mPanData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCanvas != null)
				mHolder.unlockCanvasAndPost(mCanvas);
		}

	}

	/**
	 * 绘制指针
	 *
	 * @param mStartAngle
	 */
	private void drawPointer(float mStartAngle) {

		if (mSpeed >= 35) {
			panhandTrans.setTranslate(mCenter - mMaxPrintBitmap.getWidth() / 2, mCenter - mMaxPrintBitmap.getHeight());
			mStartAngle %= 360.0f;
			panhandTrans.preRotate(mStartAngle, mMaxPrintBitmap.getWidth() / 2, mMaxPrintBitmap.getHeight());
			mCanvas.drawBitmap(mMaxPrintBitmap, panhandTrans, null);
		} else {
			panhandTrans.setTranslate(mCenter - mPrintBitmap.getWidth() / 2, mCenter - mPrintBitmap.getHeight());
			mStartAngle %= 360.0f;
			panhandTrans.preRotate(mStartAngle, mPrintBitmap.getWidth() / 2, mPrintBitmap.getHeight());
			mCanvas.drawBitmap(mPrintBitmap, panhandTrans, null);
		}
	}

	/**
	 * 根据当前旋转的mStartAngle计算当前滚动到的区域 绘制背景，不重要，完全为了美观
	 */
	private void drawBg() {
		// 设置透明
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

	}

	/**
	 * 根据当前旋转的mStartAngle计算当前滚动到的区域
	 *
	 * @param startAngle
	 */
	public void calInExactArea(float startAngle, PrizeStock[] mStrs) {
		// 让指针从水平向上开始计算 因为开始为负，有问题？ -------------怎么改？？？？？？
		float rotate = startAngle - 90;
		rotate %= 360.0;
		// 当这个小于90的时候，需要重新绘制指向不到3个区域，这个是固定死了，实在不知道怎么判断获奖范围了。
		if (rotate >= 45 && rotate < 135) {
			prize = mStrs[0];
		} else if (rotate >= 135 && rotate < 225) {
			prize = mStrs[1];
		} else if (rotate >= 225 && rotate < 315) {
			prize = mStrs[2];
		} else {
			prize = mStrs[3];
		}
	}
    DecimalFormat df = new DecimalFormat("0.00");
	/**
	 * 绘制文本
	 */
	private void drawText(PrizeStock prizeStock, int i) {
		double zdf = prizeStock.getZdf();

		if (zdf < 0) { // 如果有，代表涨跌幅为负
			mTextPaint.setColor(0xFF00FF00);
		} else if (zdf == 0) {
			mTextPaint.setColor(0xFFFFFFFF);
		} else {
			mTextPaint.setColor(0xFFFF0000);
		}
        String zdfString = df.format(zdf) + "%";
		Path path = new Path();
		float textWidth = mTextPaint.measureText(prizeStock.getName());
		if (i == 0) {
			path.moveTo(mRadius / 2 - textWidth / 2, mRadius / 10 * 8 + mTextSize / 2);
			path.lineTo(mRadius / 2 + textWidth / 2, mRadius / 10 * 8 + mTextSize / 2);
		} else if (i == 1) {
			path.moveTo(mRadius / 10 * 2 - mTextSize / 2, mRadius / 2 - textWidth / 2);
			path.lineTo(mRadius / 10 * 2 - mTextSize / 2, mRadius / 2 + textWidth / 2);
		} else if (i == 2) {
			path.moveTo(mRadius / 2 - textWidth / 2, mRadius / 10 * 2);
			path.lineTo(mRadius / 2 + textWidth / 2, mRadius / 10 * 2);
		} else if (i == 3) {
			path.moveTo(mRadius / 10 * 8, mRadius / 2 - textWidth / 2);
			path.lineTo(mRadius / 10 * 8, mRadius / 2 + textWidth / 2);
		}

		mCanvas.drawTextOnPath(prizeStock.getName(), path, 0, 0, mTextPaint);
		if (i == 0) {
			mCanvas.drawTextOnPath(zdfString, path, textWidth / 2 - mTextPaint.measureText(zdfString) / 2, -mTextSize, mTextPaint);
		} else if (i == 1) {
			mCanvas.drawTextOnPath(zdfString, path, textWidth / 2 - mTextPaint.measureText(zdfString) / 2, -mTextSize, mTextPaint);
		} else {
			mCanvas.drawTextOnPath(zdfString, path, textWidth / 2 - mTextPaint.measureText(zdfString) / 2, mTextSize, mTextPaint);
		}

	}

	private void drawTextbf2(float startAngle, float sweepAngle, PrizeStock prizeStock, int i) {
		String zdf = prizeStock.getZdf() + "";
		if ('-' == zdf.charAt(0)) { // 如果有，代表涨跌幅为负
			mTextPaint.setColor(0xFF00FF00);
			zdf = zdf.substring(0, 5);
		} else if ("0.0".equals(zdf)) {
			mTextPaint.setColor(0xFF000000);
		} else {
			mTextPaint.setColor(0xFFFF0000);
			zdf = zdf.substring(0, 4);
		}
		Path path = new Path();
		float textWidth = mTextPaint.measureText(prizeStock.getName());
		if (i == 0) {
			path.moveTo(mRadius / 2 - textWidth / 2, mRadius / 10 * 8 + mTextSize / 2);
			path.lineTo(mRadius / 2 + textWidth / 2, mRadius / 10 * 8 + mTextSize / 2);
		} else if (i == 1) {
			path.moveTo(mRadius / 10 * 2 - mTextSize / 2, mRadius / 2 - textWidth / 2);
			path.lineTo(mRadius / 10 * 2 - mTextSize / 2, mRadius / 2 + textWidth / 2);
		} else if (i == 2) {
			path.moveTo(mRadius / 2 - textWidth / 2, mRadius / 10 * 2);
			path.lineTo(mRadius / 2 + textWidth / 2, mRadius / 10 * 2);
		} else if (i == 3) {
			path.moveTo(mRadius / 10 * 8, mRadius / 2 - textWidth / 2);
			path.lineTo(mRadius / 10 * 8, mRadius / 2 + textWidth / 2);
		}

		startAngle += 45;
		// path.addArc(mRange, startAngle, sweepAngle);

		// 利用水平偏移让文字居中
		float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - (mTextPaint.measureText(prizeStock.getName())) / 2);// 水平偏移
		float hOffsetZdf = (float) (mRadius * Math.PI / mItemCount / 2 - (mTextPaint.measureText(zdf)) / 2);// 水平偏移

		// float vOffset = mRadius / 2 / 2;// 垂直偏移
		mCanvas.drawTextOnPath(prizeStock.getName(), path, 0, 0, mTextPaint);
		if (i == 0) {
			mCanvas.drawTextOnPath(zdf, path, textWidth / 2 - mTextPaint.measureText(zdf) / 2, -mTextSize, mTextPaint);
		} else if (i == 1) {
			mCanvas.drawTextOnPath(zdf, path, textWidth / 2 - mTextPaint.measureText(zdf) / 2, -mTextSize, mTextPaint);
		} else {
			mCanvas.drawTextOnPath(zdf, path, textWidth / 2 - mTextPaint.measureText(zdf) / 2, mTextSize, mTextPaint);
		}

	}

	private void drawText_bf(float startAngle, float sweepAngle, PrizeStock prizeStock) {
		String zdf = prizeStock.getZdf() + "";
		if ('-' == zdf.charAt(0)) { // 如果有，代表涨跌幅为负
			mTextPaint.setColor(0xFF00FF00);
			zdf = zdf.substring(0, 5);
		} else if ("0.0".equals(zdf)) {
			mTextPaint.setColor(0xFF000000);
		} else {
			mTextPaint.setColor(0xFFFF0000);
			zdf = zdf.substring(0, 4);
		}
		Path path = new Path();
		startAngle += 45;
		path.addArc(mRange, startAngle, sweepAngle);
		// float textWidth = mTextPaint.measureText(string);
		// 利用水平偏移让文字居中
		float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - (mTextPaint.measureText(prizeStock.getName())) / 2);// 水平偏移
		float hOffsetZdf = (float) (mRadius * Math.PI / mItemCount / 2 - (mTextPaint.measureText(zdf)) / 2);// 水平偏移

		// float vOffset = mRadius / 2 / 2;// 垂直偏移
		mCanvas.drawTextOnPath(prizeStock.getName(), path, hOffset, (mRadius / 2 / 2f), mTextPaint);
		mCanvas.drawTextOnPath(zdf, path, hOffsetZdf, (mRadius / 2 / 1.5f), mTextPaint);
	}

	private void drawIcon(float startAngle, int i) {
		// 设置图片的宽度为直径的1/8
		int imgWidth = mRadius / 8;

		float angle = (float) (startAngle * (Math.PI / 180));

		int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
		int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

		// 确定绘制图片的位置
		Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);

		mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);

	}

	/**
	 * 是否开始加速
	 */
	private boolean isSpeed = false;

	/**
	 * 点击开始旋转
	 */
	public void luckyStart() {
		isShouldEnd = false;
		isStop = false;
		isSpeed = true;
		mStartAngle = 0;
		mSpeed = 10;
		updatePriceSpeed = 10;
	}

	public void luckyEnd() {
		isShouldEnd = true;
		isStop = true;
		isSpeed = false;
		isRestart = false;
		isDraw = true;
	}

	public boolean isShouldEnd() {
		return isShouldEnd;
	}

	public void setOnLuckPanStopListener(OnLuckPanStopListener listener) {
		this.listener = listener;
	}

	/**
	 * 重新开始
	 */
	public void restart() {
		mStartAngle = 0;
		isRestart = true;
	}

	public void closeLuckview() {
		isRunning = false;
	}

	public interface OnLuckPanStopListener {
		void onLuckPanStop(PrizeStock prize);
	}

}

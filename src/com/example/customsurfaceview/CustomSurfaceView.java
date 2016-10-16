package com.example.customsurfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 *  自定义surfaceview
 *  实现效果：实现一个圆圈的缩放
 *  应用场景：绘画频率非常高，且需要自定义控制绘画帧率的情况下（照相机呈现图片，一些游戏的制作，有特殊需求进行帧率控制时候）
 *  实现原理：1）surfaceview的绘画操作是在UI线程和工作线程都可以进行的
 *  2）在surfaceview类中进行工作线程自定义，可以为surfaceholder所用，进行绘画操作
 *  3）surfaceview的大小可以自由控制
 *  4）surfaceholder对象包含surface对象的应用，从而可以控制该surface，并且内部定义了供surfaceholder对象回掉的callback接口对象
 *  5）surfaceview的生命周期和surface的生命周期是一样的
 *  实现步骤：继承surfaceview类并且实现surfaceholder.callback接口
 *  通过ge'tholder（）方法获取surfaceholder对象，对surfaceholder对象添加回调callback接口实现类
 * @author wsd_leiguoqiang
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	/**
	 * 绘制线程对象
	 */
	private LoopThread thread;
	/**
	 * 保存容器的宽度
	 */
	private int width;
	/**
	 * 保存容器的高度
	 */
	private int height;
	
	public CustomSurfaceView(Context context) {
		super(context);
		init();
	}

	public CustomSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	/**
	 * 自定义方法，进行创建surfaceview对象时候的相关初始化操作
	 */
	private void init() {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	/**
	 * 当surface对象创建的时候进行回调
	 * 用途：一般在此进行绘画线程的开启操作
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//穿件绘制线程
		thread = new LoopThread(holder, getContext());
		thread.setRunning(true);
		//开启线程,进行绘制操作
		thread.start();
	}
	/**
	 * 当surface对象的大小进行改变的时候进行回调用，一般用途不大
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	/**
	 * 当surface对象销毁的时候进行回调，
	 * 用途：一般在此进行绘画线程的停止操作
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
	}
	/**
	 * 自定义绘制图画工作线程
	 * @author wsd_leiguoqiang
	 */
	class LoopThread extends Thread{
		/**
		 * 标记变量，控制工作线程使用
		 */
		private boolean isRunning = false;
		/**
		 * surfaceHolder对象
		 */
		private SurfaceHolder holder;
		/**
		 * 上下文对象
		 */
		private Context context;
		/**
		 * 画笔工具
		 */
		private Paint paint;
		/**
		 * 圆的最大半径
		 */
		private float radius = 10f;
		/**
		 * 标记变量，标记圆圈的变化方向
		 */
		private boolean flag_orientation;
		
		public LoopThread(SurfaceHolder holder, Context context) {
			super();
			this.holder = holder;
			this.context = context;
			paint = new Paint();
			paint.setColor(Color.YELLOW);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
		}

		@Override
		public void run() {
			//定义画布变量
			Canvas canvas = null;
			//进行死循环操作绘制操作
			while(isRunning){
				try {
					//由于可能会通知开启很多线程进行绘制操作，故需要对控制该surfaceview的surfaceholder对象进行锁定
					synchronized (holder) {
						//surfaceholder锁定canvas对象
						canvas = holder.lockCanvas(null);
						//将锁定好的canvas对象，传递给下面方法，进行自定义绘制操作
						draw(canvas);
						//让线程休眠50毫秒，从而控制绘制的频率（帧数）
						Thread.sleep(50);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					//最后解锁canvas对象，并呈现绘制结果
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		/**
		 * 自定义方法，进行绘制操作
		 */
		private void draw(Canvas canvas) {
			//surfacceview需要进行手动清除之前的绘制内容
			canvas.drawColor(Color.BLACK);
			//将canvas对象的原点移动到200，200的位置，后面话圆就以这个坐标点作为参照点
			canvas.translate(200, 300);
			//绘制圆形
			if(radius==100f){
				flag_orientation = false;
			}else if(radius==10f){
				flag_orientation = true;
			}
			//放大操作
			if(flag_orientation){
				canvas.drawCircle(0, 0, radius++, paint);
			//缩小操作
			}else{
				canvas.drawCircle(0, 0, radius--, paint);
			}
		}
		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		width = widthMeasureSpec;
//		height = heightMeasureSpec;
//		System.out.println("width="+width+"height="+height);
	}
}

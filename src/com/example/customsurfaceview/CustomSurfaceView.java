package com.example.customsurfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 *  �Զ���surfaceview
 *  ʵ��Ч����ʵ��һ��ԲȦ������
 *  Ӧ�ó������滭Ƶ�ʷǳ��ߣ�����Ҫ�Զ�����ƻ滭֡�ʵ�����£����������ͼƬ��һЩ��Ϸ���������������������֡�ʿ���ʱ��
 *  ʵ��ԭ��1��surfaceview�Ļ滭��������UI�̺߳͹����̶߳����Խ��е�
 *  2����surfaceview���н��й����߳��Զ��壬����Ϊsurfaceholder���ã����л滭����
 *  3��surfaceview�Ĵ�С�������ɿ���
 *  4��surfaceholder�������surface�����Ӧ�ã��Ӷ����Կ��Ƹ�surface�������ڲ������˹�surfaceholder����ص���callback�ӿڶ���
 *  5��surfaceview���������ں�surface������������һ����
 *  ʵ�ֲ��裺�̳�surfaceview�ಢ��ʵ��surfaceholder.callback�ӿ�
 *  ͨ��ge'tholder����������ȡsurfaceholder���󣬶�surfaceholder������ӻص�callback�ӿ�ʵ����
 * @author wsd_leiguoqiang
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	/**
	 * �����̶߳���
	 */
	private LoopThread thread;
	/**
	 * ���������Ŀ��
	 */
	private int width;
	/**
	 * ���������ĸ߶�
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
	 * �Զ��巽�������д���surfaceview����ʱ�����س�ʼ������
	 */
	private void init() {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	/**
	 * ��surface���󴴽���ʱ����лص�
	 * ��;��һ���ڴ˽��л滭�̵߳Ŀ�������
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//���������߳�
		thread = new LoopThread(holder, getContext());
		thread.setRunning(true);
		//�����߳�,���л��Ʋ���
		thread.start();
	}
	/**
	 * ��surface����Ĵ�С���иı��ʱ����лص��ã�һ����;����
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	/**
	 * ��surface�������ٵ�ʱ����лص���
	 * ��;��һ���ڴ˽��л滭�̵߳�ֹͣ����
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
	}
	/**
	 * �Զ������ͼ�������߳�
	 * @author wsd_leiguoqiang
	 */
	class LoopThread extends Thread{
		/**
		 * ��Ǳ��������ƹ����߳�ʹ��
		 */
		private boolean isRunning = false;
		/**
		 * surfaceHolder����
		 */
		private SurfaceHolder holder;
		/**
		 * �����Ķ���
		 */
		private Context context;
		/**
		 * ���ʹ���
		 */
		private Paint paint;
		/**
		 * Բ�����뾶
		 */
		private float radius = 10f;
		/**
		 * ��Ǳ��������ԲȦ�ı仯����
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
			//���廭������
			Canvas canvas = null;
			//������ѭ���������Ʋ���
			while(isRunning){
				try {
					//���ڿ��ܻ�֪ͨ�����ܶ��߳̽��л��Ʋ���������Ҫ�Կ��Ƹ�surfaceview��surfaceholder�����������
					synchronized (holder) {
						//surfaceholder����canvas����
						canvas = holder.lockCanvas(null);
						//�������õ�canvas���󣬴��ݸ����淽���������Զ�����Ʋ���
						draw(canvas);
						//���߳�����50���룬�Ӷ����ƻ��Ƶ�Ƶ�ʣ�֡����
						Thread.sleep(50);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					//������canvas���󣬲����ֻ��ƽ��
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		/**
		 * �Զ��巽�������л��Ʋ���
		 */
		private void draw(Canvas canvas) {
			//surfacceview��Ҫ�����ֶ����֮ǰ�Ļ�������
			canvas.drawColor(Color.BLACK);
			//��canvas�����ԭ���ƶ���200��200��λ�ã����滰Բ��������������Ϊ���յ�
			canvas.translate(200, 300);
			//����Բ��
			if(radius==100f){
				flag_orientation = false;
			}else if(radius==10f){
				flag_orientation = true;
			}
			//�Ŵ����
			if(flag_orientation){
				canvas.drawCircle(0, 0, radius++, paint);
			//��С����
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

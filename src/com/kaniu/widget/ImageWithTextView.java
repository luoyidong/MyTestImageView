package com.kaniu.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageWithTextView extends View {
	public static final int DEFAULT_TEXTSIZE = 14;
	// 可绘制区域
	private int mViewWidth;
	private int mViewHeight;
	private int mViewLeftPadding;
	private int mViewRightPadding;
	private int mViewTopPadding;
	private int mViewBottomPadding;

	private List<Drawable> mDrawables;
	private int imgTotalWidth;
	// 图片设置的边距都是一样
	private int mImgLeftPadding;
	private int mImgRightPadding;

	private String mText;
	private TextPaint mPaint;
	private FontMetrics mFontMetrics;
	private int mTextSize;
	private float mTextHeight;

	private static Resources res;

	public ImageWithTextView(Context context) {
		this(context, null);
	}

	public ImageWithTextView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public ImageWithTextView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		init();
	}

	private void init() {
		// 在这里添加边距
		res = getResources();
		mTextSize = DEFAULT_TEXTSIZE;
		mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.DITHER_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
		mPaint.setTextSize(mTextSize);
	}

	// 暂时只能支持往左边添加图片
	public void addDrawableIntoViewById(int drawableId) {
		if (res != null) {
			if (mDrawables == null) {
				mDrawables = new ArrayList<Drawable>();
			}
			Drawable drawable = res.getDrawable(drawableId);
			mDrawables.add(drawable);
			invalidate();
		}
	}

	public boolean removeDrawableById(int drawableId) {
		if (mDrawables != null && mDrawables.size() > 0) {
			Drawable drawable = res.getDrawable(drawableId);
			if (mDrawables.contains(drawable)) {
				mDrawables.remove(drawable);
				invalidate();
				return true;
			}
		}
		return false;
	}

	public void setText(String content) {
		this.mText = content;
		invalidate();
	}

	public void setTextSize(int mSize) {
		this.mTextSize = mSize;
		mPaint.setTextSize(mTextSize);
	}

	public void setImagePadding(int padding) {
		this.mImgLeftPadding = padding;
		this.mImgRightPadding = padding;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int saveFlag = canvas.save();
		canvas.translate(mViewLeftPadding, mViewTopPadding);

		// 待寻找合适地方放置
		mViewWidth = getWidth() - mViewLeftPadding - mViewRightPadding;
		mViewHeight = getHeight() - mViewTopPadding - mViewBottomPadding;
		mTextHeight = mPaint.descent() - mPaint.ascent();
		Log.i("sms", "the descent is:"+mPaint.descent()+" and the ascent is:"+mPaint.ascent());
		if (mDrawables != null && mDrawables.size() > 0) {
			int saveImageCount = canvas.save();
			for (int index = 0; index < mDrawables.size(); index++) {
				int dY = (int) ((mTextHeight - mDrawables.get(index)
						.getIntrinsicHeight()) / 2);
				drawImage(canvas, mImgLeftPadding, dY, index);
				canvas.translate(mDrawables.get(index).getIntrinsicWidth(), 0);
				imgTotalWidth += mImgLeftPadding + mImgRightPadding;
			}
			canvas.restoreToCount(saveImageCount);
		}
		if (mText != null && !mText.trim().equals("")) {
			String textArray[] = makeTextAutoNewline(mText, mPaint, mViewWidth,
					imgTotalWidth);
			mFontMetrics = mPaint.getFontMetrics();
			int saveText = canvas.save();
			for (int ta_i = 0; ta_i < textArray.length; ta_i++) {
				if (ta_i == 0) {
					drawText(canvas, textArray[ta_i], imgTotalWidth, 0, mPaint);
				}
				canvas.translate(0, mTextHeight + mFontMetrics.leading);
				Log.i("sms", "the leading is:"+mFontMetrics.leading);
			}
			canvas.restoreToCount(saveText);
		}
		canvas.restoreToCount(saveFlag);
	}

	private void drawImage(Canvas canvas, int mX, int mY, int index) {
		Drawable drawable = mDrawables.get(index);
		int param[] = ajustImageSize(drawable, mPaint);
		imgTotalWidth += param[1];
		canvas.translate(mX, 0);
		canvas.save();
		canvas.translate(0, mY);
		drawable.setBounds(0, 0, param[0], param[1]);
		drawable.draw(canvas);
		canvas.restore();
	}

	private void drawText(Canvas canvas, String text, int fromX, int fromY,
			Paint paint) {
		float textHeight = paint.descent() - paint.ascent();
		float baseY = fromY + textHeight;
		canvas.drawText(text, 0, text.length() - 1, fromX, baseY, paint);
	}

	// 如果图片本身高度大于字体高度，则将图片合理缩放
	private int[] ajustImageSize(Drawable drawable, TextPaint paint) {
		float suitheight = paint.descent() - paint.ascent();
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		if (height > suitheight) {
			height = (int) suitheight;
			width = (int) ((float) (suitheight / height)) * width;
			return new int[] { width, height };
		} else {
			return new int[] { width, height };
		}
	}

	/**
	 * 将文本根据绘制到屏幕的宽度自动换行，注：String[lineCount+1]加入了图片行
	 * 
	 * @param text
	 *            要绘制的文本
	 * @param paint
	 *            画笔
	 * @param drawWidth
	 *            文本可绘制的区域
	 * @param occupyWidth
	 *            被图片占用的宽度
	 * @return 返回一个行数字符数组
	 */
	private static String[] makeTextAutoNewline(String text, Paint paint,
			int drawWidth, int occupyWidth) {
		int textLength = text.length();
		float totalWidth = paint.measureText(text);
		if (totalWidth <= drawWidth - occupyWidth) {
			return new String[] { text };
		} else {
			int firstremainWidth = drawWidth - occupyWidth;
			float remainTotalWidth = totalWidth - firstremainWidth;

			int lineCount = (int) Math.ceil(remainTotalWidth / drawWidth);
			String[] result = new String[lineCount + 1];

			int charStart = 0;
			int charEnd = 1;
			int curLine = 1;
			// 处理第一行的文本
			while (charStart < textLength) {
				if (paint.measureText(text, charStart, charEnd) > firstremainWidth) {
					result[0] = text.substring(charStart, --charEnd);
					charStart = charEnd;
					break;
				}
				if (charEnd == textLength) {
					result[0] = text.substring(charStart, charEnd);
					break;
				}
				charEnd++;
			}
			// 处理剩下的文本
			while (charStart < textLength) {
				if (paint.measureText(text, charStart, charEnd) > drawWidth) {
					result[curLine++] = text.substring(charStart, --charEnd);
					charStart = charEnd;
				}
				if (charEnd == textLength) {
					result[curLine] = text.substring(charStart, charEnd);
					break;
				}
				charEnd++;
			}
			return result;
		}
	}
}

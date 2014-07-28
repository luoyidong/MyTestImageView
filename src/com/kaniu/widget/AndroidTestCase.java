package com.kaniu.widget;

import android.graphics.Paint;

public class AndroidTestCase extends android.test.AndroidTestCase {
	public void test() {
		String contentString = "大致意思就是一个小方块，在屏";
		Paint paint = new Paint();

		String result[] = makeTextAutoNewline(contentString, paint, 160, 60);
		for (int i = 0; i < result.length; i++) {
			System.out.println("the i is:" + result[i]);
		}
	}

	public String[] makeTextAutoNewline(String text, Paint paint,
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

package com.biaoorder2.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biaoorder2.R;
import com.biaoorder2.bean.VegetableInformation;
import com.biaoorder2.Interface.SectionCallback;

import java.util.List;

public class SectionItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint; // 标题文本画笔
    private final int headerHeight; // 标题高度
    private final SectionCallback sectionCallback;

    private final Paint backgroundPaint; // 标题背景画笔

    private List<VegetableInformation> vegetableList;

    private final Rect textBounds = new Rect(); // 文本边界矩形


    public SectionItemDecoration(Context context, SectionCallback sectionCallback, List<VegetableInformation> vegetableList) {
        paint = new Paint();
        paint.setColor(context.getColor(R.color.black));
        paint.setTextSize(46);
        headerHeight = 100;
        paint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getColor(R.color.white)); // 设置背景颜色

        this.sectionCallback = sectionCallback;
        this.vegetableList = vegetableList;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        if (vegetableList.isEmpty()) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager == null) return;

        int childCount = parent.getChildCount();
        String previousHeader = ""; // 用于记录上一个标题

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (position == RecyclerView.NO_POSITION) continue;

            // 获取当前项的标题
            String currentHeader = vegetableList.get(position).getCategory();

            // 如果是分组中的第一个项或标题变化，绘制新的标题头
            if (!currentHeader.equals(previousHeader) || sectionCallback.isFirstInSection(position)) {
                // 设置标题头的位置，处理推顶效果
                int headerY = Math.max(headerHeight, child.getTop());
                // 如果即将到达下一个组标题，调整 headerY 实现“推顶”效果
                if (position + 1 < vegetableList.size() && !currentHeader.equals(vegetableList.get(position + 1).getCategory()) && child.getBottom() < headerY) {
                    headerY = child.getBottom(); // 实现推顶效果
                }
                // 绘制标题背景
                canvas.drawRect(parent.getPaddingLeft(), headerY - headerHeight,
                        parent.getWidth() - parent.getPaddingRight(), headerY, backgroundPaint);

                // 绘制标题文本
                paint.getTextBounds(currentHeader, 0, currentHeader.length(), textBounds);
                float textX = parent.getPaddingLeft() + 32;
                float textY = headerY - (headerHeight - textBounds.height()) / 2f;
                canvas.drawText(currentHeader, textX, textY, paint);

                previousHeader = currentHeader; // 更新上一个标题
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) return;
        // 给每个分组的第一个 item 设置顶部偏移，使标题能显示
        if (sectionCallback.isFirstInSection(position)) {
            outRect.top = headerHeight;
        } else {
            outRect.top = 0;
        }
    }
}

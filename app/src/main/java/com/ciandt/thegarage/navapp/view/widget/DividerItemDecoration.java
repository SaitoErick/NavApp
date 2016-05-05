package com.ciandt.thegarage.navapp.view.widget;

/**
 * Created by thales on 4/27/16.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class that represents divider.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
  /**
   * HORIZONTAL_LIST
   */
  public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

  /**
   * VERTICAL_LIST
   */
  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

  private static final int[] ATTRS = new int[] {
      android.R.attr.listDivider
  };

  private Drawable mDivider;
  private int mOrientation;
  private int mDividerColor;
  private DividerStyle mDividerStyle = DividerStyle.Dark;
  private int mDividerHeight;

  /**
   * Simple constructor to use when creating a view from code.
   *
   * @param context The Context the view is running in
   * @param orientation The list's orientation
   * @param dividerStyle The divider's style
   */
  public DividerItemDecoration(Context context, int orientation, DividerStyle dividerStyle) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    this.mDividerStyle = dividerStyle;
    a.recycle();
    setOrientation(orientation);
  }

  /**
   * The divider's style
   */
  public enum DividerStyle {
    Light,
    Dark,
    NoneColor,
    Default,
  }

  /**
   * Set color in divider
   *
   * @param color The string that represents the color
   */
  public void setmDividerColor(String color) {
    mDividerColor = Color.parseColor(color);
  }

  /**
   * Set color in divider
   *
   * @param color The integer that represents the color
   */
  public void setDividerColor(int color) {
    mDividerColor = color;
  }

  /**
   * Set divider height
   *
   * @param height The integer that represents the height
   */
  public void setmDividerHeight(int height) {
    mDividerHeight = height;
  }

  /**
   * Set list orientation
   *
   * @param orientation The integer that represents the orientation
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
      throw new IllegalArgumentException("invalid orientation");
    }
    mOrientation = orientation;
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    if (mOrientation == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  /**
   * Draw vertical lines
   *
   * @param c The canvas
   * @param parent The recycle view
   */
  public void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    if (mDividerStyle != DividerStyle.Default) {
      switch (mDividerStyle) {
        case Dark:
          mDividerColor = Color.argb(13, 0, 0, 0);
          break;
        case Light:
          mDividerColor = Color.WHITE;
          break;
        case NoneColor:
          mDividerColor = Color.TRANSPARENT;
          break;
        default:
          mDividerColor = Color.TRANSPARENT;
      }
      mDivider.setColorFilter(mDividerColor, PorterDuff.Mode.SRC_OUT);
    }

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  /**
   * Draw horizontal lines
   *
   * @param c The canvas
   * @param parent The recycle view
   */
  public void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int left = child.getRight() + params.rightMargin;
      final int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    if (mOrientation == VERTICAL_LIST) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight() + mDividerHeight);
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }
}
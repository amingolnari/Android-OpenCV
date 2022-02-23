package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import android.support.constraint.solver.widgets.ResolutionAnchor;
import android.support.v7.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintLayout extends ViewGroup {
    static final boolean ALLOWS_EMBEDDED = false;
    private static final boolean DEBUG = false;
    public static final int DESIGN_INFO_ID = 0;
    private static final String TAG = "ConstraintLayout";
    private static final boolean USE_CONSTRAINTS_HELPER = true;
    public static final String VERSION = "ConstraintLayout-1.1.0";
    SparseArray<View> mChildrenByIds = new SparseArray<>();
    private ArrayList<ConstraintHelper> mConstraintHelpers = new ArrayList<>(4);
    private ConstraintSet mConstraintSet = null;
    private int mConstraintSetId = -1;
    private HashMap<String, Integer> mDesignIds = new HashMap<>();
    private boolean mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    private int mLastMeasureHeight = -1;
    int mLastMeasureHeightMode = 0;
    int mLastMeasureHeightSize = -1;
    private int mLastMeasureWidth = -1;
    int mLastMeasureWidthMode = 0;
    int mLastMeasureWidthSize = -1;
    ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
    private int mMaxHeight = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    private int mMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    private Metrics mMetrics;
    private int mMinHeight = 0;
    private int mMinWidth = 0;
    private int mOptimizationLevel = 3;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList<>(100);

    public void setDesignInformation(int type, Object value1, Object value2) {
        if (type == 0 && (value1 instanceof String) && (value2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String name = (String) value1;
            int index = name.indexOf("/");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            this.mDesignIds.put(name, Integer.valueOf(((Integer) value2).intValue()));
        }
    }

    public Object getDesignInformation(int type, Object value) {
        if (type == 0 && (value instanceof String)) {
            String name = (String) value;
            if (this.mDesignIds != null && this.mDesignIds.containsKey(name)) {
                return this.mDesignIds.get(name);
            }
        }
        return null;
    }

    public ConstraintLayout(Context context) {
        super(context);
        init((AttributeSet) null);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setId(int id) {
        this.mChildrenByIds.remove(getId());
        super.setId(id);
        this.mChildrenByIds.put(getId(), this);
    }

    private void init(AttributeSet attrs) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = a.getDimensionPixelOffset(attr, this.mMinWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = a.getDimensionPixelOffset(attr, this.mMinHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = a.getDimensionPixelOffset(attr, this.mMaxWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = a.getDimensionPixelOffset(attr, this.mMaxHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = a.getInt(attr, this.mOptimizationLevel);
                } else if (attr == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    int id = a.getResourceId(attr, 0);
                    try {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.load(getContext(), id);
                    } catch (Resources.NotFoundException e) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = id;
                }
            }
            a.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 14) {
            onViewAdded(child);
        }
    }

    public void removeView(View view) {
        super.removeView(view);
        if (Build.VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    public void onViewAdded(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget widget = getViewWidget(view);
        if ((view instanceof Guideline) && !(widget instanceof Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.widget = new Guideline();
            layoutParams.isGuideline = USE_CONSTRAINTS_HELPER;
            ((Guideline) layoutParams.widget).setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper helper = (ConstraintHelper) view;
            helper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = USE_CONSTRAINTS_HELPER;
            if (!this.mConstraintHelpers.contains(helper)) {
                this.mConstraintHelpers.add(helper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void onViewRemoved(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        ConstraintWidget widget = getViewWidget(view);
        this.mLayoutWidget.remove(widget);
        this.mConstraintHelpers.remove(view);
        this.mVariableDimensionsWidgets.remove(widget);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void setMinWidth(int value) {
        if (value != this.mMinWidth) {
            this.mMinWidth = value;
            requestLayout();
        }
    }

    public void setMinHeight(int value) {
        if (value != this.mMinHeight) {
            this.mMinHeight = value;
            requestLayout();
        }
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int value) {
        if (value != this.mMaxWidth) {
            this.mMaxWidth = value;
            requestLayout();
        }
    }

    public void setMaxHeight(int value) {
        if (value != this.mMaxHeight) {
            this.mMaxHeight = value;
            requestLayout();
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private void updateHierarchy() {
        int count = getChildCount();
        boolean recompute = false;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            } else if (getChildAt(i).isLayoutRequested()) {
                recompute = USE_CONSTRAINTS_HELPER;
                break;
            } else {
                i++;
            }
        }
        if (recompute) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    private void setChildrenConstraints() {
        ConstraintWidget target;
        ConstraintWidget target2;
        ConstraintWidget target3;
        ConstraintWidget target4;
        boolean isInEditMode = isInEditMode();
        int count = getChildCount();
        if (isInEditMode) {
            for (int i = 0; i < count; i++) {
                View view = getChildAt(i);
                try {
                    String IdAsString = getResources().getResourceName(view.getId());
                    setDesignInformation(0, IdAsString, Integer.valueOf(view.getId()));
                    int slashIndex = IdAsString.indexOf(47);
                    if (slashIndex != -1) {
                        IdAsString = IdAsString.substring(slashIndex + 1);
                    }
                    getTargetWidget(view.getId()).setDebugName(IdAsString);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        for (int i2 = 0; i2 < count; i2++) {
            ConstraintWidget widget = getViewWidget(getChildAt(i2));
            if (widget != null) {
                widget.reset();
            }
        }
        if (this.mConstraintSetId != -1) {
            for (int i3 = 0; i3 < count; i3++) {
                View child = getChildAt(i3);
                if (child.getId() == this.mConstraintSetId && (child instanceof Constraints)) {
                    this.mConstraintSet = ((Constraints) child).getConstraintSet();
                }
            }
        }
        if (this.mConstraintSet != null) {
            this.mConstraintSet.applyToInternal(this);
        }
        this.mLayoutWidget.removeAllChildren();
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i4 = 0; i4 < helperCount; i4++) {
                this.mConstraintHelpers.get(i4).updatePreLayout(this);
            }
        }
        for (int i5 = 0; i5 < count; i5++) {
            View child2 = getChildAt(i5);
            if (child2 instanceof Placeholder) {
                ((Placeholder) child2).updatePreLayout(this);
            }
        }
        for (int i6 = 0; i6 < count; i6++) {
            View child3 = getChildAt(i6);
            ConstraintWidget widget2 = getViewWidget(child3);
            if (widget2 != null) {
                LayoutParams layoutParams = (LayoutParams) child3.getLayoutParams();
                layoutParams.validate();
                if (layoutParams.helped) {
                    layoutParams.helped = false;
                } else if (isInEditMode) {
                    try {
                        String IdAsString2 = getResources().getResourceName(child3.getId());
                        setDesignInformation(0, IdAsString2, Integer.valueOf(child3.getId()));
                        getTargetWidget(child3.getId()).setDebugName(IdAsString2.substring(IdAsString2.indexOf("id/") + 3));
                    } catch (Resources.NotFoundException e2) {
                    }
                }
                widget2.setVisibility(child3.getVisibility());
                if (layoutParams.isInPlaceholder) {
                    widget2.setVisibility(8);
                }
                widget2.setCompanionWidget(child3);
                this.mLayoutWidget.add(widget2);
                if (!layoutParams.verticalDimensionFixed || !layoutParams.horizontalDimensionFixed) {
                    this.mVariableDimensionsWidgets.add(widget2);
                }
                if (layoutParams.isGuideline) {
                    Guideline guideline = (Guideline) widget2;
                    int resolvedGuideBegin = layoutParams.resolvedGuideBegin;
                    int resolvedGuideEnd = layoutParams.resolvedGuideEnd;
                    float resolvedGuidePercent = layoutParams.resolvedGuidePercent;
                    if (Build.VERSION.SDK_INT < 17) {
                        resolvedGuideBegin = layoutParams.guideBegin;
                        resolvedGuideEnd = layoutParams.guideEnd;
                        resolvedGuidePercent = layoutParams.guidePercent;
                    }
                    if (resolvedGuidePercent != -1.0f) {
                        guideline.setGuidePercent(resolvedGuidePercent);
                    } else if (resolvedGuideBegin != -1) {
                        guideline.setGuideBegin(resolvedGuideBegin);
                    } else if (resolvedGuideEnd != -1) {
                        guideline.setGuideEnd(resolvedGuideEnd);
                    }
                } else if (layoutParams.leftToLeft != -1 || layoutParams.leftToRight != -1 || layoutParams.rightToLeft != -1 || layoutParams.rightToRight != -1 || layoutParams.startToStart != -1 || layoutParams.startToEnd != -1 || layoutParams.endToStart != -1 || layoutParams.endToEnd != -1 || layoutParams.topToTop != -1 || layoutParams.topToBottom != -1 || layoutParams.bottomToTop != -1 || layoutParams.bottomToBottom != -1 || layoutParams.baselineToBaseline != -1 || layoutParams.editorAbsoluteX != -1 || layoutParams.editorAbsoluteY != -1 || layoutParams.circleConstraint != -1 || layoutParams.width == -1 || layoutParams.height == -1) {
                    int resolvedLeftToLeft = layoutParams.resolvedLeftToLeft;
                    int resolvedLeftToRight = layoutParams.resolvedLeftToRight;
                    int resolvedRightToLeft = layoutParams.resolvedRightToLeft;
                    int resolvedRightToRight = layoutParams.resolvedRightToRight;
                    int resolveGoneLeftMargin = layoutParams.resolveGoneLeftMargin;
                    int resolveGoneRightMargin = layoutParams.resolveGoneRightMargin;
                    float resolvedHorizontalBias = layoutParams.resolvedHorizontalBias;
                    if (Build.VERSION.SDK_INT < 17) {
                        resolvedLeftToLeft = layoutParams.leftToLeft;
                        resolvedLeftToRight = layoutParams.leftToRight;
                        resolvedRightToLeft = layoutParams.rightToLeft;
                        resolvedRightToRight = layoutParams.rightToRight;
                        resolveGoneLeftMargin = layoutParams.goneLeftMargin;
                        resolveGoneRightMargin = layoutParams.goneRightMargin;
                        resolvedHorizontalBias = layoutParams.horizontalBias;
                        if (resolvedLeftToLeft == -1 && resolvedLeftToRight == -1) {
                            if (layoutParams.startToStart != -1) {
                                resolvedLeftToLeft = layoutParams.startToStart;
                            } else if (layoutParams.startToEnd != -1) {
                                resolvedLeftToRight = layoutParams.startToEnd;
                            }
                        }
                        if (resolvedRightToLeft == -1 && resolvedRightToRight == -1) {
                            if (layoutParams.endToStart != -1) {
                                resolvedRightToLeft = layoutParams.endToStart;
                            } else if (layoutParams.endToEnd != -1) {
                                resolvedRightToRight = layoutParams.endToEnd;
                            }
                        }
                    }
                    if (layoutParams.circleConstraint != -1) {
                        ConstraintWidget target5 = getTargetWidget(layoutParams.circleConstraint);
                        if (target5 != null) {
                            widget2.connectCircularConstraint(target5, layoutParams.circleAngle, layoutParams.circleRadius);
                        }
                    } else {
                        if (resolvedLeftToLeft != -1) {
                            ConstraintWidget target6 = getTargetWidget(resolvedLeftToLeft);
                            if (target6 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.LEFT, target6, ConstraintAnchor.Type.LEFT, layoutParams.leftMargin, resolveGoneLeftMargin);
                            }
                        } else if (!(resolvedLeftToRight == -1 || (target = getTargetWidget(resolvedLeftToRight)) == null)) {
                            widget2.immediateConnect(ConstraintAnchor.Type.LEFT, target, ConstraintAnchor.Type.RIGHT, layoutParams.leftMargin, resolveGoneLeftMargin);
                        }
                        if (resolvedRightToLeft != -1) {
                            ConstraintWidget target7 = getTargetWidget(resolvedRightToLeft);
                            if (target7 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.RIGHT, target7, ConstraintAnchor.Type.LEFT, layoutParams.rightMargin, resolveGoneRightMargin);
                            }
                        } else if (!(resolvedRightToRight == -1 || (target2 = getTargetWidget(resolvedRightToRight)) == null)) {
                            widget2.immediateConnect(ConstraintAnchor.Type.RIGHT, target2, ConstraintAnchor.Type.RIGHT, layoutParams.rightMargin, resolveGoneRightMargin);
                        }
                        if (layoutParams.topToTop != -1) {
                            ConstraintWidget target8 = getTargetWidget(layoutParams.topToTop);
                            if (target8 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.TOP, target8, ConstraintAnchor.Type.TOP, layoutParams.topMargin, layoutParams.goneTopMargin);
                            }
                        } else if (!(layoutParams.topToBottom == -1 || (target3 = getTargetWidget(layoutParams.topToBottom)) == null)) {
                            widget2.immediateConnect(ConstraintAnchor.Type.TOP, target3, ConstraintAnchor.Type.BOTTOM, layoutParams.topMargin, layoutParams.goneTopMargin);
                        }
                        if (layoutParams.bottomToTop != -1) {
                            ConstraintWidget target9 = getTargetWidget(layoutParams.bottomToTop);
                            if (target9 != null) {
                                widget2.immediateConnect(ConstraintAnchor.Type.BOTTOM, target9, ConstraintAnchor.Type.TOP, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                            }
                        } else if (!(layoutParams.bottomToBottom == -1 || (target4 = getTargetWidget(layoutParams.bottomToBottom)) == null)) {
                            widget2.immediateConnect(ConstraintAnchor.Type.BOTTOM, target4, ConstraintAnchor.Type.BOTTOM, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                        }
                        if (layoutParams.baselineToBaseline != -1) {
                            View view2 = this.mChildrenByIds.get(layoutParams.baselineToBaseline);
                            ConstraintWidget target10 = getTargetWidget(layoutParams.baselineToBaseline);
                            if (!(target10 == null || view2 == null || !(view2.getLayoutParams() instanceof LayoutParams))) {
                                layoutParams.needsBaseline = USE_CONSTRAINTS_HELPER;
                                ((LayoutParams) view2.getLayoutParams()).needsBaseline = USE_CONSTRAINTS_HELPER;
                                widget2.getAnchor(ConstraintAnchor.Type.BASELINE).connect(target10.getAnchor(ConstraintAnchor.Type.BASELINE), 0, -1, ConstraintAnchor.Strength.STRONG, 0, USE_CONSTRAINTS_HELPER);
                                widget2.getAnchor(ConstraintAnchor.Type.TOP).reset();
                                widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).reset();
                            }
                        }
                        if (resolvedHorizontalBias >= 0.0f && resolvedHorizontalBias != 0.5f) {
                            widget2.setHorizontalBiasPercent(resolvedHorizontalBias);
                        }
                        if (layoutParams.verticalBias >= 0.0f && layoutParams.verticalBias != 0.5f) {
                            widget2.setVerticalBiasPercent(layoutParams.verticalBias);
                        }
                    }
                    if (isInEditMode && !(layoutParams.editorAbsoluteX == -1 && layoutParams.editorAbsoluteY == -1)) {
                        widget2.setOrigin(layoutParams.editorAbsoluteX, layoutParams.editorAbsoluteY);
                    }
                    if (layoutParams.horizontalDimensionFixed) {
                        widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget2.setWidth(layoutParams.width);
                    } else if (layoutParams.width == -1) {
                        widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                        widget2.getAnchor(ConstraintAnchor.Type.LEFT).mMargin = layoutParams.leftMargin;
                        widget2.getAnchor(ConstraintAnchor.Type.RIGHT).mMargin = layoutParams.rightMargin;
                    } else {
                        widget2.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                        widget2.setWidth(0);
                    }
                    if (layoutParams.verticalDimensionFixed) {
                        widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget2.setHeight(layoutParams.height);
                    } else if (layoutParams.height == -1) {
                        widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                        widget2.getAnchor(ConstraintAnchor.Type.TOP).mMargin = layoutParams.topMargin;
                        widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).mMargin = layoutParams.bottomMargin;
                    } else {
                        widget2.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                        widget2.setHeight(0);
                    }
                    if (layoutParams.dimensionRatio != null) {
                        widget2.setDimensionRatio(layoutParams.dimensionRatio);
                    }
                    widget2.setHorizontalWeight(layoutParams.horizontalWeight);
                    widget2.setVerticalWeight(layoutParams.verticalWeight);
                    widget2.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
                    widget2.setVerticalChainStyle(layoutParams.verticalChainStyle);
                    widget2.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth, layoutParams.matchConstraintMinWidth, layoutParams.matchConstraintMaxWidth, layoutParams.matchConstraintPercentWidth);
                    widget2.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight, layoutParams.matchConstraintMinHeight, layoutParams.matchConstraintMaxHeight, layoutParams.matchConstraintPercentHeight);
                }
            }
        }
    }

    private final ConstraintWidget getTargetWidget(int id) {
        if (id == 0) {
            return this.mLayoutWidget;
        }
        View view = this.mChildrenByIds.get(id);
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    private void internalMeasureChildren(int parentWidthSpec, int parentHeightSpec) {
        int baseline;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (!params.isGuideline && !params.isHelper) {
                    widget.setVisibility(child.getVisibility());
                    int width = params.width;
                    int height = params.height;
                    boolean didWrapMeasureWidth = false;
                    boolean didWrapMeasureHeight = false;
                    if ((params.horizontalDimensionFixed || params.verticalDimensionFixed || (!params.horizontalDimensionFixed && params.matchConstraintDefaultWidth == 1) || params.width == -1 || (!params.verticalDimensionFixed && (params.matchConstraintDefaultHeight == 1 || params.height == -1))) ? USE_CONSTRAINTS_HELPER : false) {
                        if (width == 0) {
                            childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, -2);
                            didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                        } else if (width == -1) {
                            childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, -1);
                        } else {
                            if (width == -2) {
                                didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                            }
                            childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, width);
                        }
                        if (height == 0) {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -2);
                            didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                        } else if (height == -1) {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -1);
                        } else {
                            if (height == -2) {
                                didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                            }
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, height);
                        }
                        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                        if (this.mMetrics != null) {
                            this.mMetrics.measures++;
                        }
                        widget.setWidthWrapContent(width == -2 ? USE_CONSTRAINTS_HELPER : false);
                        widget.setHeightWrapContent(height == -2 ? USE_CONSTRAINTS_HELPER : false);
                        width = child.getMeasuredWidth();
                        height = child.getMeasuredHeight();
                    }
                    widget.setWidth(width);
                    widget.setHeight(height);
                    if (didWrapMeasureWidth) {
                        widget.setWrapWidth(width);
                    }
                    if (didWrapMeasureHeight) {
                        widget.setWrapHeight(height);
                    }
                    if (params.needsBaseline && (baseline = child.getBaseline()) != -1) {
                        widget.setBaselineDistance(baseline);
                    }
                }
            }
        }
    }

    private void updatePostMeasures() {
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child instanceof Placeholder) {
                ((Placeholder) child).updatePostMeasure(this);
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                this.mConstraintHelpers.get(i2).updatePostMeasure(this);
            }
        }
    }

    private void internalMeasureDimensions(int parentWidthSpec, int parentHeightSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        int baseline;
        int baseline2;
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (!params.isGuideline && !params.isHelper) {
                    widget.setVisibility(child.getVisibility());
                    int width = params.width;
                    int height = params.height;
                    if (width == 0 || height == 0) {
                        widget.getResolutionWidth().invalidate();
                        widget.getResolutionHeight().invalidate();
                    } else {
                        boolean didWrapMeasureWidth = false;
                        boolean didWrapMeasureHeight = false;
                        if (width == -2) {
                            didWrapMeasureWidth = USE_CONSTRAINTS_HELPER;
                        }
                        int childWidthMeasureSpec2 = getChildMeasureSpec(parentWidthSpec, widthPadding, width);
                        if (height == -2) {
                            didWrapMeasureHeight = USE_CONSTRAINTS_HELPER;
                        }
                        child.measure(childWidthMeasureSpec2, getChildMeasureSpec(parentHeightSpec, heightPadding, height));
                        if (this.mMetrics != null) {
                            this.mMetrics.measures++;
                        }
                        widget.setWidthWrapContent(width == -2 ? USE_CONSTRAINTS_HELPER : false);
                        widget.setHeightWrapContent(height == -2 ? USE_CONSTRAINTS_HELPER : false);
                        int width2 = child.getMeasuredWidth();
                        int height2 = child.getMeasuredHeight();
                        widget.setWidth(width2);
                        widget.setHeight(height2);
                        if (didWrapMeasureWidth) {
                            widget.setWrapWidth(width2);
                        }
                        if (didWrapMeasureHeight) {
                            widget.setWrapHeight(height2);
                        }
                        if (params.needsBaseline && (baseline2 = child.getBaseline()) != -1) {
                            widget.setBaselineDistance(baseline2);
                        }
                        if (params.horizontalDimensionFixed && params.verticalDimensionFixed) {
                            widget.getResolutionWidth().resolve(width2);
                            widget.getResolutionHeight().resolve(height2);
                        }
                    }
                }
            }
        }
        this.mLayoutWidget.solveGraph();
        for (int i2 = 0; i2 < widgetsCount; i2++) {
            View child2 = getChildAt(i2);
            if (child2.getVisibility() != 8) {
                LayoutParams params2 = (LayoutParams) child2.getLayoutParams();
                ConstraintWidget widget2 = params2.widget;
                if (!params2.isGuideline && !params2.isHelper) {
                    widget2.setVisibility(child2.getVisibility());
                    int width3 = params2.width;
                    int height3 = params2.height;
                    if (width3 == 0 || height3 == 0) {
                        ResolutionAnchor left = widget2.getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
                        ResolutionAnchor right = widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getResolutionNode();
                        boolean bothHorizontal = (widget2.getAnchor(ConstraintAnchor.Type.LEFT).getTarget() == null || widget2.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget() == null) ? false : USE_CONSTRAINTS_HELPER;
                        ResolutionAnchor top = widget2.getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
                        ResolutionAnchor bottom = widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getResolutionNode();
                        boolean bothVertical = (widget2.getAnchor(ConstraintAnchor.Type.TOP).getTarget() == null || widget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget() == null) ? false : USE_CONSTRAINTS_HELPER;
                        if (width3 != 0 || height3 != 0 || !bothHorizontal || !bothVertical) {
                            boolean didWrapMeasureWidth2 = false;
                            boolean didWrapMeasureHeight2 = false;
                            boolean resolveWidth = this.mLayoutWidget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
                            boolean resolveHeight = this.mLayoutWidget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT ? USE_CONSTRAINTS_HELPER : false;
                            if (!resolveWidth) {
                                widget2.getResolutionWidth().invalidate();
                            }
                            if (!resolveHeight) {
                                widget2.getResolutionHeight().invalidate();
                            }
                            if (width3 == 0) {
                                if (!resolveWidth || !widget2.isSpreadWidth() || !bothHorizontal || !left.isResolved() || !right.isResolved()) {
                                    childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, -2);
                                    didWrapMeasureWidth2 = USE_CONSTRAINTS_HELPER;
                                    resolveWidth = false;
                                } else {
                                    width3 = (int) (right.getResolvedValue() - left.getResolvedValue());
                                    widget2.getResolutionWidth().resolve(width3);
                                    childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, width3);
                                }
                            } else if (width3 == -1) {
                                childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, -1);
                            } else {
                                if (width3 == -2) {
                                    didWrapMeasureWidth2 = USE_CONSTRAINTS_HELPER;
                                }
                                childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, width3);
                            }
                            if (height3 == 0) {
                                if (!resolveHeight || !widget2.isSpreadHeight() || !bothVertical || !top.isResolved() || !bottom.isResolved()) {
                                    childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -2);
                                    didWrapMeasureHeight2 = USE_CONSTRAINTS_HELPER;
                                    resolveHeight = false;
                                } else {
                                    height3 = (int) (bottom.getResolvedValue() - top.getResolvedValue());
                                    widget2.getResolutionHeight().resolve(height3);
                                    childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, height3);
                                }
                            } else if (height3 == -1) {
                                childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -1);
                            } else {
                                if (height3 == -2) {
                                    didWrapMeasureHeight2 = USE_CONSTRAINTS_HELPER;
                                }
                                childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, height3);
                            }
                            child2.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                            if (this.mMetrics != null) {
                                this.mMetrics.measures++;
                            }
                            widget2.setWidthWrapContent(width3 == -2 ? USE_CONSTRAINTS_HELPER : false);
                            widget2.setHeightWrapContent(height3 == -2 ? USE_CONSTRAINTS_HELPER : false);
                            int width4 = child2.getMeasuredWidth();
                            int height4 = child2.getMeasuredHeight();
                            widget2.setWidth(width4);
                            widget2.setHeight(height4);
                            if (didWrapMeasureWidth2) {
                                widget2.setWrapWidth(width4);
                            }
                            if (didWrapMeasureHeight2) {
                                widget2.setWrapHeight(height4);
                            }
                            if (resolveWidth) {
                                widget2.getResolutionWidth().resolve(width4);
                            } else {
                                widget2.getResolutionWidth().remove();
                            }
                            if (resolveHeight) {
                                widget2.getResolutionHeight().resolve(height4);
                            } else {
                                widget2.getResolutionHeight().remove();
                            }
                            if (params2.needsBaseline && (baseline = child2.getBaseline()) != -1) {
                                widget2.setBaselineDistance(baseline);
                            }
                        }
                    }
                }
            }
        }
    }

    public void fillMetrics(Metrics metrics) {
        this.mMetrics = metrics;
        this.mLayoutWidget.fillMetrics(metrics);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x006a, code lost:
        r47 = r52.mLastMeasureHeightMode;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r53, int r54) {
        /*
            r52 = this;
            long r38 = java.lang.System.currentTimeMillis()
            r4 = 0
            r5 = 0
            int r43 = android.view.View.MeasureSpec.getMode(r53)
            int r45 = android.view.View.MeasureSpec.getSize(r53)
            int r16 = android.view.View.MeasureSpec.getMode(r54)
            int r18 = android.view.View.MeasureSpec.getSize(r54)
            r0 = r52
            int r0 = r0.mLastMeasureWidth
            r47 = r0
            r48 = -1
            r0 = r47
            r1 = r48
            if (r0 == r1) goto L_0x025e
            r0 = r52
            int r0 = r0.mLastMeasureHeight
            r47 = r0
            r48 = -1
            r0 = r47
            r1 = r48
            if (r0 == r1) goto L_0x025e
            r40 = 1
        L_0x0034:
            r47 = 1073741824(0x40000000, float:2.0)
            r0 = r43
            r1 = r47
            if (r0 != r1) goto L_0x0262
            r47 = 1073741824(0x40000000, float:2.0)
            r0 = r16
            r1 = r47
            if (r0 != r1) goto L_0x0262
            r0 = r52
            int r0 = r0.mLastMeasureWidth
            r47 = r0
            r0 = r45
            r1 = r47
            if (r0 != r1) goto L_0x0262
            r0 = r52
            int r0 = r0.mLastMeasureHeight
            r47 = r0
            r0 = r18
            r1 = r47
            if (r0 != r1) goto L_0x0262
            r34 = 1
        L_0x005e:
            r0 = r52
            int r0 = r0.mLastMeasureWidthMode
            r47 = r0
            r0 = r43
            r1 = r47
            if (r0 != r1) goto L_0x0266
            r0 = r52
            int r0 = r0.mLastMeasureHeightMode
            r47 = r0
            r0 = r16
            r1 = r47
            if (r0 != r1) goto L_0x0266
            r33 = 1
        L_0x0078:
            if (r33 == 0) goto L_0x026a
            r0 = r52
            int r0 = r0.mLastMeasureWidthSize
            r47 = r0
            r0 = r45
            r1 = r47
            if (r0 != r1) goto L_0x026a
            r0 = r52
            int r0 = r0.mLastMeasureHeightSize
            r47 = r0
            r0 = r18
            r1 = r47
            if (r0 != r1) goto L_0x026a
            r32 = 1
        L_0x0094:
            if (r33 == 0) goto L_0x026e
            r47 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r43
            r1 = r47
            if (r0 != r1) goto L_0x026e
            r47 = 1073741824(0x40000000, float:2.0)
            r0 = r16
            r1 = r47
            if (r0 != r1) goto L_0x026e
            r0 = r52
            int r0 = r0.mLastMeasureWidth
            r47 = r0
            r0 = r45
            r1 = r47
            if (r0 < r1) goto L_0x026e
            r0 = r52
            int r0 = r0.mLastMeasureHeight
            r47 = r0
            r0 = r18
            r1 = r47
            if (r0 != r1) goto L_0x026e
            r14 = 1
        L_0x00bf:
            if (r33 == 0) goto L_0x0271
            r47 = 1073741824(0x40000000, float:2.0)
            r0 = r43
            r1 = r47
            if (r0 != r1) goto L_0x0271
            r47 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r16
            r1 = r47
            if (r0 != r1) goto L_0x0271
            r0 = r52
            int r0 = r0.mLastMeasureWidth
            r47 = r0
            r0 = r45
            r1 = r47
            if (r0 != r1) goto L_0x0271
            r0 = r52
            int r0 = r0.mLastMeasureHeight
            r47 = r0
            r0 = r18
            r1 = r47
            if (r0 < r1) goto L_0x0271
            r13 = 1
        L_0x00ea:
            r0 = r43
            r1 = r52
            r1.mLastMeasureWidthMode = r0
            r0 = r16
            r1 = r52
            r1.mLastMeasureHeightMode = r0
            r0 = r45
            r1 = r52
            r1.mLastMeasureWidthSize = r0
            r0 = r18
            r1 = r52
            r1.mLastMeasureHeightSize = r0
            int r27 = r52.getPaddingLeft()
            int r28 = r52.getPaddingTop()
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r27
            r0.setX(r1)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r28
            r0.setY(r1)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r52
            int r0 = r0.mMaxWidth
            r48 = r0
            r47.setMaxWidth(r48)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r52
            int r0 = r0.mMaxHeight
            r48 = r0
            r47.setMaxHeight(r48)
            int r47 = android.os.Build.VERSION.SDK_INT
            r48 = 17
            r0 = r47
            r1 = r48
            if (r0 < r1) goto L_0x0167
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r48 = r0
            int r47 = r52.getLayoutDirection()
            r49 = 1
            r0 = r47
            r1 = r49
            if (r0 != r1) goto L_0x0274
            r47 = 1
        L_0x0160:
            r0 = r48
            r1 = r47
            r0.setRtl(r1)
        L_0x0167:
            r52.setSelfDimensionBehaviour(r53, r54)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r37 = r47.getWidth()
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r36 = r47.getHeight()
            r0 = r52
            boolean r0 = r0.mDirtyHierarchy
            r47 = r0
            if (r47 == 0) goto L_0x0191
            r47 = 0
            r0 = r47
            r1 = r52
            r1.mDirtyHierarchy = r0
            r52.updateHierarchy()
        L_0x0191:
            r0 = r52
            int r0 = r0.mOptimizationLevel
            r47 = r0
            r47 = r47 & 8
            r48 = 8
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x0278
            r26 = 1
        L_0x01a3:
            if (r26 == 0) goto L_0x027c
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r47.preOptimize()
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r37
            r2 = r36
            r0.optimizeForDimensions(r1, r2)
            r52.internalMeasureDimensions(r53, r54)
        L_0x01c0:
            r52.updatePostMeasures()
            int r47 = r52.getChildCount()
            if (r47 <= 0) goto L_0x01d2
            java.lang.String r47 = "First pass"
            r0 = r52
            r1 = r47
            r0.solveLinearSystem(r1)
        L_0x01d2:
            r10 = 0
            r0 = r52
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r0 = r0.mVariableDimensionsWidgets
            r47 = r0
            int r35 = r47.size()
            int r47 = r52.getPaddingBottom()
            int r17 = r28 + r47
            int r47 = r52.getPaddingRight()
            int r44 = r27 + r47
            if (r35 <= 0) goto L_0x0510
            r25 = 0
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r47 = r47.getHorizontalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r48 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x0281
            r12 = 1
        L_0x0200:
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r47 = r47.getVerticalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r48 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x0284
            r11 = 1
        L_0x0213:
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getWidth()
            r0 = r52
            int r0 = r0.mMinWidth
            r48 = r0
            int r24 = java.lang.Math.max(r47, r48)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getHeight()
            r0 = r52
            int r0 = r0.mMinHeight
            r48 = r0
            int r23 = java.lang.Math.max(r47, r48)
            r20 = 0
        L_0x023d:
            r0 = r20
            r1 = r35
            if (r0 >= r1) goto L_0x041a
            r0 = r52
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r0 = r0.mVariableDimensionsWidgets
            r47 = r0
            r0 = r47
            r1 = r20
            java.lang.Object r42 = r0.get(r1)
            android.support.constraint.solver.widgets.ConstraintWidget r42 = (android.support.constraint.solver.widgets.ConstraintWidget) r42
            java.lang.Object r9 = r42.getCompanionWidget()
            android.view.View r9 = (android.view.View) r9
            if (r9 != 0) goto L_0x0286
        L_0x025b:
            int r20 = r20 + 1
            goto L_0x023d
        L_0x025e:
            r40 = 0
            goto L_0x0034
        L_0x0262:
            r34 = 0
            goto L_0x005e
        L_0x0266:
            r33 = 0
            goto L_0x0078
        L_0x026a:
            r32 = 0
            goto L_0x0094
        L_0x026e:
            r14 = 0
            goto L_0x00bf
        L_0x0271:
            r13 = 0
            goto L_0x00ea
        L_0x0274:
            r47 = 0
            goto L_0x0160
        L_0x0278:
            r26 = 0
            goto L_0x01a3
        L_0x027c:
            r52.internalMeasureChildren(r53, r54)
            goto L_0x01c0
        L_0x0281:
            r12 = 0
            goto L_0x0200
        L_0x0284:
            r11 = 0
            goto L_0x0213
        L_0x0286:
            android.view.ViewGroup$LayoutParams r29 = r9.getLayoutParams()
            android.support.constraint.ConstraintLayout$LayoutParams r29 = (android.support.constraint.ConstraintLayout.LayoutParams) r29
            r0 = r29
            boolean r0 = r0.isHelper
            r47 = r0
            if (r47 != 0) goto L_0x025b
            r0 = r29
            boolean r0 = r0.isGuideline
            r47 = r0
            if (r47 != 0) goto L_0x025b
            int r47 = r9.getVisibility()
            r48 = 8
            r0 = r47
            r1 = r48
            if (r0 == r1) goto L_0x025b
            if (r26 == 0) goto L_0x02be
            android.support.constraint.solver.widgets.ResolutionDimension r47 = r42.getResolutionWidth()
            boolean r47 = r47.isResolved()
            if (r47 == 0) goto L_0x02be
            android.support.constraint.solver.widgets.ResolutionDimension r47 = r42.getResolutionHeight()
            boolean r47 = r47.isResolved()
            if (r47 != 0) goto L_0x025b
        L_0x02be:
            r46 = 0
            r19 = 0
            r0 = r29
            int r0 = r0.width
            r47 = r0
            r48 = -2
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x0402
            r0 = r29
            boolean r0 = r0.horizontalDimensionFixed
            r47 = r0
            if (r47 == 0) goto L_0x0402
            r0 = r29
            int r0 = r0.width
            r47 = r0
            r0 = r53
            r1 = r44
            r2 = r47
            int r46 = getChildMeasureSpec(r0, r1, r2)
        L_0x02e8:
            r0 = r29
            int r0 = r0.height
            r47 = r0
            r48 = -2
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x040e
            r0 = r29
            boolean r0 = r0.verticalDimensionFixed
            r47 = r0
            if (r47 == 0) goto L_0x040e
            r0 = r29
            int r0 = r0.height
            r47 = r0
            r0 = r54
            r1 = r17
            r2 = r47
            int r19 = getChildMeasureSpec(r0, r1, r2)
        L_0x030e:
            r0 = r46
            r1 = r19
            r9.measure(r0, r1)
            r0 = r52
            android.support.constraint.solver.Metrics r0 = r0.mMetrics
            r47 = r0
            if (r47 == 0) goto L_0x0333
            r0 = r52
            android.support.constraint.solver.Metrics r0 = r0.mMetrics
            r47 = r0
            r0 = r47
            long r0 = r0.additionalMeasures
            r48 = r0
            r50 = 1
            long r48 = r48 + r50
            r0 = r48
            r2 = r47
            r2.additionalMeasures = r0
        L_0x0333:
            int r4 = r4 + 1
            int r22 = r9.getMeasuredWidth()
            int r21 = r9.getMeasuredHeight()
            int r47 = r42.getWidth()
            r0 = r22
            r1 = r47
            if (r0 == r1) goto L_0x0385
            r0 = r42
            r1 = r22
            r0.setWidth(r1)
            if (r26 == 0) goto L_0x035b
            android.support.constraint.solver.widgets.ResolutionDimension r47 = r42.getResolutionWidth()
            r0 = r47
            r1 = r22
            r0.resolve(r1)
        L_0x035b:
            if (r12 == 0) goto L_0x0383
            int r47 = r42.getRight()
            r0 = r47
            r1 = r24
            if (r0 <= r1) goto L_0x0383
            int r47 = r42.getRight()
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r48 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.RIGHT
            r0 = r42
            r1 = r48
            android.support.constraint.solver.widgets.ConstraintAnchor r48 = r0.getAnchor(r1)
            int r48 = r48.getMargin()
            int r41 = r47 + r48
            r0 = r24
            r1 = r41
            int r24 = java.lang.Math.max(r0, r1)
        L_0x0383:
            r25 = 1
        L_0x0385:
            int r47 = r42.getHeight()
            r0 = r21
            r1 = r47
            if (r0 == r1) goto L_0x03cb
            r0 = r42
            r1 = r21
            r0.setHeight(r1)
            if (r26 == 0) goto L_0x03a3
            android.support.constraint.solver.widgets.ResolutionDimension r47 = r42.getResolutionHeight()
            r0 = r47
            r1 = r21
            r0.resolve(r1)
        L_0x03a3:
            if (r11 == 0) goto L_0x03c9
            int r47 = r42.getBottom()
            r0 = r47
            r1 = r23
            if (r0 <= r1) goto L_0x03c9
            int r47 = r42.getBottom()
            android.support.constraint.solver.widgets.ConstraintAnchor$Type r48 = android.support.constraint.solver.widgets.ConstraintAnchor.Type.BOTTOM
            r0 = r42
            r1 = r48
            android.support.constraint.solver.widgets.ConstraintAnchor r48 = r0.getAnchor(r1)
            int r48 = r48.getMargin()
            int r15 = r47 + r48
            r0 = r23
            int r23 = java.lang.Math.max(r0, r15)
        L_0x03c9:
            r25 = 1
        L_0x03cb:
            r0 = r29
            boolean r0 = r0.needsBaseline
            r47 = r0
            if (r47 == 0) goto L_0x03ec
            int r8 = r9.getBaseline()
            r47 = -1
            r0 = r47
            if (r8 == r0) goto L_0x03ec
            int r47 = r42.getBaselineDistance()
            r0 = r47
            if (r8 == r0) goto L_0x03ec
            r0 = r42
            r0.setBaselineDistance(r8)
            r25 = 1
        L_0x03ec:
            int r47 = android.os.Build.VERSION.SDK_INT
            r48 = 11
            r0 = r47
            r1 = r48
            if (r0 < r1) goto L_0x025b
            int r47 = r9.getMeasuredState()
            r0 = r47
            int r10 = combineMeasuredStates(r10, r0)
            goto L_0x025b
        L_0x0402:
            int r47 = r42.getWidth()
            r48 = 1073741824(0x40000000, float:2.0)
            int r46 = android.view.View.MeasureSpec.makeMeasureSpec(r47, r48)
            goto L_0x02e8
        L_0x040e:
            int r47 = r42.getHeight()
            r48 = 1073741824(0x40000000, float:2.0)
            int r19 = android.view.View.MeasureSpec.makeMeasureSpec(r47, r48)
            goto L_0x030e
        L_0x041a:
            if (r25 == 0) goto L_0x0495
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r37
            r0.setWidth(r1)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r36
            r0.setHeight(r1)
            if (r26 == 0) goto L_0x0441
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r47.solveGraph()
        L_0x0441:
            java.lang.String r47 = "2nd pass"
            r0 = r52
            r1 = r47
            r0.solveLinearSystem(r1)
            r25 = 0
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getWidth()
            r0 = r47
            r1 = r24
            if (r0 >= r1) goto L_0x046b
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r24
            r0.setWidth(r1)
            r25 = 1
        L_0x046b:
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getHeight()
            r0 = r47
            r1 = r23
            if (r0 >= r1) goto L_0x048a
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            r0 = r47
            r1 = r23
            r0.setHeight(r1)
            r25 = 1
        L_0x048a:
            if (r25 == 0) goto L_0x0495
            java.lang.String r47 = "3rd pass"
            r0 = r52
            r1 = r47
            r0.solveLinearSystem(r1)
        L_0x0495:
            r20 = 0
        L_0x0497:
            r0 = r20
            r1 = r35
            if (r0 >= r1) goto L_0x0510
            r0 = r52
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r0 = r0.mVariableDimensionsWidgets
            r47 = r0
            r0 = r47
            r1 = r20
            java.lang.Object r42 = r0.get(r1)
            android.support.constraint.solver.widgets.ConstraintWidget r42 = (android.support.constraint.solver.widgets.ConstraintWidget) r42
            java.lang.Object r9 = r42.getCompanionWidget()
            android.view.View r9 = (android.view.View) r9
            if (r9 != 0) goto L_0x04b8
        L_0x04b5:
            int r20 = r20 + 1
            goto L_0x0497
        L_0x04b8:
            int r47 = r9.getMeasuredWidth()
            int r48 = r42.getWidth()
            r0 = r47
            r1 = r48
            if (r0 != r1) goto L_0x04d4
            int r47 = r9.getMeasuredHeight()
            int r48 = r42.getHeight()
            r0 = r47
            r1 = r48
            if (r0 == r1) goto L_0x04b5
        L_0x04d4:
            int r47 = r42.getWidth()
            r48 = 1073741824(0x40000000, float:2.0)
            int r46 = android.view.View.MeasureSpec.makeMeasureSpec(r47, r48)
            int r47 = r42.getHeight()
            r48 = 1073741824(0x40000000, float:2.0)
            int r19 = android.view.View.MeasureSpec.makeMeasureSpec(r47, r48)
            r0 = r46
            r1 = r19
            r9.measure(r0, r1)
            r0 = r52
            android.support.constraint.solver.Metrics r0 = r0.mMetrics
            r47 = r0
            if (r47 == 0) goto L_0x050d
            r0 = r52
            android.support.constraint.solver.Metrics r0 = r0.mMetrics
            r47 = r0
            r0 = r47
            long r0 = r0.additionalMeasures
            r48 = r0
            r50 = 1
            long r48 = r48 + r50
            r0 = r48
            r2 = r47
            r2.additionalMeasures = r0
        L_0x050d:
            int r5 = r5 + 1
            goto L_0x04b5
        L_0x0510:
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getWidth()
            int r7 = r47 + r44
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            int r47 = r47.getHeight()
            int r6 = r47 + r17
            int r47 = android.os.Build.VERSION.SDK_INT
            r48 = 11
            r0 = r47
            r1 = r48
            if (r0 < r1) goto L_0x059e
            r0 = r53
            int r31 = resolveSizeAndState(r7, r0, r10)
            int r47 = r10 << 16
            r0 = r54
            r1 = r47
            int r30 = resolveSizeAndState(r6, r0, r1)
            r47 = 16777215(0xffffff, float:2.3509886E-38)
            r31 = r31 & r47
            r47 = 16777215(0xffffff, float:2.3509886E-38)
            r30 = r30 & r47
            r0 = r52
            int r0 = r0.mMaxWidth
            r47 = r0
            r0 = r47
            r1 = r31
            int r31 = java.lang.Math.min(r0, r1)
            r0 = r52
            int r0 = r0.mMaxHeight
            r47 = r0
            r0 = r47
            r1 = r30
            int r30 = java.lang.Math.min(r0, r1)
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            boolean r47 = r47.isWidthMeasuredTooSmall()
            if (r47 == 0) goto L_0x0578
            r47 = 16777216(0x1000000, float:2.3509887E-38)
            r31 = r31 | r47
        L_0x0578:
            r0 = r52
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = r0.mLayoutWidget
            r47 = r0
            boolean r47 = r47.isHeightMeasuredTooSmall()
            if (r47 == 0) goto L_0x0588
            r47 = 16777216(0x1000000, float:2.3509887E-38)
            r30 = r30 | r47
        L_0x0588:
            r0 = r52
            r1 = r31
            r2 = r30
            r0.setMeasuredDimension(r1, r2)
            r0 = r31
            r1 = r52
            r1.mLastMeasureWidth = r0
            r0 = r30
            r1 = r52
            r1.mLastMeasureHeight = r0
        L_0x059d:
            return
        L_0x059e:
            r0 = r52
            r0.setMeasuredDimension(r7, r6)
            r0 = r52
            r0.mLastMeasureWidth = r7
            r0 = r52
            r0.mLastMeasureHeight = r6
            goto L_0x059d
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.ConstraintLayout.onMeasure(int, int):void");
    }

    private void setSelfDimensionBehaviour(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        ConstraintWidget.DimensionBehaviour widthBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        ConstraintWidget.DimensionBehaviour heightBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        int desiredWidth = 0;
        int desiredHeight = 0;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        switch (widthMode) {
            case Integer.MIN_VALUE:
                widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                desiredWidth = widthSize;
                break;
            case 0:
                widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                break;
            case 1073741824:
                desiredWidth = Math.min(this.mMaxWidth, widthSize) - widthPadding;
                break;
        }
        switch (heightMode) {
            case Integer.MIN_VALUE:
                heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                desiredHeight = heightSize;
                break;
            case 0:
                heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                break;
            case 1073741824:
                desiredHeight = Math.min(this.mMaxHeight, heightSize) - heightPadding;
                break;
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(widthBehaviour);
        this.mLayoutWidget.setWidth(desiredWidth);
        this.mLayoutWidget.setVerticalDimensionBehaviour(heightBehaviour);
        this.mLayoutWidget.setHeight(desiredHeight);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    /* access modifiers changed from: protected */
    public void solveLinearSystem(String reason) {
        this.mLayoutWidget.layout();
        if (this.mMetrics != null) {
            this.mMetrics.resolutions++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View content;
        int widgetsCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            ConstraintWidget widget = params.widget;
            if ((child.getVisibility() != 8 || params.isGuideline || params.isHelper || isInEditMode) && !params.isInPlaceholder) {
                int l = widget.getDrawX();
                int t = widget.getDrawY();
                int r = l + widget.getWidth();
                int b = t + widget.getHeight();
                child.layout(l, t, r, b);
                if ((child instanceof Placeholder) && (content = ((Placeholder) child).getContent()) != null) {
                    content.setVisibility(0);
                    content.layout(l, t, r, b);
                }
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                this.mConstraintHelpers.get(i2).updatePostLayout(this);
            }
        }
    }

    public void setOptimizationLevel(int level) {
        this.mLayoutWidget.setOptimizationLevel(level);
    }

    public int getOptimizationLevel() {
        return this.mLayoutWidget.getOptimizationLevel();
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet set) {
        this.mConstraintSet = set;
    }

    public View getViewById(int id) {
        return this.mChildrenByIds.get(id);
    }

    public void dispatchDraw(Canvas canvas) {
        Object tag;
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int count = getChildCount();
            float cw = (float) getWidth();
            float ch = (float) getHeight();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || (tag = child.getTag()) == null || !(tag instanceof String))) {
                    String[] split = ((String) tag).split(",");
                    if (split.length == 4) {
                        int x = Integer.parseInt(split[0]);
                        int y = Integer.parseInt(split[1]);
                        int w = Integer.parseInt(split[2]);
                        int x2 = (int) ((((float) x) / 1080.0f) * cw);
                        int y2 = (int) ((((float) y) / 1920.0f) * ch);
                        int w2 = (int) ((((float) w) / 1080.0f) * cw);
                        int h = (int) ((((float) Integer.parseInt(split[3])) / 1920.0f) * ch);
                        Paint paint = new Paint();
                        paint.setColor(-65536);
                        canvas.drawLine((float) x2, (float) y2, (float) (x2 + w2), (float) y2, paint);
                        Canvas canvas2 = canvas;
                        canvas2.drawLine((float) (x2 + w2), (float) y2, (float) (x2 + w2), (float) (y2 + h), paint);
                        Canvas canvas3 = canvas;
                        canvas3.drawLine((float) (x2 + w2), (float) (y2 + h), (float) x2, (float) (y2 + h), paint);
                        canvas.drawLine((float) x2, (float) (y2 + h), (float) x2, (float) y2, paint);
                        paint.setColor(-16711936);
                        Canvas canvas4 = canvas;
                        canvas4.drawLine((float) x2, (float) y2, (float) (x2 + w2), (float) (y2 + h), paint);
                        canvas.drawLine((float) x2, (float) (y2 + h), (float) (x2 + w2), (float) y2, paint);
                    }
                }
            }
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int BASELINE = 5;
        public static final int BOTTOM = 4;
        public static final int CHAIN_PACKED = 2;
        public static final int CHAIN_SPREAD = 0;
        public static final int CHAIN_SPREAD_INSIDE = 1;
        public static final int END = 7;
        public static final int HORIZONTAL = 0;
        public static final int LEFT = 1;
        public static final int MATCH_CONSTRAINT = 0;
        public static final int MATCH_CONSTRAINT_PERCENT = 2;
        public static final int MATCH_CONSTRAINT_SPREAD = 0;
        public static final int MATCH_CONSTRAINT_WRAP = 1;
        public static final int PARENT_ID = 0;
        public static final int RIGHT = 2;
        public static final int START = 6;
        public static final int TOP = 3;
        public static final int UNSET = -1;
        public static final int VERTICAL = 1;
        public int baselineToBaseline = -1;
        public int bottomToBottom = -1;
        public int bottomToTop = -1;
        public float circleAngle = 0.0f;
        public int circleConstraint = -1;
        public int circleRadius = 0;
        public boolean constrainedHeight = false;
        public boolean constrainedWidth = false;
        public String dimensionRatio = null;
        int dimensionRatioSide = 1;
        float dimensionRatioValue = 0.0f;
        public int editorAbsoluteX = -1;
        public int editorAbsoluteY = -1;
        public int endToEnd = -1;
        public int endToStart = -1;
        public int goneBottomMargin = -1;
        public int goneEndMargin = -1;
        public int goneLeftMargin = -1;
        public int goneRightMargin = -1;
        public int goneStartMargin = -1;
        public int goneTopMargin = -1;
        public int guideBegin = -1;
        public int guideEnd = -1;
        public float guidePercent = -1.0f;
        public boolean helped = false;
        public float horizontalBias = 0.5f;
        public int horizontalChainStyle = 0;
        boolean horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
        public float horizontalWeight = 0.0f;
        boolean isGuideline = false;
        boolean isHelper = false;
        boolean isInPlaceholder = false;
        public int leftToLeft = -1;
        public int leftToRight = -1;
        public int matchConstraintDefaultHeight = 0;
        public int matchConstraintDefaultWidth = 0;
        public int matchConstraintMaxHeight = 0;
        public int matchConstraintMaxWidth = 0;
        public int matchConstraintMinHeight = 0;
        public int matchConstraintMinWidth = 0;
        public float matchConstraintPercentHeight = 1.0f;
        public float matchConstraintPercentWidth = 1.0f;
        boolean needsBaseline = false;
        public int orientation = -1;
        int resolveGoneLeftMargin = -1;
        int resolveGoneRightMargin = -1;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias = 0.5f;
        int resolvedLeftToLeft = -1;
        int resolvedLeftToRight = -1;
        int resolvedRightToLeft = -1;
        int resolvedRightToRight = -1;
        public int rightToLeft = -1;
        public int rightToRight = -1;
        public int startToEnd = -1;
        public int startToStart = -1;
        public int topToBottom = -1;
        public int topToTop = -1;
        public float verticalBias = 0.5f;
        public int verticalChainStyle = 0;
        boolean verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
        public float verticalWeight = 0.0f;
        ConstraintWidget widget = new ConstraintWidget();

        public void reset() {
            if (this.widget != null) {
                this.widget.reset();
            }
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.guideBegin = source.guideBegin;
            this.guideEnd = source.guideEnd;
            this.guidePercent = source.guidePercent;
            this.leftToLeft = source.leftToLeft;
            this.leftToRight = source.leftToRight;
            this.rightToLeft = source.rightToLeft;
            this.rightToRight = source.rightToRight;
            this.topToTop = source.topToTop;
            this.topToBottom = source.topToBottom;
            this.bottomToTop = source.bottomToTop;
            this.bottomToBottom = source.bottomToBottom;
            this.baselineToBaseline = source.baselineToBaseline;
            this.circleConstraint = source.circleConstraint;
            this.circleRadius = source.circleRadius;
            this.circleAngle = source.circleAngle;
            this.startToEnd = source.startToEnd;
            this.startToStart = source.startToStart;
            this.endToStart = source.endToStart;
            this.endToEnd = source.endToEnd;
            this.goneLeftMargin = source.goneLeftMargin;
            this.goneTopMargin = source.goneTopMargin;
            this.goneRightMargin = source.goneRightMargin;
            this.goneBottomMargin = source.goneBottomMargin;
            this.goneStartMargin = source.goneStartMargin;
            this.goneEndMargin = source.goneEndMargin;
            this.horizontalBias = source.horizontalBias;
            this.verticalBias = source.verticalBias;
            this.dimensionRatio = source.dimensionRatio;
            this.dimensionRatioValue = source.dimensionRatioValue;
            this.dimensionRatioSide = source.dimensionRatioSide;
            this.horizontalWeight = source.horizontalWeight;
            this.verticalWeight = source.verticalWeight;
            this.horizontalChainStyle = source.horizontalChainStyle;
            this.verticalChainStyle = source.verticalChainStyle;
            this.constrainedWidth = source.constrainedWidth;
            this.constrainedHeight = source.constrainedHeight;
            this.matchConstraintDefaultWidth = source.matchConstraintDefaultWidth;
            this.matchConstraintDefaultHeight = source.matchConstraintDefaultHeight;
            this.matchConstraintMinWidth = source.matchConstraintMinWidth;
            this.matchConstraintMaxWidth = source.matchConstraintMaxWidth;
            this.matchConstraintMinHeight = source.matchConstraintMinHeight;
            this.matchConstraintMaxHeight = source.matchConstraintMaxHeight;
            this.matchConstraintPercentWidth = source.matchConstraintPercentWidth;
            this.matchConstraintPercentHeight = source.matchConstraintPercentHeight;
            this.editorAbsoluteX = source.editorAbsoluteX;
            this.editorAbsoluteY = source.editorAbsoluteY;
            this.orientation = source.orientation;
            this.horizontalDimensionFixed = source.horizontalDimensionFixed;
            this.verticalDimensionFixed = source.verticalDimensionFixed;
            this.needsBaseline = source.needsBaseline;
            this.isGuideline = source.isGuideline;
            this.resolvedLeftToLeft = source.resolvedLeftToLeft;
            this.resolvedLeftToRight = source.resolvedLeftToRight;
            this.resolvedRightToLeft = source.resolvedRightToLeft;
            this.resolvedRightToRight = source.resolvedRightToRight;
            this.resolveGoneLeftMargin = source.resolveGoneLeftMargin;
            this.resolveGoneRightMargin = source.resolveGoneRightMargin;
            this.resolvedHorizontalBias = source.resolvedHorizontalBias;
            this.widget = source.widget;
        }

        private static class Table {
            public static final int ANDROID_ORIENTATION = 1;
            public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
            public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
            public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
            public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
            public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
            public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
            public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
            public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
            public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
            public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
            public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
            public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
            public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
            public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
            public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
            public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
            public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
            public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
            public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
            public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
            public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
            public static final int LAYOUT_GONE_MARGIN_END = 26;
            public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
            public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
            public static final int LAYOUT_GONE_MARGIN_START = 25;
            public static final int LAYOUT_GONE_MARGIN_TOP = 22;
            public static final int UNUSED = 0;
            public static final SparseIntArray map = new SparseIntArray();

            private Table() {
            }

            static {
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
            }
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int commaIndex;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (Table.map.get(attr)) {
                    case 1:
                        this.orientation = a.getInt(attr, this.orientation);
                        break;
                    case 2:
                        this.circleConstraint = a.getResourceId(attr, this.circleConstraint);
                        if (this.circleConstraint != -1) {
                            break;
                        } else {
                            this.circleConstraint = a.getInt(attr, -1);
                            break;
                        }
                    case 3:
                        this.circleRadius = a.getDimensionPixelSize(attr, this.circleRadius);
                        break;
                    case 4:
                        this.circleAngle = a.getFloat(attr, this.circleAngle) % 360.0f;
                        if (this.circleAngle >= 0.0f) {
                            break;
                        } else {
                            this.circleAngle = (360.0f - this.circleAngle) % 360.0f;
                            break;
                        }
                    case 5:
                        this.guideBegin = a.getDimensionPixelOffset(attr, this.guideBegin);
                        break;
                    case 6:
                        this.guideEnd = a.getDimensionPixelOffset(attr, this.guideEnd);
                        break;
                    case 7:
                        this.guidePercent = a.getFloat(attr, this.guidePercent);
                        break;
                    case 8:
                        this.leftToLeft = a.getResourceId(attr, this.leftToLeft);
                        if (this.leftToLeft != -1) {
                            break;
                        } else {
                            this.leftToLeft = a.getInt(attr, -1);
                            break;
                        }
                    case 9:
                        this.leftToRight = a.getResourceId(attr, this.leftToRight);
                        if (this.leftToRight != -1) {
                            break;
                        } else {
                            this.leftToRight = a.getInt(attr, -1);
                            break;
                        }
                    case 10:
                        this.rightToLeft = a.getResourceId(attr, this.rightToLeft);
                        if (this.rightToLeft != -1) {
                            break;
                        } else {
                            this.rightToLeft = a.getInt(attr, -1);
                            break;
                        }
                    case 11:
                        this.rightToRight = a.getResourceId(attr, this.rightToRight);
                        if (this.rightToRight != -1) {
                            break;
                        } else {
                            this.rightToRight = a.getInt(attr, -1);
                            break;
                        }
                    case 12:
                        this.topToTop = a.getResourceId(attr, this.topToTop);
                        if (this.topToTop != -1) {
                            break;
                        } else {
                            this.topToTop = a.getInt(attr, -1);
                            break;
                        }
                    case 13:
                        this.topToBottom = a.getResourceId(attr, this.topToBottom);
                        if (this.topToBottom != -1) {
                            break;
                        } else {
                            this.topToBottom = a.getInt(attr, -1);
                            break;
                        }
                    case 14:
                        this.bottomToTop = a.getResourceId(attr, this.bottomToTop);
                        if (this.bottomToTop != -1) {
                            break;
                        } else {
                            this.bottomToTop = a.getInt(attr, -1);
                            break;
                        }
                    case 15:
                        this.bottomToBottom = a.getResourceId(attr, this.bottomToBottom);
                        if (this.bottomToBottom != -1) {
                            break;
                        } else {
                            this.bottomToBottom = a.getInt(attr, -1);
                            break;
                        }
                    case 16:
                        this.baselineToBaseline = a.getResourceId(attr, this.baselineToBaseline);
                        if (this.baselineToBaseline != -1) {
                            break;
                        } else {
                            this.baselineToBaseline = a.getInt(attr, -1);
                            break;
                        }
                    case 17:
                        this.startToEnd = a.getResourceId(attr, this.startToEnd);
                        if (this.startToEnd != -1) {
                            break;
                        } else {
                            this.startToEnd = a.getInt(attr, -1);
                            break;
                        }
                    case 18:
                        this.startToStart = a.getResourceId(attr, this.startToStart);
                        if (this.startToStart != -1) {
                            break;
                        } else {
                            this.startToStart = a.getInt(attr, -1);
                            break;
                        }
                    case 19:
                        this.endToStart = a.getResourceId(attr, this.endToStart);
                        if (this.endToStart != -1) {
                            break;
                        } else {
                            this.endToStart = a.getInt(attr, -1);
                            break;
                        }
                    case 20:
                        this.endToEnd = a.getResourceId(attr, this.endToEnd);
                        if (this.endToEnd != -1) {
                            break;
                        } else {
                            this.endToEnd = a.getInt(attr, -1);
                            break;
                        }
                    case 21:
                        this.goneLeftMargin = a.getDimensionPixelSize(attr, this.goneLeftMargin);
                        break;
                    case 22:
                        this.goneTopMargin = a.getDimensionPixelSize(attr, this.goneTopMargin);
                        break;
                    case 23:
                        this.goneRightMargin = a.getDimensionPixelSize(attr, this.goneRightMargin);
                        break;
                    case 24:
                        this.goneBottomMargin = a.getDimensionPixelSize(attr, this.goneBottomMargin);
                        break;
                    case 25:
                        this.goneStartMargin = a.getDimensionPixelSize(attr, this.goneStartMargin);
                        break;
                    case 26:
                        this.goneEndMargin = a.getDimensionPixelSize(attr, this.goneEndMargin);
                        break;
                    case 27:
                        this.constrainedWidth = a.getBoolean(attr, this.constrainedWidth);
                        break;
                    case 28:
                        this.constrainedHeight = a.getBoolean(attr, this.constrainedHeight);
                        break;
                    case 29:
                        this.horizontalBias = a.getFloat(attr, this.horizontalBias);
                        break;
                    case 30:
                        this.verticalBias = a.getFloat(attr, this.verticalBias);
                        break;
                    case 31:
                        this.matchConstraintDefaultWidth = a.getInt(attr, 0);
                        if (this.matchConstraintDefaultWidth != 1) {
                            break;
                        } else {
                            Log.e(ConstraintLayout.TAG, "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead.");
                            break;
                        }
                    case 32:
                        this.matchConstraintDefaultHeight = a.getInt(attr, 0);
                        if (this.matchConstraintDefaultHeight != 1) {
                            break;
                        } else {
                            Log.e(ConstraintLayout.TAG, "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead.");
                            break;
                        }
                    case 33:
                        try {
                            this.matchConstraintMinWidth = a.getDimensionPixelSize(attr, this.matchConstraintMinWidth);
                            break;
                        } catch (Exception e) {
                            if (a.getInt(attr, this.matchConstraintMinWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinWidth = -2;
                                break;
                            }
                        }
                    case 34:
                        try {
                            this.matchConstraintMaxWidth = a.getDimensionPixelSize(attr, this.matchConstraintMaxWidth);
                            break;
                        } catch (Exception e2) {
                            if (a.getInt(attr, this.matchConstraintMaxWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxWidth = -2;
                                break;
                            }
                        }
                    case 35:
                        this.matchConstraintPercentWidth = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentWidth));
                        break;
                    case 36:
                        try {
                            this.matchConstraintMinHeight = a.getDimensionPixelSize(attr, this.matchConstraintMinHeight);
                            break;
                        } catch (Exception e3) {
                            if (a.getInt(attr, this.matchConstraintMinHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinHeight = -2;
                                break;
                            }
                        }
                    case 37:
                        try {
                            this.matchConstraintMaxHeight = a.getDimensionPixelSize(attr, this.matchConstraintMaxHeight);
                            break;
                        } catch (Exception e4) {
                            if (a.getInt(attr, this.matchConstraintMaxHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxHeight = -2;
                                break;
                            }
                        }
                    case 38:
                        this.matchConstraintPercentHeight = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentHeight));
                        break;
                    case 44:
                        this.dimensionRatio = a.getString(attr);
                        this.dimensionRatioValue = Float.NaN;
                        this.dimensionRatioSide = -1;
                        if (this.dimensionRatio == null) {
                            break;
                        } else {
                            int len = this.dimensionRatio.length();
                            int commaIndex2 = this.dimensionRatio.indexOf(44);
                            if (commaIndex2 <= 0 || commaIndex2 >= len - 1) {
                                commaIndex = 0;
                            } else {
                                String dimension = this.dimensionRatio.substring(0, commaIndex2);
                                if (dimension.equalsIgnoreCase("W")) {
                                    this.dimensionRatioSide = 0;
                                } else if (dimension.equalsIgnoreCase("H")) {
                                    this.dimensionRatioSide = 1;
                                }
                                commaIndex = commaIndex2 + 1;
                            }
                            int colonIndex = this.dimensionRatio.indexOf(58);
                            if (colonIndex >= 0 && colonIndex < len - 1) {
                                String nominator = this.dimensionRatio.substring(commaIndex, colonIndex);
                                String denominator = this.dimensionRatio.substring(colonIndex + 1);
                                if (nominator.length() > 0 && denominator.length() > 0) {
                                    try {
                                        float nominatorValue = Float.parseFloat(nominator);
                                        float denominatorValue = Float.parseFloat(denominator);
                                        if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                                            if (this.dimensionRatioSide != 1) {
                                                this.dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                                                break;
                                            } else {
                                                this.dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                                                break;
                                            }
                                        }
                                    } catch (NumberFormatException e5) {
                                        break;
                                    }
                                }
                            } else {
                                String r = this.dimensionRatio.substring(commaIndex);
                                if (r.length() <= 0) {
                                    break;
                                } else {
                                    try {
                                        this.dimensionRatioValue = Float.parseFloat(r);
                                        break;
                                    } catch (NumberFormatException e6) {
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 45:
                        this.horizontalWeight = a.getFloat(attr, 0.0f);
                        break;
                    case 46:
                        this.verticalWeight = a.getFloat(attr, 0.0f);
                        break;
                    case 47:
                        this.horizontalChainStyle = a.getInt(attr, 0);
                        break;
                    case 48:
                        this.verticalChainStyle = a.getInt(attr, 0);
                        break;
                    case 49:
                        this.editorAbsoluteX = a.getDimensionPixelOffset(attr, this.editorAbsoluteX);
                        break;
                    case 50:
                        this.editorAbsoluteY = a.getDimensionPixelOffset(attr, this.editorAbsoluteY);
                        break;
                }
            }
            a.recycle();
            validate();
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            if (this.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                this.matchConstraintDefaultWidth = 1;
            }
            if (this.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                this.matchConstraintDefaultHeight = 1;
            }
            if (this.width == 0 || this.width == -1) {
                this.horizontalDimensionFixed = false;
                if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    this.width = -2;
                    this.constrainedWidth = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.height == 0 || this.height == -1) {
                this.verticalDimensionFixed = false;
                if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    this.height = -2;
                    this.constrainedHeight = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                if (!(this.widget instanceof Guideline)) {
                    this.widget = new Guideline();
                }
                ((Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        @TargetApi(17)
        public void resolveLayoutDirection(int layoutDirection) {
            int preLeftMargin = this.leftMargin;
            int preRightMargin = this.rightMargin;
            super.resolveLayoutDirection(layoutDirection);
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            this.resolvedGuideBegin = this.guideBegin;
            this.resolvedGuideEnd = this.guideEnd;
            this.resolvedGuidePercent = this.guidePercent;
            if (1 == getLayoutDirection()) {
                boolean startEndDefined = false;
                if (this.startToEnd != -1) {
                    this.resolvedRightToLeft = this.startToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                } else if (this.startToStart != -1) {
                    this.resolvedRightToRight = this.startToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToStart != -1) {
                    this.resolvedLeftToRight = this.endToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToEnd != -1) {
                    this.resolvedLeftToLeft = this.endToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                if (startEndDefined) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
                if (this.isGuideline && this.orientation == 1) {
                    if (this.guidePercent != -1.0f) {
                        this.resolvedGuidePercent = 1.0f - this.guidePercent;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuideEnd = -1;
                    } else if (this.guideBegin != -1) {
                        this.resolvedGuideEnd = this.guideBegin;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuidePercent = -1.0f;
                    } else if (this.guideEnd != -1) {
                        this.resolvedGuideBegin = this.guideEnd;
                        this.resolvedGuideEnd = -1;
                        this.resolvedGuidePercent = -1.0f;
                    }
                }
            } else {
                if (this.startToEnd != -1) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != -1) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != -1) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != -1) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1 && this.startToStart == -1 && this.startToEnd == -1) {
                if (this.rightToLeft != -1) {
                    this.resolvedRightToLeft = this.rightToLeft;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                } else if (this.rightToRight != -1) {
                    this.resolvedRightToRight = this.rightToRight;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                }
                if (this.leftToLeft != -1) {
                    this.resolvedLeftToLeft = this.leftToLeft;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                } else if (this.leftToRight != -1) {
                    this.resolvedLeftToRight = this.leftToRight;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                }
            }
        }
    }

    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }
}

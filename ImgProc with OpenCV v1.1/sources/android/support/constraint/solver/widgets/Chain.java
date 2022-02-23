package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget;

class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem system, int orientation) {
        int offset;
        int chainsSize;
        ConstraintWidget[] chainsArray;
        if (orientation == 0) {
            offset = 0;
            chainsSize = constraintWidgetContainer.mHorizontalChainsSize;
            chainsArray = constraintWidgetContainer.mHorizontalChainsArray;
        } else {
            offset = 2;
            chainsSize = constraintWidgetContainer.mVerticalChainsSize;
            chainsArray = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i = 0; i < chainsSize; i++) {
            ConstraintWidget first = chainsArray[i];
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, system, orientation, offset, first)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            }
        }
    }

    static void applyChainConstraints(ConstraintWidgetContainer container, LinearSystem system, int orientation, int offset, ConstraintWidget first) {
        boolean isChainSpreadInside;
        boolean isChainSpread;
        boolean isChainPacked;
        ConstraintAnchor beginNextAnchor;
        SolverVariable beginNextTarget;
        ConstraintAnchor beginNextAnchor2;
        SolverVariable beginNextTarget2;
        float bias;
        int currentDimensionDefault;
        int nextDimensionDefault;
        ConstraintWidget next;
        ConstraintWidget next2;
        ConstraintWidget widget = first;
        ConstraintWidget firstVisibleWidget = null;
        ConstraintWidget lastVisibleWidget = null;
        boolean done = false;
        int numMatchConstraints = 0;
        float totalWeights = 0.0f;
        ConstraintWidget firstMatchConstraintsWidget = null;
        ConstraintWidget previousMatchConstraintsWidget = null;
        boolean isWrapContent = container.mListDimensionBehaviors[orientation] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        ConstraintWidget head = first;
        if (orientation == 0 && container.isRtl()) {
            while (!done) {
                ConstraintAnchor nextAnchor = widget.mListAnchors[offset + 1].mTarget;
                if (nextAnchor != null) {
                    next2 = nextAnchor.mOwner;
                    if (next2.mListAnchors[offset].mTarget == null || next2.mListAnchors[offset].mTarget.mOwner != widget) {
                        next2 = null;
                    }
                } else {
                    next2 = null;
                }
                if (next2 != null) {
                    widget = next2;
                } else {
                    done = true;
                }
            }
            head = widget;
            widget = first;
            done = false;
        }
        if (orientation == 0) {
            isChainSpread = head.mHorizontalChainStyle == 0;
            isChainSpreadInside = head.mHorizontalChainStyle == 1;
            isChainPacked = head.mHorizontalChainStyle == 2;
        } else {
            isChainSpread = head.mVerticalChainStyle == 0;
            isChainSpreadInside = head.mVerticalChainStyle == 1;
            isChainPacked = head.mVerticalChainStyle == 2;
        }
        while (!done) {
            widget.mListNextVisibleWidget[orientation] = null;
            if (widget.getVisibility() != 8) {
                if (lastVisibleWidget != null) {
                    lastVisibleWidget.mListNextVisibleWidget[orientation] = widget;
                }
                if (firstVisibleWidget == null) {
                    firstVisibleWidget = widget;
                }
                lastVisibleWidget = widget;
            }
            ConstraintAnchor begin = widget.mListAnchors[offset];
            int strength = 1;
            int margin = begin.getMargin();
            if (!(begin.mTarget == null || widget == first || widget.getVisibility() == 8)) {
                margin += begin.mTarget.getMargin();
            }
            if (!(!isChainPacked || widget == first || widget == firstVisibleWidget)) {
                strength = 6;
            }
            if (widget == firstVisibleWidget) {
                system.addGreaterThan(begin.mSolverVariable, begin.mTarget.mSolverVariable, margin, 5);
            } else {
                system.addGreaterThan(begin.mSolverVariable, begin.mTarget.mSolverVariable, margin, 6);
            }
            system.addEquality(begin.mSolverVariable, begin.mTarget.mSolverVariable, margin, strength);
            widget.mListNextMatchConstraintsWidget[orientation] = null;
            if (widget.getVisibility() != 8 && widget.mListDimensionBehaviors[orientation] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                numMatchConstraints++;
                totalWeights += widget.mWeight[orientation];
                if (firstMatchConstraintsWidget == null) {
                    firstMatchConstraintsWidget = widget;
                } else {
                    previousMatchConstraintsWidget.mListNextMatchConstraintsWidget[orientation] = widget;
                }
                previousMatchConstraintsWidget = widget;
                if (isWrapContent) {
                    system.addGreaterThan(widget.mListAnchors[offset + 1].mSolverVariable, widget.mListAnchors[offset].mSolverVariable, 0, 6);
                }
            }
            if (isWrapContent) {
                system.addGreaterThan(widget.mListAnchors[offset].mSolverVariable, container.mListAnchors[offset].mSolverVariable, 0, 6);
            }
            ConstraintAnchor nextAnchor2 = widget.mListAnchors[offset + 1].mTarget;
            if (nextAnchor2 != null) {
                next = nextAnchor2.mOwner;
                if (next.mListAnchors[offset].mTarget == null || next.mListAnchors[offset].mTarget.mOwner != widget) {
                    next = 0;
                }
            } else {
                next = null;
            }
            if (next != null) {
                widget = next;
            } else {
                done = true;
            }
        }
        ConstraintWidget last = widget;
        if (!(lastVisibleWidget == null || last.mListAnchors[offset + 1].mTarget == null)) {
            ConstraintAnchor end = lastVisibleWidget.mListAnchors[offset + 1];
            system.addLowerThan(end.mSolverVariable, last.mListAnchors[offset + 1].mTarget.mSolverVariable, -end.getMargin(), 5);
        }
        if (isWrapContent) {
            system.addGreaterThan(container.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].getMargin(), 6);
        }
        if (numMatchConstraints > 0) {
            ConstraintWidget widget2 = firstMatchConstraintsWidget;
            while (widget2 != null) {
                ConstraintWidget next3 = widget2.mListNextMatchConstraintsWidget[orientation];
                if (next3 != null) {
                    float currentWeight = widget2.mWeight[orientation];
                    float nextWeight = next3.mWeight[orientation];
                    SolverVariable begin2 = widget2.mListAnchors[offset].mSolverVariable;
                    SolverVariable end2 = widget2.mListAnchors[offset + 1].mSolverVariable;
                    SolverVariable nextBegin = next3.mListAnchors[offset].mSolverVariable;
                    SolverVariable nextEnd = next3.mListAnchors[offset + 1].mSolverVariable;
                    if (orientation == 0) {
                        currentDimensionDefault = widget2.mMatchConstraintDefaultWidth;
                        nextDimensionDefault = next3.mMatchConstraintDefaultWidth;
                    } else {
                        currentDimensionDefault = widget2.mMatchConstraintDefaultHeight;
                        nextDimensionDefault = next3.mMatchConstraintDefaultHeight;
                    }
                    if ((currentDimensionDefault == 0 || currentDimensionDefault == 3) && (nextDimensionDefault == 0 || nextDimensionDefault == 3)) {
                        ArrayRow row = system.createRow();
                        row.createRowEqualMatchDimensions(currentWeight, totalWeights, nextWeight, begin2, end2, nextBegin, nextEnd);
                        system.addConstraint(row);
                    }
                }
                widget2 = next3;
            }
        }
        if (firstVisibleWidget != null && (firstVisibleWidget == lastVisibleWidget || isChainPacked)) {
            ConstraintAnchor begin3 = first.mListAnchors[offset];
            ConstraintAnchor end3 = last.mListAnchors[offset + 1];
            SolverVariable beginTarget = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
            SolverVariable endTarget = last.mListAnchors[offset + 1].mTarget != null ? last.mListAnchors[offset + 1].mTarget.mSolverVariable : null;
            if (firstVisibleWidget == lastVisibleWidget) {
                begin3 = firstVisibleWidget.mListAnchors[offset];
                end3 = firstVisibleWidget.mListAnchors[offset + 1];
            }
            if (!(beginTarget == null || endTarget == null)) {
                if (orientation == 0) {
                    bias = head.mHorizontalBiasPercent;
                } else {
                    bias = head.mVerticalBiasPercent;
                }
                int beginMargin = begin3.getMargin();
                if (lastVisibleWidget == null) {
                    lastVisibleWidget = last;
                }
                system.addCentering(begin3.mSolverVariable, beginTarget, beginMargin, bias, endTarget, end3.mSolverVariable, lastVisibleWidget.mListAnchors[offset + 1].getMargin(), 5);
            }
        } else if (isChainSpread && firstVisibleWidget != null) {
            ConstraintWidget widget3 = firstVisibleWidget;
            ConstraintWidget previousVisibleWidget = firstVisibleWidget;
            while (widget3 != null) {
                ConstraintWidget next4 = widget3.mListNextVisibleWidget[orientation];
                if (next4 != null || widget3 == lastVisibleWidget) {
                    ConstraintAnchor beginAnchor = widget3.mListAnchors[offset];
                    SolverVariable begin4 = beginAnchor.mSolverVariable;
                    SolverVariable beginTarget2 = beginAnchor.mTarget != null ? beginAnchor.mTarget.mSolverVariable : null;
                    if (previousVisibleWidget != widget3) {
                        beginTarget2 = previousVisibleWidget.mListAnchors[offset + 1].mSolverVariable;
                    } else if (widget3 == firstVisibleWidget && previousVisibleWidget == widget3) {
                        beginTarget2 = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
                    }
                    SolverVariable beginNext = null;
                    int beginMargin2 = beginAnchor.getMargin();
                    int nextMargin = widget3.mListAnchors[offset + 1].getMargin();
                    if (next4 != null) {
                        beginNextAnchor2 = next4.mListAnchors[offset];
                        beginNext = beginNextAnchor2.mSolverVariable;
                        beginNextTarget2 = beginNextAnchor2.mTarget != null ? beginNextAnchor2.mTarget.mSolverVariable : null;
                    } else {
                        beginNextAnchor2 = last.mListAnchors[offset + 1].mTarget;
                        if (beginNextAnchor2 != null) {
                            beginNext = beginNextAnchor2.mSolverVariable;
                        }
                        beginNextTarget2 = widget3.mListAnchors[offset + 1].mSolverVariable;
                    }
                    if (beginNextAnchor2 != null) {
                        nextMargin += beginNextAnchor2.getMargin();
                    }
                    if (previousVisibleWidget != null) {
                        beginMargin2 += previousVisibleWidget.mListAnchors[offset + 1].getMargin();
                    }
                    if (!(begin4 == null || beginTarget2 == null || beginNext == null || beginNextTarget2 == null)) {
                        int margin1 = beginMargin2;
                        if (widget3 == firstVisibleWidget) {
                            margin1 = firstVisibleWidget.mListAnchors[offset].getMargin();
                        }
                        int margin2 = nextMargin;
                        if (widget3 == lastVisibleWidget) {
                            margin2 = lastVisibleWidget.mListAnchors[offset + 1].getMargin();
                        }
                        system.addCentering(begin4, beginTarget2, margin1, 0.5f, beginNext, beginNextTarget2, margin2, 4);
                    }
                }
                previousVisibleWidget = widget3;
                widget3 = next4;
            }
        } else if (isChainSpreadInside && firstVisibleWidget != null) {
            ConstraintWidget widget4 = firstVisibleWidget;
            ConstraintWidget previousVisibleWidget2 = firstVisibleWidget;
            while (widget4 != null) {
                ConstraintWidget next5 = widget4.mListNextVisibleWidget[orientation];
                if (!(widget4 == firstVisibleWidget || widget4 == lastVisibleWidget || next5 == null)) {
                    if (next5 == lastVisibleWidget) {
                        next5 = null;
                    }
                    ConstraintAnchor beginAnchor2 = widget4.mListAnchors[offset];
                    SolverVariable begin5 = beginAnchor2.mSolverVariable;
                    if (beginAnchor2.mTarget != null) {
                        SolverVariable solverVariable = beginAnchor2.mTarget.mSolverVariable;
                    }
                    SolverVariable beginTarget3 = previousVisibleWidget2.mListAnchors[offset + 1].mSolverVariable;
                    SolverVariable beginNext2 = null;
                    int beginMargin3 = beginAnchor2.getMargin();
                    int nextMargin2 = widget4.mListAnchors[offset + 1].getMargin();
                    if (next5 != null) {
                        beginNextAnchor = next5.mListAnchors[offset];
                        beginNext2 = beginNextAnchor.mSolverVariable;
                        beginNextTarget = beginNextAnchor.mTarget != null ? beginNextAnchor.mTarget.mSolverVariable : null;
                    } else {
                        beginNextAnchor = widget4.mListAnchors[offset + 1].mTarget;
                        if (beginNextAnchor != null) {
                            beginNext2 = beginNextAnchor.mSolverVariable;
                        }
                        beginNextTarget = widget4.mListAnchors[offset + 1].mSolverVariable;
                    }
                    if (beginNextAnchor != null) {
                        nextMargin2 += beginNextAnchor.getMargin();
                    }
                    if (previousVisibleWidget2 != null) {
                        beginMargin3 += previousVisibleWidget2.mListAnchors[offset + 1].getMargin();
                    }
                    if (!(begin5 == null || beginTarget3 == null || beginNext2 == null || beginNextTarget == null)) {
                        system.addCentering(begin5, beginTarget3, beginMargin3, 0.5f, beginNext2, beginNextTarget, nextMargin2, 4);
                    }
                }
                previousVisibleWidget2 = widget4;
                widget4 = next5;
            }
            ConstraintAnchor begin6 = firstVisibleWidget.mListAnchors[offset];
            ConstraintAnchor beginTarget4 = first.mListAnchors[offset].mTarget;
            ConstraintAnchor end4 = lastVisibleWidget.mListAnchors[offset + 1];
            ConstraintAnchor endTarget2 = last.mListAnchors[offset + 1].mTarget;
            if (beginTarget4 != null) {
                if (firstVisibleWidget != lastVisibleWidget) {
                    system.addEquality(begin6.mSolverVariable, beginTarget4.mSolverVariable, begin6.getMargin(), 5);
                } else if (endTarget2 != null) {
                    system.addCentering(begin6.mSolverVariable, beginTarget4.mSolverVariable, begin6.getMargin(), 0.5f, end4.mSolverVariable, endTarget2.mSolverVariable, end4.getMargin(), 5);
                }
            }
            if (!(endTarget2 == null || firstVisibleWidget == lastVisibleWidget)) {
                system.addEquality(end4.mSolverVariable, endTarget2.mSolverVariable, -end4.getMargin(), 5);
            }
        }
        if ((isChainSpread || isChainSpreadInside) && firstVisibleWidget != null) {
            ConstraintAnchor begin7 = firstVisibleWidget.mListAnchors[offset];
            ConstraintAnchor end5 = lastVisibleWidget.mListAnchors[offset + 1];
            SolverVariable beginTarget5 = begin7.mTarget != null ? begin7.mTarget.mSolverVariable : null;
            SolverVariable endTarget3 = end5.mTarget != null ? end5.mTarget.mSolverVariable : null;
            if (firstVisibleWidget == lastVisibleWidget) {
                begin7 = firstVisibleWidget.mListAnchors[offset];
                end5 = firstVisibleWidget.mListAnchors[offset + 1];
            }
            if (beginTarget5 != null && endTarget3 != null) {
                int beginMargin4 = begin7.getMargin();
                if (lastVisibleWidget == null) {
                    lastVisibleWidget = last;
                }
                system.addCentering(begin7.mSolverVariable, beginTarget5, beginMargin4, 0.5f, endTarget3, end5.mSolverVariable, lastVisibleWidget.mListAnchors[offset + 1].getMargin(), 5);
            }
        }
    }
}

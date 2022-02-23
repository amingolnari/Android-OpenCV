package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable;
import java.util.Arrays;

public class ArrayVariables {
    private static final boolean DEBUG = false;
    private int ROW_SIZE = 8;
    int currentSize = 0;
    private int[] mArrayIndices = new int[this.ROW_SIZE];
    private boolean[] mArrayValid = new boolean[this.ROW_SIZE];
    private float[] mArrayValues = new float[this.ROW_SIZE];
    private final Cache mCache;
    private final ArrayRow mRow;

    ArrayVariables(ArrayRow arrayRow, Cache cache) {
        this.mRow = arrayRow;
        this.mCache = cache;
    }

    public final void put(SolverVariable variable, float value) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayIndices[i] == variable.id) {
                this.mArrayValues[i] = value;
                if (value == 0.0f) {
                    this.mArrayValid[i] = false;
                    variable.removeFromRow(this.mRow);
                    return;
                }
                return;
            }
        }
        if (this.currentSize >= this.mArrayIndices.length) {
            this.ROW_SIZE *= 2;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
        }
        this.mArrayIndices[this.currentSize] = variable.id;
        this.mArrayValues[this.currentSize] = value;
        this.mArrayValid[this.currentSize] = true;
        if (value == 0.0f) {
            variable.removeFromRow(this.mRow);
            this.mArrayValid[this.currentSize] = false;
        }
        variable.usageInRowCount++;
        variable.addToRow(this.mRow);
        this.currentSize++;
    }

    /* access modifiers changed from: package-private */
    public final void add(SolverVariable variable, float value, boolean removeFromDefinition) {
        if (value != 0.0f) {
            for (int i = 0; i < this.currentSize; i++) {
                if (this.mArrayIndices[i] == variable.id) {
                    float[] fArr = this.mArrayValues;
                    fArr[i] = fArr[i] + value;
                    return;
                }
            }
            if (this.currentSize >= this.mArrayIndices.length) {
                this.ROW_SIZE *= 2;
                this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
                this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
                this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
            }
            this.mArrayIndices[this.currentSize] = variable.id;
            float[] fArr2 = this.mArrayValues;
            int i2 = this.currentSize;
            fArr2[i2] = fArr2[i2] + value;
            this.mArrayValid[this.currentSize] = true;
            variable.usageInRowCount++;
            variable.addToRow(this.mRow);
            if (this.mArrayValues[this.currentSize] == 0.0f) {
                variable.usageInRowCount--;
                variable.removeFromRow(this.mRow);
                this.mArrayValid[this.currentSize] = false;
            }
            this.currentSize++;
        }
    }

    public final float remove(SolverVariable variable, boolean removeFromDefinition) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayIndices[i] == variable.id) {
                float value = this.mArrayValues[i];
                this.mArrayValues[i] = 0.0f;
                this.mArrayValid[i] = false;
                if (!removeFromDefinition) {
                    return value;
                }
                variable.usageInRowCount--;
                variable.removeFromRow(this.mRow);
                return value;
            }
        }
        return 0.0f;
    }

    public final void clear() {
        for (int i = 0; i < this.currentSize; i++) {
            SolverVariable variable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
            if (variable != null) {
                variable.removeFromRow(this.mRow);
            }
        }
        this.currentSize = 0;
    }

    /* access modifiers changed from: package-private */
    public final boolean containsKey(SolverVariable variable) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayIndices[i] == variable.id) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean hasAtLeastOnePositiveVariable() {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayValues[i] > 0.0f) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void invert() {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                float[] fArr = this.mArrayValues;
                fArr[i] = fArr[i] * -1.0f;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void divideByAmount(float amount) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                float[] fArr = this.mArrayValues;
                fArr[i] = fArr[i] / amount;
            }
        }
    }

    private boolean isNew(SolverVariable variable, LinearSystem system) {
        return variable.mClientEquationsCount <= 1;
    }

    /* access modifiers changed from: package-private */
    public SolverVariable chooseSubject(LinearSystem system) {
        SolverVariable restrictedCandidate = null;
        SolverVariable unrestrictedCandidate = null;
        float unrestrictedCandidateAmount = 0.0f;
        float restrictedCandidateAmount = 0.0f;
        boolean unrestrictedCandidateIsNew = false;
        boolean restrictedCandidateIsNew = false;
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                float amount = this.mArrayValues[i];
                SolverVariable variable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (amount < 0.0f) {
                    if (amount > (-0.001f)) {
                        this.mArrayValues[i] = 0.0f;
                        amount = 0.0f;
                        this.mArrayValid[i] = false;
                        variable.removeFromRow(this.mRow);
                    }
                } else if (amount < 0.001f) {
                    this.mArrayValues[i] = 0.0f;
                    amount = 0.0f;
                    this.mArrayValid[i] = false;
                    variable.removeFromRow(this.mRow);
                }
                if (amount != 0.0f) {
                    if (variable.mType == SolverVariable.Type.UNRESTRICTED) {
                        if (unrestrictedCandidate == null) {
                            unrestrictedCandidate = variable;
                            unrestrictedCandidateAmount = amount;
                            unrestrictedCandidateIsNew = isNew(variable, system);
                        } else if (unrestrictedCandidateAmount > amount) {
                            unrestrictedCandidate = variable;
                            unrestrictedCandidateAmount = amount;
                            unrestrictedCandidateIsNew = isNew(variable, system);
                        } else if (!unrestrictedCandidateIsNew && isNew(variable, system)) {
                            unrestrictedCandidate = variable;
                            unrestrictedCandidateAmount = amount;
                            unrestrictedCandidateIsNew = true;
                        }
                    } else if (unrestrictedCandidate == null && amount < 0.0f) {
                        if (restrictedCandidate == null) {
                            restrictedCandidate = variable;
                            restrictedCandidateAmount = amount;
                            restrictedCandidateIsNew = isNew(variable, system);
                        } else if (restrictedCandidateAmount > amount) {
                            restrictedCandidate = variable;
                            restrictedCandidateAmount = amount;
                            restrictedCandidateIsNew = isNew(variable, system);
                        } else if (!restrictedCandidateIsNew && isNew(variable, system)) {
                            restrictedCandidate = variable;
                            restrictedCandidateAmount = amount;
                            restrictedCandidateIsNew = true;
                        }
                    }
                }
            }
        }
        return unrestrictedCandidate != null ? unrestrictedCandidate : restrictedCandidate;
    }

    /* access modifiers changed from: package-private */
    public final void updateFromRow(ArrayRow self, ArrayRow definition, boolean removeFromDefinition) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayIndices[i] == definition.variable.id) {
                float value = this.mArrayValues[i];
                if (value != 0.0f) {
                    this.mArrayValues[i] = 0.0f;
                    this.mArrayValid[i] = false;
                    if (removeFromDefinition) {
                        definition.variable.removeFromRow(this.mRow);
                    }
                    ArrayVariables definitionVariables = (ArrayVariables) definition.variables;
                    for (int j = 0; j < definitionVariables.currentSize; j++) {
                        add(this.mCache.mIndexedVariables[definitionVariables.mArrayIndices[j]], definitionVariables.mArrayValues[j] * value, removeFromDefinition);
                    }
                    self.constantValue += definition.constantValue * value;
                    if (removeFromDefinition) {
                        definition.variable.removeFromRow(self);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateFromSystem(ArrayRow self, ArrayRow[] rows) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                SolverVariable variable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (variable.definitionId != -1) {
                    float value = this.mArrayValues[i];
                    this.mArrayValues[i] = 0.0f;
                    this.mArrayValid[i] = false;
                    variable.removeFromRow(this.mRow);
                    ArrayRow definition = rows[variable.definitionId];
                    if (!definition.isSimpleDefinition) {
                        ArrayVariables definitionVariables = (ArrayVariables) definition.variables;
                        for (int j = 0; j < definitionVariables.currentSize; j++) {
                            add(this.mCache.mIndexedVariables[definitionVariables.mArrayIndices[j]], definitionVariables.mArrayValues[j] * value, true);
                        }
                    }
                    self.constantValue += definition.constantValue * value;
                    definition.variable.removeFromRow(self);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public SolverVariable getPivotCandidate() {
        SolverVariable pivot = null;
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayValues[i] < 0.0f) {
                SolverVariable v = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (pivot == null || pivot.strength < v.strength) {
                    pivot = v;
                }
            }
        }
        return pivot;
    }

    /* access modifiers changed from: package-private */
    public SolverVariable getPivotCandidate(boolean[] avoid, SolverVariable exclude) {
        SolverVariable pivot = null;
        float value = 0.0f;
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayValues[i] < 0.0f) {
                SolverVariable v = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if ((avoid == null || !avoid[v.id]) && v != exclude && (v.mType == SolverVariable.Type.SLACK || v.mType == SolverVariable.Type.ERROR)) {
                    float currentValue = this.mArrayValues[i];
                    if (currentValue < value) {
                        value = currentValue;
                        pivot = v;
                    }
                }
            }
        }
        return pivot;
    }

    /* access modifiers changed from: package-private */
    public final SolverVariable getVariable(int index) {
        if (index < this.currentSize) {
            return this.mCache.mIndexedVariables[this.mArrayIndices[index]];
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final float getVariableValue(int index) {
        if (index < this.currentSize) {
            return this.mArrayValues[index];
        }
        return 0.0f;
    }

    public final float get(SolverVariable v) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayIndices[i] == v.id) {
                return this.mArrayValues[i];
            }
        }
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public int sizeInBytes() {
        return 0 + (this.mArrayIndices.length * 4 * 3) + 36;
    }

    public void display() {
        SolverVariable v;
        int count = this.currentSize;
        System.out.print("{ ");
        for (int i = 0; i < count; i++) {
            if (this.mArrayValid[i] && (v = getVariable(i)) != null) {
                System.out.print(v + " = " + getVariableValue(i) + " ");
            }
        }
        System.out.println(" }");
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i] && this.mArrayValues[i] != 0.0f) {
                result = ((result + " -> ") + this.mArrayValues[i] + " : ") + this.mCache.mIndexedVariables[this.mArrayIndices[i]];
            }
        }
        return result;
    }
}

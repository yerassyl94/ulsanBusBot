package android.support.v4.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;

public abstract class AbsSavedState implements Parcelable {
    public static final Creator<AbsSavedState> CREATOR = new C01192();
    public static final AbsSavedState EMPTY_STATE = new C04191();
    private final Parcelable mSuperState;

    /* renamed from: android.support.v4.view.AbsSavedState$2 */
    static class C01192 implements ClassLoaderCreator<AbsSavedState> {
        C01192() {
        }

        public AbsSavedState createFromParcel(Parcel in, ClassLoader loader) {
            if (in.readParcelable(loader) == null) {
                return AbsSavedState.EMPTY_STATE;
            }
            throw new IllegalStateException("superState must be null");
        }

        public AbsSavedState createFromParcel(Parcel in) {
            return createFromParcel(in, null);
        }

        public AbsSavedState[] newArray(int size) {
            return new AbsSavedState[size];
        }
    }

    /* renamed from: android.support.v4.view.AbsSavedState$1 */
    static class C04191 extends AbsSavedState {
        C04191() {
            super();
        }
    }

    private AbsSavedState() {
        this.mSuperState = null;
    }

    protected AbsSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        if (superState == EMPTY_STATE) {
            superState = null;
        }
        this.mSuperState = superState;
    }

    protected AbsSavedState(Parcel source) {
        this(source, null);
    }

    protected AbsSavedState(Parcel source, ClassLoader loader) {
        Parcelable superState = source.readParcelable(loader);
        if (superState == null) {
            superState = EMPTY_STATE;
        }
        this.mSuperState = superState;
    }

    public final Parcelable getSuperState() {
        return this.mSuperState;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, flags);
    }
}

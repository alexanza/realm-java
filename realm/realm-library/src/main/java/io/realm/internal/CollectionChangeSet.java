package io.realm.internal;


import io.realm.OrderedCollectionChange;

public class CollectionChangeSet implements OrderedCollectionChange, NativeObject {

    private static long finalizerPtr = nativeGetFinalizerPtr();
    private final long nativePtr;

    private long[] deletionIndices;
    private long[] insertionIndices;
    private long[] changeIndices;
    private Range[] deletionRanges;
    private Range[] insertionRanges;
    private Range[] changeRanges;

    public long[] getDeletions() {
        if (deletionIndices == null) {
           deletionIndices = rangesToIndexArray(getDeletionRanges());
        }
        return deletionIndices.length == 0 ? null : deletionIndices;
    }

    public long[] getInsertions() {
        if (insertionIndices == null) {
            insertionIndices = rangesToIndexArray(getInsertionRanges());
        }
        return insertionIndices.length == 0 ? null : insertionIndices;
    }

    public long[] getChanges()  {
        if (changeIndices == null) {
            changeIndices = rangesToIndexArray(getChangeRanges());
        }
        return changeIndices.length == 0 ? null : changeIndices;
    }

    public Range[] getDeletionRanges() {
        if (deletionRanges == null) {
            deletionRanges = longArrayToRangeArray(nativeGetDeletionRanges(nativePtr));
        }

        return deletionRanges.length == 0 ? null : deletionRanges;
    }

    public Range[] getInsertionRanges() {
        if (insertionRanges == null) {
            insertionRanges = longArrayToRangeArray(nativeGetInsertionRanges(nativePtr));
        }

        return insertionRanges.length == 0 ? null : insertionRanges;
    }

    public Range[] getChangeRanges() {
        if (changeRanges == null) {
            changeRanges = longArrayToRangeArray(nativeGetChangeRanges(nativePtr));
        }

        return changeRanges.length == 0 ? null : changeRanges;
    }

    CollectionChangeSet(long nativePtr) {
        this.nativePtr = nativePtr;
        Context.dummyContext.addReference(this);
    }

    @Override
    public long getNativePtr() {
        return nativePtr;
    }

    @Override
    public long getNativeFinalizerPtr() {
        return finalizerPtr;
    }

    private Range[] longArrayToRangeArray(long[] longArray) {
        if (longArray == null) {
            // Returns a size 0 array so we know the JNI gets called.
            return new Range[0];
        }

        Range[] ranges = new Range[longArray.length / 2];
        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = new Range(longArray[i * 2], longArray[i * 2 + 1]);
        }
        return ranges;
    }

    private long[] rangesToIndexArray(Range[] ranges) {
        if (ranges == null || ranges.length == 0) {
            return new long[0];
        }

        long count = 0;
        for (Range range : ranges) {
            count += range.length;
        }
        if (count > Integer.MAX_VALUE) {
            throw new IllegalStateException("There are too many indices in this change set. " +
                    "They cannot fit into an array.");
        }

        long[] indexArray = new long[(int) count];
        int i = 0;
        for (Range range : ranges) {
            for (int j = 0; j < range.length; j++) {
                indexArray[i] = range.startIndex + j;
                i++;
            }
        }
        return indexArray;
    }

    private native static long nativeGetFinalizerPtr();
    private native static long[] nativeGetDeletionRanges(long nativePtr);
    private native static long[] nativeGetInsertionRanges(long nativePtr);
    private native static long[] nativeGetChangeRanges(long nativePtr);
}

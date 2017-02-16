package io.realm;

/**
 * This interface describes the changes made to a collection during the last update.
 */
public interface OrderedCollectionChange {
    long[] getDeletions();
    long[] getInsertions();
    long[] getChanges();
    Range[] getDeletionRanges();
    Range[] getInsertionRanges();
    Range[] getChangeRanges();

    class Range {
        public final long startIndex;
        public final long length;

        public Range(long startIndex, long length) {
            this.startIndex = startIndex;
            this.length = length;
        }
    }
}

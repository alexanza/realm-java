package io.realm;

import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.entities.AllTypes;
import io.realm.rule.RunInLooperThread;
import io.realm.rule.RunTestInLooperThread;
import io.realm.rule.TestRealmConfigurationFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class OrderedCollectionChangeTests {

    @Rule
    public final TestRealmConfigurationFactory configFactory = new TestRealmConfigurationFactory();
    @Rule
    public final RunInLooperThread looperThread = new RunInLooperThread();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private void populateData(Realm realm, int testSize) {
        realm.beginTransaction();
        for (int i = 0; i < testSize; i++) {
            realm.createObject(AllTypes.class).setColumnLong(i);
        }
        realm.commitTransaction();
    }

    @Test
    @RunTestInLooperThread
    public void deletionRanges() {
        final AtomicBoolean listenerCalled = new AtomicBoolean(false);
        Realm realm = looperThread.realm;
        populateData(realm, 10);
        RealmResults<AllTypes> results = realm.where(AllTypes.class).findAllSorted(AllTypes.FIELD_LONG);
        results.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<AllTypes>>() {
            @Override
            public void onChange(RealmResults<AllTypes> collection, OrderedCollectionChange changes) {
                listenerCalled.set(true);
                OrderedCollectionChange.Range[] ranges = changes.getDeletionRanges();
                assertEquals(3, ranges.length);

                assertEquals(0, ranges[0].startIndex);
                assertEquals(1, ranges[0].length);

                assertEquals(2, ranges[1].startIndex);
                assertEquals(3, ranges[1].length);

                assertEquals(8, ranges[2].startIndex);
                assertEquals(2, ranges[2].length);
            }
        });

        realm.beginTransaction();
        // 0
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 0).findFirst().deleteFromRealm();

        // 2-4
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 2).findFirst().deleteFromRealm();
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 3).findFirst().deleteFromRealm();
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 4).findFirst().deleteFromRealm();

        // 8-9
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 8).findFirst().deleteFromRealm();
        realm.where(AllTypes.class).equalTo(AllTypes.FIELD_LONG, 9).findFirst().deleteFromRealm();
        realm.commitTransaction();

        realm.beginTransaction();
        realm.cancelTransaction();

        assertTrue(listenerCalled.get());
        looperThread.testComplete();
    }
}

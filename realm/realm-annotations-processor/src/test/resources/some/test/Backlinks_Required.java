package some.test;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.Required;

public class Backlinks_Required extends RealmObject {

    private Backlinks_Required child;

    @Required
    @LinkingObjects("child")
    private RealmResults<Backlinks_Required> selectedFieldParents;
}

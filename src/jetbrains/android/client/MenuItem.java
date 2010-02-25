package jetbrains.android.client;

/**
 * Created by IntelliJ IDEA.
 * User: Zeckson
 * Date: Feb 26, 2010
 * Time: 12:50:48 AM
 * To change this template use File | Settings | File Templates.
 */
public enum MenuItem {
    SETTINGS(R.string.options, 0, 1),
    REPORTS(R.string.reports, 1, 2),
    QUIT(R.string.quit, 2, 3);

    public int titleId;
    public int order;
    public int id;

    MenuItem(int titleId, int order, int id) {
        this.titleId = titleId;
        this.order = order;
        this.id = id;
    }
}

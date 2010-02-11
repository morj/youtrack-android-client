package jetbrains.android.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Toshiba
 * Date: 10.02.2010
 * Time: 23:03:22
 * To change this template use File | Settings | File Templates.
 */
public class Issue {
    private String id;
    private String summary;
    private String description;

    public Issue(String id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public static List<Issue> getSampleData() {
        ArrayList<Issue> list = new ArrayList<Issue>();
        list.add(new Issue("JT-4062", "User can change other users' avatars", "At least I could open another user's profile, then clicked “click to change” link under his avatar, selected “no avatar” and then clicked OK. His avatar disappeared.\n" +
                "\n" +
                "This is a strong security problem."));
        list.add(new Issue("JT-3977", "Need an ability to edit search string in saved search", ""));
        list.add(new Issue("JT-3485", "Saved Searches are promted be saved", ""));
        list.add(new Issue("JT-3901", "Use \"not\" and \"!\" in addition to \"-\" for exclude", "Also, since you use \"guessable\" synonyms for a lot of other things perhaps it would help other users if you added \"not\" and \"!\" as synonyms? (\"not\" is also much more consistent with \"or\" - anyone knowing \"or\" is bound to try \"not\")"));
        list.add(new Issue("JT-4016", "New Issue\" Button Does not Work when Server Is Up", "When \"server is up\" banner appears, the New Issue button and most other buttons/actions stop working -- just silently, no action, no error message, no nothing. The URI changes back and forth on each click, though, but without any other effect."));
        list.add(new Issue("JT-4017", "Incorrect completion list order for \"priority\" command", ""));
        list.add(new Issue("JT-4031", "Search completion for \"Sort by\" value does not work in some cases", "I have some search request with \"sort by: priority\" somewhere in the middle. I go to 'priority' word, erase it and press Ctrl-Space. It does not work. It only works if I place the caret at the start of the next word (but in this case it erases it!)."));
        return list;
    }
}

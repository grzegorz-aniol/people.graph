package people.analitics;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class AbstractSentence {

    private LinkedList<WordText> content = new LinkedList<WordText>();
    private ArrayList<WordText> tabContent = new ArrayList<WordText>();

    public ListIterator<WordText> getIterator() {
        return content.listIterator();
    }

    public void add(WordText word) {
        content.add(word);
        tabContent.add(word);
    }

    public WordText get(int index) {
        if (index >= tabContent.size()) {
            return null;
        }
        return tabContent.get(index);
    }

    public int size() {
        return content.size();
    }

    public String getSeparatorBefore() {
        if (tabContent.size() > 0) {
            return tabContent.get(0).getSeparatorBefore();
        }
        return null;
    }

    public String getSeparatorAfter() {
        if (tabContent.size() > 0) {
            return tabContent.get(tabContent.size()-1).getSeparatorAfter();
        }
        return null;
    }

    public String getJoined() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < tabContent.size(); ++i) {
            WordText w = tabContent.get(i);
            sb.append(w.text);
            if (i+1 < tabContent.size()) {
                if (StringUtils.isNotBlank(w.getSeparatorAfter())) {
                    sb.append(w.getSeparatorAfter());
                } else {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
}

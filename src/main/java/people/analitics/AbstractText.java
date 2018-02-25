package people.analitics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractText {

    protected String text;

    protected int totalStartPos;

    protected String separatorBefore;

    protected String separatorAfter;

    public boolean startsWithUpperCase() {
        return (text != null && text.length() > 0 && Character.isUpperCase(text.charAt(0)));
    }

    @Override
    public String toString() {
        return text;
    }

    public int getLength() {
        return (text != null ? text.length() : 0);
    }

}



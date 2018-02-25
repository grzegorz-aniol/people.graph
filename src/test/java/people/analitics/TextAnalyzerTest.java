package people.analitics;

import lombok.val;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TextAnalyzerTest {

    @Test
    public void testOneSimpleSentence() {
        final String TEXT =
            "Dodawanie nowych pozycji do grona artykułów wyróżnionych następuje po procesie weryfikacji.";
        final String P1 =
            "Dodawanie nowych pozycji do grona artykułów wyróżnionych następuje po procesie weryfikacji";

        val ta = new TextAnalyzer(TEXT);
        val sentence = ta.getNextSentence();
        assertThat(sentence, notNullValue());
        val parts = sentence.getParts();
        assertThat(parts, notNullValue());
        val part1 = parts.get(0);
        assertThat(part1.getJoined(), equalTo(P1));
    }

    @Test
    public void tesComplexSentence() {
        final String TEXT =
            "Aby artykuł otrzymał wyróżnienie, " +
            "musi spełnić szereg wymagań, " +
            "przedstawionych w poradniku Porównanie wyróżnień artykułów.";
        final String P1 =
            "Aby artykuł otrzymał wyróżnienie";
        final String P2 =
            "musi spełnić szereg wymagań";
        final String P3 =
            "przedstawionych w poradniku Porównanie wyróżnień artykułów";

        val ta = new TextAnalyzer(TEXT);
        val sentence = ta.getNextSentence();
        assertThat(sentence, notNullValue());

        val parts = sentence.getParts();
        assertThat(parts, notNullValue());
        assertThat(parts.size(), equalTo(3));

        assertThat(parts.get(0).getJoined(), equalTo(P1));
        assertThat(parts.get(1).getJoined(), equalTo(P2));
        assertThat(parts.get(2).getJoined(), equalTo(P3));
    }

}

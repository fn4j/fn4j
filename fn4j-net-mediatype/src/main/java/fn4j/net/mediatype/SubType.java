package fn4j.net.mediatype;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;

import java.util.Objects;

public record SubType(Seq<Element> elements) {
    public static final SubType ALL = subType(Element.ALL);
    public static final SubType JSON = subType(Element.JSON);
    public static final SubType PLAIN = subType(Element.PLAIN);

    public static SubType subType(Element firstElement,
                                  Element... remainingElements) {
        return new SubType(Array.of(firstElement).appendAll(Array.of(remainingElements)));
    }

    public static SubType subType(String value) {
        return parse(Objects.requireNonNull(value));
    }

    public String encode() {
        return elements.head().value;
    }

    private static SubType parse(String value) {
        return subType(new Element(value));
    }

    public static record Element(String value) {
        public static final String ALL_VALUE = "*";
        public static final Element ALL = new Element(ALL_VALUE);

        public static final String JSON_VALUE = "json";
        public static final Element JSON = new Element(JSON_VALUE);

        public static final String PLAIN_VALUE = "plain";
        public static final Element PLAIN = new Element(PLAIN_VALUE);
    }
}
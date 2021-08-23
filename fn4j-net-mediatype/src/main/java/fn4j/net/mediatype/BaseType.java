package fn4j.net.mediatype;

import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.function.Function.identity;

public final class BaseType {
    public static final String ALL_VALUE = "*";
    public static final BaseType ALL = new BaseType(ALL_VALUE);

    public static final String APPLICATION_VALUE = "application";
    public static final BaseType APPLICATION = new BaseType(APPLICATION_VALUE);

    public static final String TEXT_VALUE = "text";
    public static final BaseType TEXT = new BaseType(TEXT_VALUE);

    private static final Pattern VALID_VALUES = Pattern.compile("([a-z0-9][a-z0-9!#$&\\-^_]{0,126}|\\*)");

    private final String value;

    private BaseType(String value) {
        this.value = value;
    }

    public static Either<RuntimeException, BaseType> baseType(String value) {
        return preConformChecks(value).toEither(() -> conform(value))
                                      .swap()
                                      .flatMap(conformed -> postConformChecks(conformed).toEither(conformed)
                                                                                        .swap())
                                      .map(BaseType::new);
    }

    public static BaseType baseTypeOrThrow(String value) {
        return baseType(value).getOrElseThrow(identity());
    }

    public String encode() {
        return value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BaseType) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "BaseType[value=" + value + ']';
    }

    private static String conform(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private static Option<RuntimeException> preConformChecks(String value) {
        if (value == null) {
            return Option.of(new IllegalArgumentException("value is null"));
        }

        return Option.none();
    }

    private static Option<RuntimeException> postConformChecks(String value) {
        if (value.isEmpty()) {
            return Option.of(new IllegalArgumentException("value is empty"));
        }

        if (!VALID_VALUES.matcher(value).matches()) {
            return Option.of(new IllegalArgumentException("value contains invalid characters"));
        }

        return Option.none();
    }
}
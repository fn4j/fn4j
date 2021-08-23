package fn4j.net.mediatype;

import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.control.Option;

import java.util.Objects;

import static fn4j.net.mediatype.BaseType.baseTypeOrThrow;

public record MediaType(BaseType baseType,
                        SubType subType,
                        Option<Suffix> maybeSuffix,
                        LinkedHashMap<ParameterName, ParameterValue> parameters) {

    public static final String ALL_VALUE = "*/*";
    public static final MediaType ALL = mediaType(BaseType.ALL, SubType.ALL);

    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MediaType APPLICATION_JSON = mediaType(BaseType.APPLICATION, SubType.JSON);

    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final MediaType TEXT_PLAIN = mediaType(BaseType.TEXT, SubType.PLAIN);

    public static MediaType mediaType(BaseType baseType,
                                      SubType subType) {
        return new MediaType(baseType,
                             subType,
                             Option.none(),
                             LinkedHashMap.empty());
    }

    public static MediaType mediaType(BaseType baseType,
                                      SubType subType,
                                      Suffix suffix) {
        return new MediaType(baseType,
                             subType,
                             Option.of(suffix),
                             LinkedHashMap.empty());
    }

    public static MediaType mediaType(BaseType baseType,
                                      SubType subType,
                                      LinkedHashMap<ParameterName, ParameterValue> parameters) {
        return new MediaType(baseType,
                             subType,
                             Option.none(),
                             parameters);
    }

    @SafeVarargs
    public static MediaType mediaType(BaseType baseType,
                                      SubType subType,
                                      Tuple2<ParameterName, ParameterValue>... parameters) {
        return mediaType(baseType,
                         subType,
                         LinkedHashMap.ofEntries(parameters));
    }

    public static MediaType mediaType(BaseType baseType,
                                      SubType subType,
                                      Suffix suffix,
                                      LinkedHashMap<ParameterName, ParameterValue> parameters) {
        return new MediaType(baseType,
                             subType,
                             Option.of(suffix),
                             parameters);
    }

    @SafeVarargs
    public static MediaType mediaType(BaseType baseType,
                                      SubType subType,
                                      Suffix suffix,
                                      Tuple2<ParameterName, ParameterValue>... parameters) {
        return mediaType(baseType,
                         subType,
                         suffix,
                         LinkedHashMap.ofEntries(parameters));
    }

    public static MediaType mediaTypeOrThrow(String value) {
        return parse(Objects.requireNonNull(value));
    }

    public String encode() {
        return baseType.encode() + "/" + subType.encode();
    }

    private static MediaType parse(String value) {
        var indexOfSlash = value.indexOf('/');
        if (indexOfSlash < 0) {
            throw new IllegalArgumentException("sub type is missing");
        }

        return mediaType(baseTypeOrThrow(value.substring(0, indexOfSlash)),
                         SubType.subType(value.substring(Math.min(indexOfSlash + 1, value.length()))));
    }
}
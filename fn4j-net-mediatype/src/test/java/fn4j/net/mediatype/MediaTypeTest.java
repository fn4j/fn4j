package fn4j.net.mediatype;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import net.jqwik.api.*;

import static fn4j.net.mediatype.MediaType.*;
import static net.jqwik.api.Arbitraries.strings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class MediaTypeTest {

    @Property
    @Label("should encode and parse again")
    void shouldEncodeAndParseApplicationJson(@ForAll("media types and their values") Tuple2<MediaType, String> mediaTypeAndValue) {
        // given
        MediaType mediaType = mediaTypeAndValue._1();
        String value = mediaTypeAndValue._2();

        // when
        String encodeResult = mediaType.encode();

        // then
        assertThat(encodeResult).isEqualTo(value);

        // when
        MediaType parseResult = mediaTypeOrThrow(encodeResult);

        // then
        assertThat(parseResult).isEqualTo(mediaType);
    }

    @Provide("media types and their values")
    Arbitrary<Tuple2<MediaType, String>> mediaTypesAndTheirValues() {
        return Arbitraries.of(Tuple.of(ALL, ALL_VALUE),
                              Tuple.of(APPLICATION_JSON, APPLICATION_JSON_VALUE),
                              Tuple.of(TEXT_PLAIN, TEXT_PLAIN_VALUE));
    }

    @Property
    @Label("should fail without sub type")
    void shouldFailWithoutSubtype(@ForAll("strings without slashes") String value) {
        // when
        Throwable result = catchThrowable(() -> mediaTypeOrThrow(value));

        // then
        assertThat(result).isExactlyInstanceOf(IllegalArgumentException.class)
                          .hasMessage("sub type is missing");
    }

    @Provide("strings without slashes")
    Arbitrary<String> stringsWithoutSlashes() {
        return strings().excludeChars('/');
    }
}
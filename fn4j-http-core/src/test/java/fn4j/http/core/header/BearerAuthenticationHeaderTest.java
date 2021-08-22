package fn4j.http.core.header;

import fn4j.http.core.header.BearerAuthenticationHeader.Token;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.StringArbitrary;

import static fn4j.http.core.header.BearerAuthenticationHeader.bearerAuthentication;
import static fn4j.http.core.header.HeaderName.AUTHENTICATION;
import static fn4j.http.core.header.HeaderName.AUTHENTICATION_VALUE;
import static fn4j.http.core.header.Headers.headers;
import static net.jqwik.api.Arbitraries.strings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class BearerAuthenticationHeaderTest {
    @Property
    @Label("should have tuple")
    void shouldHaveTuple(@ForAll("tokens") Token token) {
        // given
        BearerAuthenticationHeader header = new BearerAuthenticationHeader(token);

        // when
        Tuple2<HeaderName, HeaderValue> result = header.tuple();

        // then
        assertThat(result._1().value()).isEqualTo(AUTHENTICATION_VALUE);
        assertThat(result._2().value()).isEqualTo("Bearer " + token.value());
    }

    @Property
    @Label("should read header value")
    void shouldReadHeaderValue(@ForAll("tokens") Token token) {
        // given
        HeaderValue headerValue = new HeaderValue("Bearer " + token.value());
        Headers headers = headers(new RawHeader(AUTHENTICATION, headerValue));

        // when
        Option<BearerAuthenticationHeader> result = headers.getSingle(bearerAuthentication());

        // then
        assertThat(result).hasValueSatisfying(header -> {
            assertThat(header.token()).isEqualTo(token);
        });
    }

    @Property
    @Label("should read header value if invalid bearer authentication")
    void shouldReadHeaderValueIfInvalidBearerAuthentication(@ForAll("invalid bearer authentication") HeaderValue headerValue) {
        // given
        Headers headers = headers(new RawHeader(AUTHENTICATION, headerValue));

        // when
        Option<BearerAuthenticationHeader> result = headers.getSingle(bearerAuthentication());

        // then
        assertThat(result).isEmpty();
    }

    @Provide("tokens")
    Arbitrary<Token> tokens() {
        return headerValues().map(Token::new);
    }

    private StringArbitrary headerValues() {
        return strings().excludeChars('\r', '\n');
    }

    @Provide("invalid bearer authentication")
    Arbitrary<HeaderValue> invalidBearerAuthentication() {
        return Arbitraries.of("",
                              "Bear",
                              "Bea rer <token>",
                              "Bearer<token>",
                              "Bearer").map(HeaderValue::new);
    }
}
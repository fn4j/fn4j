package fn4j.http.core.header;

import fn4j.http.core.header.BasicAuthenticationHeader.Password;
import fn4j.http.core.header.BasicAuthenticationHeader.Username;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.StringArbitrary;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static fn4j.http.core.header.BasicAuthenticationHeader.basicAuthentication;
import static fn4j.http.core.header.HeaderName.AUTHENTICATION;
import static fn4j.http.core.header.HeaderName.AUTHENTICATION_VALUE;
import static fn4j.http.core.header.Headers.headers;
import static net.jqwik.api.Arbitraries.strings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class BasicAuthenticationHeaderTest {
    @Property
    @Label("should have tuple")
    void shouldHaveTuple(@ForAll("usernames") Username username,
                         @ForAll("passwords") Password password) {
        // given
        var header = new BasicAuthenticationHeader(username, password);

        // when
        Tuple2<HeaderName, HeaderValue> result = header.tuple();

        // then
        assertThat(result._1().value()).isEqualTo(AUTHENTICATION_VALUE);
        assertThat(result._2().value()).isEqualTo("Basic " + encodeAsBase64("%s:%s".formatted(username.value(), password.value())));
    }

    @Property
    @Label("should read header value")
    void shouldReadHeaderValue(@ForAll("usernames") Username username,
                               @ForAll("passwords") Password password) {
        // given
        HeaderValue headerValue = new HeaderValue("Basic " + encodeAsBase64("%s:%s".formatted(username.value(), password.value())));
        Headers headers = headers(new RawHeader(AUTHENTICATION, headerValue));

        // when
        Option<BasicAuthenticationHeader> result = headers.getSingle(basicAuthentication());

        // then
        assertThat(result).hasValueSatisfying(header -> {
            assertThat(header.username()).isEqualTo(username);
            assertThat(header.password()).isEqualTo(password);
        });
    }

    @Property
    @Label("should read header value if invalid basic authentication")
    void shouldReadHeaderValue(@ForAll("invalid basic authentication") HeaderValue headerValue) {
        // given
        Headers headers = headers(new RawHeader(AUTHENTICATION, headerValue));

        // when
        Option<BasicAuthenticationHeader> result = headers.getSingle(basicAuthentication());

        // then
        assertThat(result).isEmpty();
    }

    @Provide("usernames")
    Arbitrary<Username> usernames() {
        return headerValues().excludeChars(':').map(Username::new);
    }

    @Provide("passwords")
    Arbitrary<Password> passwords() {
        return headerValues().map(Password::new);
    }

    private StringArbitrary headerValues() {
        return strings().excludeChars('\r', '\n');
    }

    @Provide("invalid basic authentication")
    Arbitrary<HeaderValue> invalidBasicAuthentication() {
        return Arbitraries.of("",
                              "Bas",
                              "Bas ic " + encodeAsBase64("username:password"),
                              "Basic" + encodeAsBase64("username:password"),
                              "Basic " + encodeAsBase64(""),
                              "Basic " + encodeAsBase64("username")).map(HeaderValue::new);
    }

    private String encodeAsBase64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }
}
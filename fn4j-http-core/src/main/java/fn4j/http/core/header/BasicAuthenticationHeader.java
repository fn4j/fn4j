package fn4j.http.core.header;

import io.vavr.control.Option;

import java.util.Base64;

import static fn4j.http.core.header.HeaderName.AUTHENTICATION;
import static java.nio.charset.StandardCharsets.UTF_8;

public record BasicAuthenticationHeader(Username username,
                                        Password password) implements Header {
    @Override
    public HeaderName headerName() {
        return AUTHENTICATION;
    }

    @Override
    public HeaderValue headerValue() {
        return new HeaderValue("Basic " + encodeBase64(username.value() + ':' + password.value()));
    }

    public static HeaderReader<BasicAuthenticationHeader> basicAuthentication() {
        return HeaderReader.of(AUTHENTICATION)
                           .map(HeaderValue::value)
                           .mapOption(value -> Option.when(value.startsWith("Basic "), end(value, 6)))
                           .mapTry(BasicAuthenticationHeader::decodeBase64)
                           .mapOption(value -> {
                               var colonIndex = value.indexOf(':');
                               if (colonIndex < 0) {
                                   return Option.none();
                               }
                               var username = new Username(start(value, colonIndex));
                               var password = new Password(end(value, colonIndex + 1));
                               return Option.of(new BasicAuthenticationHeader(username, password));
                           });
    }

    public static record Username(String value) {
    }

    public static record Password(String value) {
    }

    private static String start(String value,
                                int endIndex) {
        return value.substring(0, Math.min(value.length(), endIndex));
    }

    private static String end(String value,
                              int startIndex) {
        return value.substring(Math.min(value.length(), startIndex));
    }

    private static String encodeBase64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes(UTF_8));
    }

    private static String decodeBase64(String string) {
        return new String(Base64.getDecoder().decode(string), UTF_8);
    }
}
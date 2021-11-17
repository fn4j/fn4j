package fn4j.http.core.header;

import io.vavr.control.Option;

import static fn4j.http.core.header.HeaderName.AUTHENTICATION;

public record BearerAuthenticationHeader(Token token) implements Header {
    @Override
    public HeaderName headerName() {
        return AUTHENTICATION;
    }

    @Override
    public HeaderValue headerValue() {
        return new HeaderValue("Bearer " + token.value());
    }

    public static HeaderReader<BearerAuthenticationHeader> bearerAuthentication() {
        return HeaderReader.of(AUTHENTICATION)
                           .map(HeaderValue::value)
                           .mapOption(value -> Option.when(value.startsWith("Bearer "),
                                                           value.substring(Math.min(value.length(), 7))))
                           .map(Token::new)
                           .map(BearerAuthenticationHeader::new);
    }

    public record Token(String value) {
    }
}
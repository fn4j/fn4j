package fn4j.http.core.header;

import fn4j.net.uri.Uri;

import static fn4j.http.core.header.HeaderName.LOCATION;

public record LocationHeader(Uri uri) implements Header {
    @Override
    public HeaderName headerName() {
        return LOCATION;
    }

    @Override
    public HeaderValue headerValue() {
        return new HeaderValue(uri.encode());
    }

    public static HeaderReader<LocationHeader> location() {
        return HeaderReader.of(LOCATION)
                           .map(HeaderValue::value)
                           .mapTry(Uri::new)
                           .map(LocationHeader::new);
    }
}
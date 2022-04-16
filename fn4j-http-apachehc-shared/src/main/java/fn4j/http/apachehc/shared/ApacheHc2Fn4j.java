package fn4j.http.apachehc.shared;

import fn4j.http.core.*;
import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Uri;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;

import static fn4j.http.core.Body.maybeBody;

public interface ApacheHc2Fn4j {
    static Try<RequestHead> requestHead(HttpRequest httpRequest) {
        return Try.of(httpRequest::getUri).map(Uri::new)
                  .map(uri -> RequestHead.requestHead(new Method(httpRequest.getMethod()),
                                                      uri,
                                                      headers(httpRequest.getHeaders())));
    }

    static Headers headers(Header... headers) {
        return Headers.headers(Stream.of(headers)
                                     .map(header -> Tuple.of(new HeaderName(header.getName()),
                                                             new HeaderValue(header.getValue()))));
    }

    static Response<byte[]> response(Message<HttpResponse, byte[]> message) {
        return responseHead(message.getHead()).toResponse(maybeBody(message.getBody()));
    }

    static ResponseHead responseHead(HttpResponse httpResponse) {
        return ResponseHead.responseHead(status(httpResponse),
                                         headers(httpResponse.getHeaders()));
    }

    static Status status(HttpResponse httpResponse) {
        return new Status(httpResponse.getCode(),
                          httpResponse.getReasonPhrase());
    }
}
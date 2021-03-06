package fn4j.http.server.apachehc;

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
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.apache.hc.core5.http.nio.entity.AsyncEntityProducers.createBinary;

public final class Conversions {
    private Conversions() {
    }

    public static Headers headers(Header... headers) {
        return Headers.headers(Stream.of(headers)
                                     .map(header -> Tuple.of(new HeaderName(header.getName()),
                                                             new HeaderValue(header.getValue()))));
    }

    public static Try<RequestHead> requestHead(HttpRequest httpRequest) {
        return Try.of(httpRequest::getUri).map(Uri::new)
                  .map(uri -> RequestHead.requestHead(new Method(httpRequest.getMethod()),
                                                      uri,
                                                      headers(httpRequest.getHeaders())));
    }

    public static final class ApacheHc {
        private ApacheHc() {
        }

        public static AsyncResponseProducer asyncResponseProducer(Response<byte[]> response,
                                                                  ProtocolVersion protocolVersion) {
            return new BasicResponseProducer(httpResponse(response, protocolVersion),
                                             response.maybeBody().fold(() -> null,
                                                                       ApacheHc::asyncEntityProducer));
        }

        public static HttpResponse httpResponse(ResponseHead responseHead,
                                                ProtocolVersion protocolVersion) {
            var basicHttpResponse = new BasicHttpResponse(responseHead.status().statusCode().value(),
                                                          responseHead.status().reasonPhrase().value());
            basicHttpResponse.setVersion(protocolVersion);
            responseHead.headers()
                        .stream()
                        .forEach(header -> basicHttpResponse.addHeader(header.headerName().value(),
                                                                       header.headerValue().value()));
            return basicHttpResponse;
        }

        public static AsyncEntityProducer asyncEntityProducer(Body<byte[]> body) {
            return createBinary(streamChannel -> {
                try {
                    streamChannel.write(ByteBuffer.wrap(body.value()));
                    streamChannel.endStream();
                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            }, null);
        }
    }
}
package fn4j.http.apachehc.shared;

import fn4j.http.core.*;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.apache.hc.core5.http.nio.entity.AsyncEntityProducers.createBinary;

public interface Fn4j2ApacheHc {
    static AsyncRequestProducer asyncRequestProducer(Request<byte[]> request) {
        return new BasicRequestProducer(httpRequest(request),
                                        request.maybeBody().fold(() -> null,
                                                                 Fn4j2ApacheHc::asyncEntityProducer));
    }

    static AsyncResponseProducer asyncResponseProducer(Response<byte[]> response,
                                                       ProtocolVersion protocolVersion) {
        return new BasicResponseProducer(httpResponse(response, protocolVersion),
                                         response.maybeBody().fold(() -> null,
                                                                   Fn4j2ApacheHc::asyncEntityProducer));
    }

    static HttpRequest httpRequest(RequestHead requestHead) {
        var basicHttpRequest = new BasicHttpRequest(method(requestHead.method()),
                                                    requestHead.uri().asJavaURI());
        requestHead.headers()
                   .forEach((headerName, headerValue) -> {
                       basicHttpRequest.addHeader(headerName.value(),
                                                  headerValue.value());
                   });
        return basicHttpRequest;
    }

    static org.apache.hc.core5.http.Method method(Method method) {
        return org.apache.hc.core5.http.Method.normalizedValueOf(method.value());
    }

    static HttpResponse httpResponse(ResponseHead responseHead,
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

    static AsyncEntityProducer asyncEntityProducer(Body<byte[]> body) {
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

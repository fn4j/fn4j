package fn4j.http.core;

import io.vavr.Tuple;
import io.vavr.control.Option;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.util.Collection;

import static fn4j.http.core.Method.COMMON_METHODS;
import static fn4j.http.core.StatusCode.COMMON_STATUS_CODES;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.RandomDistribution.gaussian;

public final class Fn4jHttpCoreArbitraries {
    private Fn4jHttpCoreArbitraries() {
    }

    public static <B> Arbitrary<Body<B>> bodies(Class<B> valueClass) {
        return Arbitraries.forType(valueClass)
                          .map(Body::new);
    }

    public static Arbitrary<Head> heads() {
        return headers().map(Head::head);
    }

    public static Arbitrary<HeaderName> headerNames() {
        return strings().alpha()
                        .ofMinLength(1)
                        .ofMaxLength(20)
                        .list()
                        .ofMinSize(1)
                        .ofMaxSize(5)
                        .map(elements -> String.join("-", elements))
                        .map(HeaderName::new);
    }

    public static Arbitrary<Headers> headers() {
        return headerNames().list()
                            .ofMinSize(0)
                            .ofMaxSize(50)
                            .withSizeDistribution(gaussian())
                            .flatMapEach((headerNames, headerName) -> headerValues().list()
                                                                                    .ofMinSize(1)
                                                                                    .ofMaxSize(4)
                                                                                    .mapEach((headerValues, headerValue) -> Tuple.of(headerName, headerValue)))
                            .map(lists -> lists.stream()
                                               .flatMap(Collection::stream)
                                               .collect(toList()))
                            .map(Headers::headers);
    }

    public static Arbitrary<HeaderValue> headerValues() {
        return strings().map(HeaderValue::new);
    }

    public static <B> Arbitrary<Message<B>> messages(Class<B> bodyClass) {
        return heads().flatMap(head -> bodies(bodyClass).optional()
                                                        .map(Option::ofOptional)
                                                        .map(head::toMessage));
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<Method> methods() {
        return Arbitraries.oneOf(commonMethods(), uncommonMethods());
    }

    public static Arbitrary<Method> commonMethods() {
        return Arbitraries.of(COMMON_METHODS.toJavaList());
    }

    public static Arbitrary<Method> uncommonMethods() {
        return strings().withCharRange('A', 'Z')
                        .ofMinLength(1)
                        .ofMaxLength(10)
                        .map(Method::new);
    }

    public static <B> Arbitrary<Request<B>> requests(Class<B> bodyClass) {
        return combine(requestHeads(),
                       bodies(bodyClass).optional()
                                        .map(Option::ofOptional))
                .as(RequestHead::toRequest);
    }

    public static Arbitrary<RequestHead> requestHeads() {
        return heads().flatMap(head -> methods().flatMap(method -> requestUris().map(requestUri -> head.toRequestHead(method, requestUri))));
    }

    public static Arbitrary<RequestPath> requestPaths() {
        return requestUris().map(RequestUri::path);
    }

    public static Arbitrary<RequestUri> requestUris() {
        return combine(Arbitraries.of("http", "https", "ftp"),
                       strings().withCharRange('a', 'z')
                                .ofMinLength(3)
                                .ofMaxLength(100),
                       integers().between(1, 65535),
                       strings().withCharRange('a', 'z')
                                .ofMinLength(1)
                                .ofMaxLength(50),
                       maps(strings().withCharRange('a', 'z')
                                     .ofMinLength(1)
                                     .ofMaxLength(10),
                            strings().alpha()
                                     .numeric()
                                     .ofMinLength(1)
                                     .ofMaxLength(50))
                               .ofMinSize(0)
                               .ofMaxSize(5))
                .as((scheme, hostname, port, path, parameters) -> new RequestUri("%s://%s:%d/%s?%s".formatted(scheme,
                                                                                                              hostname,
                                                                                                              port,
                                                                                                              path,
                                                                                                              parameters.entrySet()
                                                                                                                        .stream()
                                                                                                                        .map(queryParameter -> "%s=%s".formatted(queryParameter.getKey(),
                                                                                                                                                                 queryParameter.getValue()))
                                                                                                                        .collect(joining("&")))));
    }

    public static <B> Arbitrary<Response<B>> responses(Class<B> bodyClass) {
        return combine(responseHeads(),
                       bodies(bodyClass).optional()
                                        .map(Option::ofOptional))
                .as(ResponseHead::toResponse);
    }

    public static Arbitrary<ResponseHead> responseHeads() {
        return heads().flatMap(head -> statusCodes().map(head::toResponseHead));
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<StatusCode> statusCodes() {
        return Arbitraries.oneOf(commonStatusCodes(), uncommonStatusCodes());
    }

    public static Arbitrary<StatusCode> commonStatusCodes() {
        return Arbitraries.of(COMMON_STATUS_CODES.toJavaList());
    }

    public static Arbitrary<StatusCode> uncommonStatusCodes() {
        return integers().between(100, 999)
                         .map(StatusCode::new);
    }

}
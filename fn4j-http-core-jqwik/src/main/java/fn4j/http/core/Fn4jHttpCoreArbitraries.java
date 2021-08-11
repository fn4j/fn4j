package fn4j.http.core;

import io.vavr.control.Option;
import net.jqwik.api.Arbitrary;

import java.util.Collection;

import static fn4j.http.core.Method.COMMON_METHODS;
import static fn4j.http.core.Status.COMMON_STATUSES;
import static fn4j.net.uri.Fn4jNetUriArbitraries.uris;
import static java.util.stream.Collectors.toList;
import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.RandomDistribution.gaussian;

public final class Fn4jHttpCoreArbitraries {
    private Fn4jHttpCoreArbitraries() {
    }

    public static <B> Arbitrary<Body<B>> bodies(Class<B> valueClass) {
        return forType(valueClass).map(Body::new);
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
                                                                                    .mapEach((headerValues, headerValue) -> io.vavr.Tuple.of(headerName, headerValue)))
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
        return oneOf(commonMethods(), uncommonMethods());
    }

    public static Arbitrary<Method> commonMethods() {
        return of(COMMON_METHODS.toJavaList());
    }

    public static Arbitrary<Method> uncommonMethods() {
        return strings().withCharRange('A', 'Z')
                        .ofMinLength(1)
                        .ofMaxLength(10)
                        .map(Method::new);
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<ReasonPhrase> reasonPhrases() {
        return oneOf(commonReasonPhrases(), uncommonReasonPhrases());
    }

    public static Arbitrary<ReasonPhrase> commonReasonPhrases() {
        return of(COMMON_STATUSES.toStream()
                                 .map(Status::reasonPhrase)
                                 .toJavaList());
    }

    public static Arbitrary<ReasonPhrase> uncommonReasonPhrases() {
        return strings().withCharRange('A', 'Z')
                        .withChars(' ')
                        .ofMinLength(3)
                        .ofMaxLength(50)
                        .map(ReasonPhrase::new);
    }

    public static <B> Arbitrary<Request<B>> requests(Class<B> bodyClass) {
        return combine(requestHeads(),
                       bodies(bodyClass).optional()
                                        .map(Option::ofOptional))
                .as(RequestHead::toRequest);
    }

    public static Arbitrary<RequestHead> requestHeads() {
        return heads().flatMap(head -> methods().flatMap(method -> uris().map(uri -> head.toRequestHead(method, uri))));
    }

    public static <B> Arbitrary<Response<B>> responses(Class<B> bodyClass) {
        return combine(responseHeads(),
                       bodies(bodyClass).optional()
                                        .map(Option::ofOptional))
                .as(ResponseHead::toResponse);
    }

    public static Arbitrary<ResponseHead> responseHeads() {
        return heads().flatMap(head -> statuses().map(head::toResponseHead));
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<Status> statuses() {
        return oneOf(commonStatuses(), uncommonStatuses());
    }

    public static Arbitrary<Status> commonStatuses() {
        return of(COMMON_STATUSES.toJavaList());
    }

    public static Arbitrary<Status> uncommonStatuses() {
        return combine(uncommonStatusCodes(), uncommonReasonPhrases()).as(Status::new);
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<StatusCode> statusCodes() {
        return oneOf(commonStatusCodes(), uncommonStatusCodes());
    }

    public static Arbitrary<StatusCode> commonStatusCodes() {
        return of(COMMON_STATUSES.toStream()
                                 .map(Status::statusCode)
                                 .toJavaList());
    }

    public static Arbitrary<StatusCode> uncommonStatusCodes() {
        return integers().between(600, 999)
                         .map(StatusCode::new);
    }
}
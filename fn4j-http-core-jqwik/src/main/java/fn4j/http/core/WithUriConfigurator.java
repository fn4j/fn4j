package fn4j.http.core;

import fn4j.net.uri.Uri;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.configurators.ArbitraryConfiguratorBase;
import net.jqwik.api.providers.TypeUsage;

import static fn4j.http.core.Request.request;
import static fn4j.http.core.RequestHead.requestHead;

public class WithUriConfigurator extends ArbitraryConfiguratorBase {
    @Override
    protected boolean acceptTargetType(TypeUsage targetType) {
        return RequestHead.class.isAssignableFrom(targetType.getRawType());
    }

    public Arbitrary<RequestHead> configure(Arbitrary<RequestHead> arbitrary,
                                            WithUri withUri) {
        return arbitrary.map(requestHead -> {
            var uri = new Uri(withUri.value());
            if (requestHead instanceof Request<?> request) {
                return request(request.method(),
                               uri,
                               request.headers(),
                               request.maybeBody());
            } else {
                return requestHead(requestHead.method(),
                                   uri,
                                   requestHead.headers());
            }
        });
    }
}
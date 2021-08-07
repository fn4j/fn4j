package fn4j.http.core;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.configurators.ArbitraryConfiguratorBase;
import net.jqwik.api.providers.TypeUsage;

import static fn4j.http.core.Request.request;
import static fn4j.http.core.RequestHead.requestHead;

public class WithMethodConfigurator extends ArbitraryConfiguratorBase {
    @Override
    protected boolean acceptTargetType(TypeUsage targetType) {
        return RequestHead.class.isAssignableFrom(targetType.getRawType());
    }

    public Arbitrary<RequestHead> configure(Arbitrary<RequestHead> arbitrary,
                                            WithMethod withMethod) {
        return arbitrary.map(requestHead -> {
            var method = new Method(withMethod.value());
            if (requestHead instanceof Request<?> request) {
                return request(method,
                               request.uri(),
                               request.headers(),
                               request.maybeBody());
            } else {
                return requestHead(method,
                                   requestHead.uri(),
                                   requestHead.headers());
            }
        });
    }
}
package fn4j.http.core;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.configurators.ArbitraryConfiguratorBase;
import net.jqwik.api.providers.TypeUsage;

import static fn4j.http.core.Response.response;
import static fn4j.http.core.ResponseHead.responseHead;

public class WithStatusConfigurator extends ArbitraryConfiguratorBase {
    @Override
    protected boolean acceptTargetType(TypeUsage targetType) {
        return ResponseHead.class.isAssignableFrom(targetType.getRawType());
    }

    public Arbitrary<ResponseHead> configure(Arbitrary<ResponseHead> arbitrary,
                                             WithStatus withStatus) {
        return arbitrary.map(responseHead -> {
            var status = withStatus.reasonPhrase().isEmpty() ?
                    new Status(withStatus.value()) :
                    new Status(withStatus.value(), withStatus.reasonPhrase());

            if (responseHead instanceof Response<?> response) {
                return response(status,
                                response.headers(),
                                response.maybeBody());
            } else {
                return responseHead(status,
                                    responseHead.headers());
            }
        });
    }
}

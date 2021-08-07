package fn4j.http.core;

public class ResponseAssert<B> extends AbstractResponseAssert<ResponseAssert<B>, Response<B>, B> {
    public ResponseAssert(Response<B> actual) {
        super(actual, ResponseAssert.class);
    }
}
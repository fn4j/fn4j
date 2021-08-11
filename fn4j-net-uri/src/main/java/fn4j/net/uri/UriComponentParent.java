package fn4j.net.uri;

import io.vavr.collection.Seq;

public interface UriComponentParent extends UriComponent {
    Seq<UriComponent> components();

    @Override
    default String encode() {
        return components().toStream().map(UriComponent::encode).mkString();
    }
}
package people.api;

public interface TextResourceConsumer {

    void onNewResource(TextResource resource);

    void onComplete();

}

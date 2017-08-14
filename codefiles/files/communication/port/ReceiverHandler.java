package {{communicationPackageName}}.port;

import com.google.common.base.Optional;

import {{communicationPackageName}}.Message;

public interface ReceiverHandler {
    /**
     *
     * @param msg must not be null
     * @return
     */
    Optional<Message> handle(final Message msg);
}

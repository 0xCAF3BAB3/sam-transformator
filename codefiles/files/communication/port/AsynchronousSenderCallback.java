package {{communicationPackageName}}.port;

import {{communicationPackageName}}.Message;

public interface AsynchronousSenderCallback {
    /**
     *
     * @param msg must not be null
     */
    void process(final Message msg);
}

package cross.platform.test.suite.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class PortManager extends ResourceManager<Integer, Integer> {

    private static final Map<Integer, Integer> defaultResourcePool = IntStream.rangeClosed(4795, 4798)
                                                                              .boxed()
                                                                              .collect(Collectors.toMap(e -> e, e -> e));

    public PortManager() {
        super(defaultResourcePool);
    }
    
    public PortManager(Set<Integer> resourcePool) {
        super(resourcePool.stream().collect(Collectors.toMap(e -> e, e -> e)));
    }

    public PortManager(int from, int to) {
        super(IntStream.rangeClosed(from, to).boxed().collect(Collectors.toMap(e -> e, e -> e)));
    }

    @Override
    protected boolean isResourceAvailable(Integer port) {
        try (ServerSocket socket = new ServerSocket()) {
            // setReuseAddress(false) is required only on macOS, 
            // otherwise the code will not work correctly on that platform   
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (IOException ex) {
            log.info("Port {} is occupied!", port);
            return false;
        }
    }
}

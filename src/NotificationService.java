import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationService {
    private final Map<String, Queue<String>> notifications = new ConcurrentHashMap<>();

    public void addNotification(String username, String message) {
        notifications.computeIfAbsent(username, k -> new ConcurrentLinkedQueue<>()).add(message);
    }

    public Queue<String> getNotifications(String username) {
        return notifications.getOrDefault(username, new ConcurrentLinkedQueue<>());
    }
}

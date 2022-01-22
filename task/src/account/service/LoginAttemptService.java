package account.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private final LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void handleLoginFailedForAddress(String key) {
        int attempt = 0;
        try {
            attempt = attemptsCache.get(key);
        } catch (ExecutionException ignored) {
        }
        attempt++;
        attemptsCache.put(key, attempt);
        System.out.println("attempts for " + key + " is " + attempt);
    }

    public void invalidateAllSavedDataFor(String key) {
        attemptsCache.invalidate(key);
    }

    public boolean shouldBlockUser(String key) {
        try {
            int MAX_ATTEMPT = 5;
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}

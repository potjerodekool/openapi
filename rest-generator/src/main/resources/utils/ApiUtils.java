package <packageName>;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public final class ApiUtils {

    private ApiUtils() {
    }

    public static URI createLocation(final HttpServletRequest request, final Object id) {
        final StringBuffer location = request.getRequestURL();

        if (location.charAt(location.length() -1) != '/') {
            location.append('/');
        }

        try {
            return new URI(location.append(id).toString());
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Failed to create location uri", e);
        }
    }
}
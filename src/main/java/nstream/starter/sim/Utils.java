package nstream.starter.sim;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

final class Utils {

  private Utils() {
  }

  public static InputStream loadResource(String resourceName) {
    return Utils.class.getClassLoader()
        .getResourceAsStream(resourceName);
  }

  public static void loadResourceLines(String resourceName, Consumer<String> onLine) {
    try (InputStream is = loadResource(resourceName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
      br.lines().forEach(onLine);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load lines from resource " + resourceName, e);
    }
  }

}

package nstream.starter.sim;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import swim.api.space.Space;
import swim.structure.Record;
import swim.structure.Value;

public final class VehiclesSimulation {

  private VehiclesSimulation() {
  }

  private static final Map<Integer, String> ROUTES = new HashMap<>();

  private static final Map<Integer, List<Location>> LOCATIONS = new HashMap<>();

  public static void seed(Space space) {
    // routes
    loadResourceLines("routes.csv", s -> {
      final String[] split = s.split(",");
      if (split.length > 1) {
        ROUTES.put(Integer.parseInt(split[0]), split[1]);
      }
    });
    // locations
    loadResourceLines("locations.csv", s -> {
      final String[] split = s.split(",");
      if (split.length > 1) {
        final int vehicle = Integer.parseInt(split[0]);
        final List<Location> path;
        final boolean seen;
        if (LOCATIONS.containsKey(vehicle)) {
          path = LOCATIONS.get(vehicle);
          seen = true;
        } else {
          path = new ArrayList<>(500);
          seen = false;
        }
        path.add(new Location(split));
        if (!seen) {
          LOCATIONS.put(vehicle, path);
        }
        space.command("/vehicle/" + vehicle, "unused", Value.extant());
      }
    });
  }

  public static int incrementIdx(int vehicle, int idx) {
    return (idx + 1) % LOCATIONS.get(vehicle).size();
  }

  public static Value structuredLocation(int vehicle, int idx, long timestamp) {
    final Location loc = LOCATIONS.get(vehicle).get(idx);
    return Record.create(9)
        .slot("id", loc.vehicle)
        .slot("routeId", loc.route)
        .slot("dir", loc.inbound ? "inbound" : "outbound")
        .slot("latitude", loc.latitude)
        .slot("longitude", loc.longitude)
        .slot("speed", loc.speed)
        .slot("bearing", loc.bearing.toString())
        .slot("routeName", ROUTES.get(loc.route))
        .slot("timestamp", timestamp);
  }

  public static GenericRecord avroLocation(Value state) {
    return Location.structureToGenericRecord(state);
  }

  private static class Location {

    private final int vehicle;
    private final int route;
    private final boolean inbound;
    private final float latitude;
    private final float longitude;
    private final int speed;
    private final Bearing bearing;

    private Location(String[] split) {
      this.vehicle = Integer.parseInt(split[0]);
      this.route = Integer.parseInt(split[1]);
      this.inbound = "i".equals(split[2]);
      this.latitude = Float.parseFloat(split[3]);
      this.longitude = Float.parseFloat(split[4]);
      this.speed = Integer.parseInt(split[5]);
      this.bearing = Bearing.valueOf(split[6]);
    }

    private enum Bearing {
      N, NE, E, SE, S, SW, W, NW
    }

    private static GenericRecord structureToGenericRecord(Value structure) {
      final GenericRecord result = new GenericData.Record(SCHEMA);
      result.put("id", structure.get("id").intValue());
      result.put("routeId", structure.get("routeId").intValue());
      result.put("dir", new GenericData.EnumSymbol(DIR_SCHEMA, structure.get("dir").stringValue()));
      result.put("latitude", structure.get("latitude").floatValue());
      result.put("longitude", structure.get("longitude").floatValue());
      result.put("speed", structure.get("speed").intValue());
      result.put("bearing", new GenericData.EnumSymbol(BEARING_SCHEMA, structure.get("bearing").stringValue()));
      result.put("routeName", structure.get("routeName").stringValue());
      result.put("timestamp", structure.get("timestamp").longValue());
      return result;
    }

    private static final Schema SCHEMA = SchemaBuilder.record("vehicle").fields()
        .name("id").type().intType().noDefault()
        .name("routeId").type().intType().noDefault()
        .name("dir").type().enumeration("Dir")
            .symbols("inbound", "outbound").noDefault()
        .name("latitude").type().floatType().noDefault()
        .name("longitude").type().floatType().noDefault()
        .name("speed").type().intType().noDefault()
        .name("bearing").type().enumeration("Bearing")
            .symbols(Arrays.stream(Bearing.values()).map(Bearing::name).toArray(String[]::new))
            .noDefault()
        .name("routeName").type().stringType().noDefault()
        .name("timestamp").type().longType().noDefault()
        .endRecord();

    private static final Schema DIR_SCHEMA = SCHEMA.getField("dir").schema();

    private static final Schema BEARING_SCHEMA = SCHEMA.getField("bearing").schema();

  }

  public static InputStream loadResource(String resourceName) {
    return VehiclesSimulation.class.getClassLoader()
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

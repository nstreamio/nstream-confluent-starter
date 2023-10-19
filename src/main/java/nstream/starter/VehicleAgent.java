package nstream.starter;

import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.CommandLane;
import swim.api.lane.MapLane;
import swim.api.lane.ValueLane;
import swim.structure.Value;

/**
 * A skeletal "business-logic" type Web Agent implementation.
 * <p>Note how traits (in this case {@link PolarityMemberAgent}) may supplement
 * {@code VehicleAgents} through a "mixin"-type declaration within {@code
 * server.recon}.
 */
public class VehicleAgent extends AbstractAgent {

  // Message ingestion endpoint
  @SwimLane("addEvent")
  CommandLane<Value> addEvent = this.<Value>commandLane()
      .onCommand(v -> {
        this.latest.set(v);
        this.history.put(v.get("timestamp").longValue(), v);
      });

  // Latest seen value. Also, the "join target lane" for PolarityAgent
  @SwimLane("latest")
  ValueLane<Value> latest = this.<Value>valueLane();

  // Accumulated "time-series" history of ingested values
  @SwimLane("history")
  MapLane<Long, Value> history = this.<Long, Value>mapLane();

}

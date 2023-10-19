package nstream.starter.sim;

import swim.api.SwimLane;
import swim.api.agent.AbstractAgent;
import swim.api.lane.ValueLane;
import swim.concurrent.TimerRef;
import swim.structure.Value;

public class VehicleSimAgent extends AbstractAgent {

  private volatile int idx;
  protected TimerRef simTicker;

  @SwimLane("toPublish")
  protected ValueLane<Value> toPublish;

  protected void onSimTick() {
    final int vehicle = Integer.parseInt(getProp("id").stringValue());
    this.toPublish.set(VehiclesSimulation.structuredLocation(vehicle, this.idx, System.currentTimeMillis()));
    this.idx = VehiclesSimulation.incrementIdx(vehicle, idx);
    this.simTicker.reschedule((long) ((30. + 270 * Math.random()) * 1000L));
  }

  @Override
  public void didStart() {
    super.didStart();
    this.idx = 0;
    try {
      Integer.parseInt(getProp("id").stringValue());
      this.simTicker = setTimer((long) ((5. + 30 * Math.random()) * 1000L), this::onSimTick);
    } catch (Exception e) {
      // swallow, but don't launch timer
    }
  }

}

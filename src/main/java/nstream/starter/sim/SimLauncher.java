package nstream.starter.sim;

import java.io.IOException;
import nstream.adapter.common.provision.ProvisionLoader;
import swim.api.space.Space;
import swim.kernel.Kernel;
import swim.kernel.KernelLoader;
import swim.server.ServerLoader;
import swim.structure.Value;

/**
 * Launcher of an application that populates a Kafka topic with messages.
 * <p>Note that this application itself happens to be a Swim server, enabling
 * demonstration of {@link nstream.adapter.confluent.ConfluentPublishingAgent}.
 */
public final class SimLauncher {

  private SimLauncher() {
  }

  public static void main(String[] args) {
    ProvisionLoader.loadProvisions(simKernelConfig());
    ProvisionLoader.debugProvisionNames();
    final Kernel kernel = ServerLoader.loadServer();
    final Space space = kernel.getSpace("sim");

    kernel.start();
    System.out.println("Running simulator...");
    kernel.run();

    VehiclesSimulation.seed(space);
    // main thread parked as swim server continues to run asynchronously
  }

  private static Value simKernelConfig() {
    final ClassLoader classLoader = SimLauncher.class.getClassLoader();
    try {
      Value kernelConfig = KernelLoader.loadConfig(classLoader);
      if (kernelConfig == null) {
        kernelConfig = KernelLoader.loadConfigResource(classLoader, "sim.recon");
      }
      if (kernelConfig == null) {
        kernelConfig = Value.absent();
      }
      return kernelConfig;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load kernel config", e);
    }
  }

}

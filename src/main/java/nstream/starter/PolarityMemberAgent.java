package nstream.starter;

import nstream.adapter.common.patches.MemberPatch;
import swim.structure.Selector;
import swim.structure.Value;
import swim.uri.Uri;

/**
 * A "low-code" {@link MemberPatch} extension with (in a fresh clone of this
 * repository) a corresponding "no-code" {@link
 * nstream.adapter.common.patches.GroupPatch GroupPatch} described in {@code
 * server.recon}.
 * <p>Note how this trait is added to {@link VehicleAgent} instances in a
 * "mixin"-style fashion within {@code server.recon}.
 */
public class PolarityMemberAgent extends MemberPatch {

  @Override
  protected Value extractGroup() {
    return Selector.identity();
  }

  @Override
  protected Uri getGroupUriFromEvent(Value event) {
    final float indicator = event.get("latitude").floatValue();
    return groupUriPattern().apply(indicator < 34.f ? "south" : "north");
  }

}

package im.dlg.botsdk.utils;

import com.google.common.collect.Sets;
import dialog.Miscellaneous;

import java.util.Set;

public class Constants {
    public static final Set<Miscellaneous.UpdateOptimization> OPTIMISATIONS = Sets.newHashSet(
            Miscellaneous.UpdateOptimization.UPDATEOPTIMIZATION_STRIP_ENTITIES,
            Miscellaneous.UpdateOptimization.UPDATEOPTIMIZATION_STRIP_ENTITIES_V2,
            Miscellaneous.UpdateOptimization.UPDATEOPTIMIZATION_STRIP_COUNTERS,
            Miscellaneous.UpdateOptimization.UPDATEOPTIMIZATION_COMPACT_USERS
    );
}

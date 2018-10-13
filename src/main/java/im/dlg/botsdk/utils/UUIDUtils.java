package im.dlg.botsdk.utils;

import dialog.Definitions;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class UUIDUtils {

    public static UUID convert(Definitions.UUIDValue value) {
        return new UUID(value.getMsb(), value.getLsb());
    }

    public static Definitions.UUIDValue convertToApi(UUID uuid) {
        return Definitions.UUIDValue.newBuilder()
                .setMsb(uuid.getMostSignificantBits())
                .setLsb(uuid.getLeastSignificantBits()).build();
    }

    private static final long START_EPOCH = makeEpoch();

    private static long makeEpoch() {
        // UUID v1 timestamp must be in 100-nanoseconds interval since 00:00:00.000 15 Oct 1582.
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
        c.set(Calendar.YEAR, 1582);
        c.set(Calendar.MONTH, Calendar.OCTOBER);
        c.set(Calendar.DAY_OF_MONTH, 15);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long unixTimestamp(UUID uuid) {
        if (uuid.version() != 1)
            throw new IllegalArgumentException(String.format("Can only retrieve the unix timestamp for version 1 uuid (provided version %d)", uuid.version()));

        long timestamp = uuid.timestamp();
        return (timestamp / 10000) + START_EPOCH;
    }
}

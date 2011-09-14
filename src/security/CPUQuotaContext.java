package security;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/** Custom Context to track when to time out. */
final class CpuQuotaContext extends Context {
  CpuQuotaContext(ContextFactory f) { super(f); }
  long startTimeNanos = System.nanoTime();
}
package security;
import java.util.concurrent.TimeoutException;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;

class SandboxContextFactory extends ContextFactory {
	
	@Override
	protected Context makeContext() {
		Context cx = new CpuQuotaContext(this);
		cx.setWrapFactory(new SandboxWrapFactory());
		cx.setClassShutter(new ClassShutter() {
			public boolean visibleToScripts(String className) {
				return false;
			}
		});
		cx.setInstructionObserverThreshold(10000);
		return cx;
	}
	
	protected void observeInstructionCount(Context cx, int instructionCount) {
	      if (!(cx instanceof CpuQuotaContext)) {
	        throw new IllegalArgumentException();
	      }
	      CpuQuotaContext qcx = (CpuQuotaContext) cx;
	      long currentTime = System.nanoTime();
	      if (currentTime - qcx.startTimeNanos > 5000000000L) {
	        // More then 5 seconds from Context creation time:
	        // it is time to stop the script.
	        // Throw Error instance to ensure that script will never
	        // get control back through catch or finally.
	        throw new Error("Javascript execution timed out");
	      }
	    }

}

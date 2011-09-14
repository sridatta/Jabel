package puzzles;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;

import com.rabbitmq.client.Channel;

abstract public class Solver {
	float totalTime;
	public int numPassed;
	public int totalTests;
	public boolean error;
	public EcmaError errorObj;
	public abstract void run(Scriptable scope, Context cx);
}

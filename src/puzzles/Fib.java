package puzzles;

import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;

import com.rabbitmq.client.Channel;

public class Fib extends Solver {
	float totalTests = 1;
	
	@Override
	public void run(Scriptable scope, Context cx) {
		// TODO Auto-generated method stub
		String failure = "";
		Object fObj = scope.get("add", scope);
		if (!(fObj instanceof Function)) {
			this.error = true;
			this.errorObj = new EcmaError(null, "UserScript", 0, 0, ""); 
		} else {
			Object functionArgs[] = { 2, 3 };
			Function f = (Function)fObj;
			
			// Time the call
			long start = System.nanoTime();
			Object result = f.call(cx, scope, scope, functionArgs);
			this.totalTime = System.nanoTime() - start;
			
			// Try to make this into a double and compare result
			if(Context.toNumber(result) == 5.0){
				System.out.println("Code generated correct answer");
				this.numPassed++;
			}
		}
	}
}

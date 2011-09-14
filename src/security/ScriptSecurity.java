package security;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;


public class ScriptSecurity {

	public static void initSecurity(){
		ContextFactory.initGlobal(new SandboxContextFactory());
	}

}

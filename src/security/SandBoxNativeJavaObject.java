package security;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

class SandboxNativeJavaObject extends NativeJavaObject {
	public SandboxNativeJavaObject(Scriptable scope, Object javaObject, Class staticType) {
		super(scope, javaObject, staticType);
	}

	@Override
	public Object get(String name, Scriptable start) {
		return NOT_FOUND;
	}
}

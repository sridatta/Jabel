
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.mozilla.javascript.*;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import puzzles.Solver;

public class ScriptRunner implements Runnable{
	
	static ConnectionFactory factory = new ConnectionFactory();
	
	static{
		factory.setHost("localhost");
	}
	
	private JSONObject result;
	private Channel channel;
	private String filePath;
	private String puzzleType;
	
	public ScriptRunner(String filePath, String puzzleType, String submissionId){
		
		result= new JSONObject();
		result.put("id",submissionId);
		result.put("error", true);
		result.put("passed", 0);
		result.put("total", 0);
		
		this.filePath= filePath;
		this.puzzleType = puzzleType;		
		try {
			Connection connection = factory.newConnection();
			this.channel = connection.createChannel();
			channel.queueDeclare("node", false, false, false, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Do not forget this line!// Creates and enters a Context. The Context stores information
        // about the execution environment of a script.        
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            
            Scriptable scope = cx.initStandardObjects();

            // We really shouldn't read files without safety checks
            File f = new File(this.filePath);
            Reader r = new FileReader(f);
            cx.evaluateReader(scope, r, "<UserScript>", 1, null);
            
            //Really shouldn't load arbitrary classes like this either. At least it's forced to be in the "puzzle" namespace
            Solver solver = (Solver) this.getClass().getClassLoader().loadClass("puzzles."+this.puzzleType).newInstance();
            solver.run(scope, cx);
            
    		result.put("error", solver.error);
    		if(solver.error){
    			result.put("errorLine", solver.errorObj.lineNumber());
				result.put("errorMessage", solver.errorObj.getErrorMessage());
    		}
    		result.put("passed", solver.numPassed);
    		result.put("total", solver.totalTests);
            
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EcmaError e){
			boolean hackAttempt = e.getErrorMessage().contains("JavaPackage");
			if(hackAttempt) {
				result.put("hack", hackAttempt);
			} else {
				result.put("errorLine", e.lineNumber());
				result.put("errorMessage", e.getErrorMessage());
			}
		} finally {
            // Exit from the context.
            Context.exit();
            try {
				channel.basicPublish("", "node", null, result.toJSONString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

	}



}
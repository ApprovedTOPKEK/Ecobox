/***
 * 
 * 
 * 
 * 
 * 
 *******************************************************************************************************************************************
 *                                                                                                                                         *
 *     /\    DISCLAIMER     UGLY, UN-OPTIMIZED, "ALPHA-PROTOTYPING" CODE                                                                   *
 *    /  \   DISCLAIMER     DO NOT READ FURTHER UNTIL YOU HAVE FOUND A CURE FOR EYE CANCER                                                 *
 *   / !! \  DISCLAIMER     #KAPPA                                                                                                         *
 *  /______\ DISCLAIMER     Seriously though. Don't judge, this was written in a rush and will be improved, revised, and refactored soon.  *
 *                                                                                                                                         *
 *******************************************************************************************************************************************
 *
 *
 *
 *
 * (I'll only warn you once)
 ***/


import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;


public class PyDataScript extends DataScript {

	private PythonInterpreter interpreter;
	private PyObject fun;
	private DataRetriever dr;
	
	public PyDataScript(String name, long interval, PROTOCOL nP, PROTOCOL sP, int priority, String pythonFileName) {
		super(name, interval, nP, sP, priority);
		
		PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
		interpreter = new PythonInterpreter();
		Py.getSystemState().path.append(new PyString("Scripts/"));
		
		interpreter.exec("from " + pythonFileName + " import *");
		interpreter.exec("setup()");
		fun = interpreter.get("retrieveData");
		dr = (DataRetriever) fun.__tojava__(DataRetriever.class); 
	}

	@Override
	public Object retrieveData() {
		 return dr.retrieveData();
	}

	
}

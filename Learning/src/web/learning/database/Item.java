package web.learning.database;

import java.util.Vector;

public class Item {
		
	protected String NAME, VERSIONS, SH_DESCRIPTION, DESCRIPTION, OUTPUT, ERRORS, EXAMPLE;
	protected String EXAMPLE_RESULT;
	protected Vector<String> PARAMETERS;
	
	public Item(){
		NAME = VERSIONS = SH_DESCRIPTION = DESCRIPTION =
				OUTPUT = ERRORS = EXAMPLE = EXAMPLE_RESULT = "";
		PARAMETERS = new Vector<String>();
	}
	
	public String GetName(){
		return NAME;
	}
	public String GetVersions(){
		return VERSIONS;
	}
	public String GetSh_Description(){
		return SH_DESCRIPTION;
	}
	public String GetDescription(){
		return DESCRIPTION;
	}
	public String GetOutput(){
		return OUTPUT;
	}
	public String GetErrors(){
		return ERRORS;
	}
	public String GetExample(){
		return EXAMPLE;
	}
	public String GetExample_result(){
		return EXAMPLE_RESULT;
	}
	public Vector<String> GetParameters(){
		return PARAMETERS;
	}
}

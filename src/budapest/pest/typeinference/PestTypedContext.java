package budapest.pest.typeinference;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PestTypedContext {

	private TreeMap<String, PestType> _varTypes;
	
	public PestTypedContext() 
	{
		_varTypes = new TreeMap<String, PestType>();
	}
	
	public PestTypedContext(PestTypedContext other)
	{
		this();
		addAll(other);
	}
	
	private void addAll(PestTypedContext other)
	{
		for(String var : other.getAllTypedVars())
		{
			add(var, other.getTypeOf(var));
		}
	}
	
	public boolean isTyped(String var)
	{
		return _varTypes.containsKey(var);
	}
	
	public PestType getTypeOf(String var)
	{
		if(!isTyped(var))
		{
			return null;
		}
		
		return _varTypes.get(var);
	}
	
	public void add(String var, PestType type)
	{
		_varTypes.put(var, type);
	}
	
	public void setType(String var, PestType type)
	{
		_varTypes.put(var, type);
	}
	
	public List<String> getAllTypedVars()
	{
		List<String> result = new ArrayList<String>();
		for(String var : _varTypes.keySet())
			result.add(var);
		return result;
	}
		
	public PestTypedContextUnionResult union(PestTypedContext context)
	{
		for(String typedVar : context.getAllTypedVars())
		{
			if(!isTyped(typedVar))
			{
				add(typedVar, context.getTypeOf(typedVar));
			}
			else
			{
				PestType thisType = getTypeOf(typedVar);
				PestType otherType = context.getTypeOf(typedVar);
				PestType unifier = mgu.execute(thisType, otherType);
				if(unifier == null)
				{
					String errorMsg = "Type mismatch. Variable " + typedVar + " cannot be " + 
							thisType.getTypeName() + " and " + 
							otherType.getTypeName();
					
					return new PestTypedContextUnionResult(false, errorMsg);
				}
				else
				{
					for(String newTypedVar : getAllTypedVars())
					{
						PestType newTypedVarType = getTypeOf(newTypedVar);
						if(newTypedVarType.equals(thisType) || newTypedVarType.equals(otherType))
						{
							setType(typedVar, unifier);
						}
					}
				}
			}
		}
		return new PestTypedContextUnionResult(true, "");
	}
	
	public void replaceType(PestType searchType, PestType replacementType)
	{
		for(String typedVar : getAllTypedVars())
		{
			if(getTypeOf(typedVar).equals(searchType))
			{
				setType(typedVar, replacementType);
			}
		}
	}
	
	public String toString() 
	{
		String result = "";
		for(String var : getAllTypedVars())
		{
			result += var + ":" + getTypeOf(var).getTypeName() + ", ";
		}
		if(result.length() > 0)
		{
			result = result.substring(0, result.length()-2);
		}
		result = "{" + result + "}";
		
		return result;
	}
}
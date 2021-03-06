package it.polito.netgroup.selforchestratingservices.compiler.model.json;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.polito.netgroup.selforchestratingservices.compiler.model.json.event.StateDescription;

public class ResourceTemplateDescription implements GenerateJavaClass
{
	@JsonProperty("id")
	public String id;
	@JsonProperty("resource")
	public String resource;
	@JsonProperty("default_port_connections")
	public Map<String,String> default_port_connections;
	@JsonProperty("default_configuration")
	public String default_configuration; //TODO

	private String getDefaultFlowRulesJavaCode()
	{
		String java = "\t\tList<DeclarativeFlowRule> default_flowrules = new ArrayList<DeclarativeFlowRule>();\n" +
				"\t\t\n";

		Integer counter = 0;
		for( Entry<String, String> c : default_port_connections.entrySet())
		{
			java += "\t\tDeclarativeFlowRule dfr"+counter+" = new DeclarativeFlowRuleImpl();\n";

			java += "\t\tdfr"+counter+".linkPorts(\""+c.getKey()+"_"+c.getValue()+"\",\""+c.getKey()+"\",\""+c.getValue()+"\");\n";

			java += "\t\tdefault_flowrules.add(dfr"+counter+");\n";

			counter++;
		}
		java += "\t\treturn default_flowrules;\n";

		return java;
	}

	private String getDefaultConfigurationJavaCode()
	{
		String java = "\t\tNatConfiguration default_cfg = new NatConfiguration(null, null, null,null);\n" +
				"\t\ttry\n" +
				"\t\t\t{\n" +
				"\t\t\t\tdefault_cfg.setIP(new InterfaceLabel(\"User\"), new IpAddressAndNetmask(\"192.168.10.1\", \"255.255.255.0\") , new MacAddress(\"02:01:02:03:04:05\"));\n" +
				"\t\t}catch(InvalidInterfaceLabel e)\n" +
				"\t\t{\n" +
				"\t\t\te.printStackTrace();\n" +
				"\t\t\treturn null;\n" +
				"\t\t}\n" +
				"\t\treturn default_cfg;\n";

		return java;
	}


	@Override
	public String getJavaClass(String prefix, SelfOrchestratorModel model, String pack)
	{
		String java = "package "+pack+";\n" + 
				"\n" +
				PackageGenerator.getPackage() +
				"\n" + 
				"//Autogenerated file\n" + 
				"public class "+getJavaClassName(prefix)+" implements ResourceTemplate\n" + 
				"{\n" + 
				"\tpublic "+getJavaClassName(prefix)+"()\n" + 
				"\t{\n" + 
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic List<DeclarativeFlowRule> getDefaultFlowRules() {\n" +

				getDefaultFlowRulesJavaCode() +

				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic ConfigurationSDN getDefaultConfiguration() {\n" +

				getDefaultConfigurationJavaCode() +

				"\t}\n" +
				"\t@Override\n" + 
				"\tpublic Class<? extends Resource> getType()\n" +
				"\t{\n" + 
				"\t\treturn "+resource+".class;\n" +
				"\t}\n" + 
				"\n" +
				"\t@Override\n" + 
				"\tpublic void init(Variables var, Resource resource, Framework framework) throws Exception\n" +
				"\t{\n";

		for (StateDescription state : model.getStateForType(resource)) {
			java += state.getJavaInit(model, 2);
		}



		java +=	"\t\t\n" + 
				"\t}"+
				"}";	
		return java;
	}
	@Override
	public String getJavaClassName(String prefix)
	{
		String className = prefix+resource;
		return Character.toUpperCase(className.charAt(0)) + className.substring(1) +"ResourceTemplate";
	}
}

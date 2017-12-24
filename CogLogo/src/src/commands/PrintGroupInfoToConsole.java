package src.commands;

import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;

public class PrintGroupInfoToConsole implements Command{
	
	// int : log detail
	public Syntax getSyntax(){
		return SyntaxJ.commandSyntax(new int[] {Syntax.NumberType()});
	}

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		//Double level = args[0].getDoubleValue();
		
		int count = 0;
		for(CognitiveScheme cs : CognitonExtension.cognitiveSchemes.values())
			count += cs.groupInstances.values().size();
		CognitonExtension.myConsole.println("GROUP INFO: Group in memory : " + count);		
	}
}

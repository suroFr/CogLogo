//    CogLogo : an implementation of the Cogniton architecture
//    Copyright (C) 2017  SURO François (suro@lirmm.fr)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.


package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.PrimitiveManager;

import src.commands.ActivateCogniton;
import src.commands.AddToCogniton;
import src.commands.AddToCulturon;
import src.commands.AddToParticipation;
import src.commands.ChooseNextPlan;
import src.commands.CognitonEditor;
import src.commands.CreateAndJoinGroup;
import src.commands.DeactivateCogniton;
import src.commands.FeedBackFromPlan;
import src.commands.GetCogniton;
import src.commands.GetCulturon;
import src.commands.GetGroupId;
import src.commands.GetGroupRoleId;
import src.commands.InitCognitons;
import src.commands.JoinGroup;
import src.commands.LeaveAllGroups;
import src.commands.LeaveGroup;
import src.commands.PrintGroupInfoToConsole;
import src.commands.ReportAgentData;
import src.commands.ResetSimulation;
import src.commands.SetCogniton;
import src.commands.SetCulturon;
import src.commands.SetParticipation;
import src.editor.MainPanel;
import src.myutils.GroupInstanceScrubber;

public class CognitonExtension extends DefaultClassManager {
	public static JFrame frame;
	public static MainPanel mainPanel;
	public static HashMap<String, CognitiveScheme> cognitiveSchemes = new HashMap<>();
	public static boolean isLoaded = false;
	static String rawPath = null;
	static String modelPath = null;
	static String modelName = null;
	static String cogSchemeFolder = null;
	static String cogSchemeFolderTag = "_Cognitive_Schemes";
	static String cogSchemeFileTag = ".cogscheme";
	public static PrintStream myConsole = null;
	public static Context modelContext = null;
	static HashMap<String, Integer> breedVarOffset = new HashMap<>();
	Timer groupScrubberTimer;
	int groupScrubInterval = 60000 * 1;
	
	public CognitonExtension() {
		groupScrubberTimer = new Timer(groupScrubInterval, new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	        		if(myConsole != null && modelContext != null)
	        			GroupInstanceScrubber.scrub(cognitiveSchemes.values());
	            }    
	        });
		groupScrubberTimer.start();
	}

	public void load(PrimitiveManager primitiveManager) {
		makeEditorPanel();
		// misc
		primitiveManager.addPrimitive("OpenEditor", new CognitonEditor());
		primitiveManager.addPrimitive("report-agent-data", new ReportAgentData());
		primitiveManager.addPrimitive("choose-next-plan", new ChooseNextPlan());
		primitiveManager.addPrimitive("feed-back-from-plan", new FeedBackFromPlan());
		primitiveManager.addPrimitive("reset-simulation", new ResetSimulation());
		primitiveManager.addPrimitive("group-info-to-console", new PrintGroupInfoToConsole());
		// Cognitons
		primitiveManager.addPrimitive("init-cognitons", new InitCognitons());
		primitiveManager.addPrimitive("add-to-cogniton-value", new AddToCogniton());
		primitiveManager.addPrimitive("set-cogniton-value", new SetCogniton());
		primitiveManager.addPrimitive("get-cogniton-value", new GetCogniton());
		primitiveManager.addPrimitive("activate-cogniton", new ActivateCogniton());
		primitiveManager.addPrimitive("deactivate-cogniton", new DeactivateCogniton());
		// groups
		primitiveManager.addPrimitive("add-to-participation", new AddToParticipation());
		primitiveManager.addPrimitive("set-participation", new SetParticipation());
		primitiveManager.addPrimitive("create-and-join-group", new CreateAndJoinGroup());
		primitiveManager.addPrimitive("join-group", new JoinGroup());
		primitiveManager.addPrimitive("get-group-id", new GetGroupId());
		primitiveManager.addPrimitive("get-group-role-id", new GetGroupRoleId());
		primitiveManager.addPrimitive("leave-group", new LeaveGroup());
		primitiveManager.addPrimitive("leave-all-groups", new LeaveAllGroups());
		primitiveManager.addPrimitive("add-to-culturon-value", new AddToCulturon());
		primitiveManager.addPrimitive("set-culturon-value", new SetCulturon());
		primitiveManager.addPrimitive("get-culturon-value", new GetCulturon());
	}

	private static void updateFilePaths(String modelPath)
	{
		String[] pth;
		rawPath = modelPath;
		pth = modelPath.split(Pattern.quote(System.getProperty("file.separator")));
		modelPath = "";
		for (int i = 0; i < pth.length - 1; i++)
			modelPath += pth[i] + System.getProperty("file.separator");
		modelName = (pth[pth.length - 1]);
		modelName = modelName.split("\\.")[0];
		cogSchemeFolder = modelPath + modelName + cogSchemeFolderTag;
	}
	
	public static void loadCognitiveSchemes(String path) {
		
		if (!isLoaded) {
			updateFilePaths(path);
			File settingsDirectory = new File(cogSchemeFolder);

			if (settingsDirectory.exists()) {
				File[] csFiles = settingsDirectory.listFiles();
				if (csFiles != null && csFiles.length > 0) {
					for (File curFile : csFiles) {
						if (curFile.getName().equals("Setting.cfg"))
							loadSetting(curFile);
						else
							loadCognitiveScheme(curFile);
					}
				}
			}
		}
		else
		{
			if(!rawPath.equals(path))
			{
				updateFilePaths(path);
				saveAllCognitiveScheme();
			}
		}
		isLoaded = true;
	}

	private static void loadSetting(File settingFile) {
		if (settingFile != null && settingFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(settingFile));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (br != null) {
				String line = null;
				try {
					while ((line = br.readLine()) != null) {
						line = "breed to cogScheme goes here";
						line.isEmpty();
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void saveSettings() {

		FileWriter output = null;
		File settingsDirectory = new File(cogSchemeFolder);
		settingsDirectory.mkdirs();
		File settingsFile = new File(
				cogSchemeFolder + System.getProperty("file.separator") + "Setting.cfg");
		try {
			settingsFile.createNewFile();
			output = new FileWriter(settingsFile);
			output.write("SETTINGS" + "\n");
			output.flush();
			output.close();
		} catch (Exception e) {}
	}

	public static void loadCognitiveScheme(File csFile) {

		if (csFile != null && csFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csFile));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (br != null) {
				String line = null;
				try {
					if ((line = br.readLine()) != null && line.equals("STARTCS")) {
						line = br.readLine();
						CognitiveScheme cs = new CognitiveScheme(line);
						line = br.readLine();
						if (line != null && !line.equals("COG") && !line.equals("ENDCS")) {
							cs.myDecisionMaker = line.split(" ")[0];
							if(line.split(" ").length > 1)
								cs.DecisionMakerBias = Double.parseDouble(line.split(" ")[1]);
							line = br.readLine();
						}
						if (line != null && !line.equals("COG")&& !line.equals("BREEDS") && line.equals("PLANPOS")) {
							line = br.readLine();
							while(line != null && !line.equals("BREEDS") && !line.equals("COG") && !line.equals("ENDCS"))
							{
								cs.planSavedPosition.put(line.split("=")[0], new Point(Integer.parseInt(line.split("=")[1].split(":")[0]),Integer.parseInt(line.split("=")[1].split(":")[1])));
								line = br.readLine();
							}
						}
						if (line != null && !line.equals("COG")&& line.equals("BREEDS")) {
							line = br.readLine();
							while (line != null && !line.equals("COG") && !line.equals("ENDCS")) {
								cs.linkedBreeds.add(line);
								line = br.readLine();
							}
						}
						while (line != null && line.equals("COG")) 
						{
							CognitiveScheme.Cogniton curCog = cs.addCogniton(br.readLine(), false);
							curCog.customColor = new Color(Integer.parseInt(br.readLine()));
							if ((line = br.readLine()) != null && !line.equals("INFLUENCE")) 
							{
								if (line.toUpperCase().equals("TRUE"))
									curCog.starting = true;
								line = br.readLine();
							}
							if (line != null && !line.equals("INFLUENCE")) 
							{
								curCog.startingValue = Double.parseDouble(line);
								line = br.readLine();
							}
							if (line != null && !line.equals("INFLUENCE")) 
							{
								curCog.posX = Integer.parseInt(line.split(":")[0]);
								curCog.posY = Integer.parseInt(line.split(":")[1]);
								line = br.readLine();
							}
							if (line != null && line.equals("INFLUENCE")) 
							{
								while ((line = br.readLine()) != null && !line.equals("DEPENDENCY")
										&& !line.equals("REINFORCEMENT") && !line.equals("COG")
										&& !line.equals("ENDCS") && !line.equals("GRP")) 
								{
									cs.addInfluenceLink(curCog.Name, Double.parseDouble(line.split("=")[1]),
											line.split("=")[0]);
								}
							}
							if (line != null && line.equals("DEPENDENCY"))
							{
								while ((line = br.readLine()) != null && !line.equals("REINFORCEMENT")
											&& !line.equals("COG")
											&& !line.equals("ENDCS") && !line.equals("GRP"))
								{
									cs.addDependencyLink(curCog.Name, line);									
								}
							}
							if (line != null && line.equals("REINFORCEMENT")) 
							{
								while ((line = br.readLine()) != null && !line.equals("COG")
										&& !line.equals("ENDCS") && !line.equals("GRP")) 
								{
									cs.addReinforcementLink(curCog.Name, Double.parseDouble(line.split("=")[1]),
											line.split("=")[0]);
								}
							}
						}
						while (line != null && line.equals("GRP")) 
						{
							String grp = br.readLine();
							cs.addGroup(grp);
							line = br.readLine();
							while (line != null && line.equals("ROL")) 
							{
								String rol = br.readLine();
								cs.addRole(grp, rol);
								line = br.readLine();
								while (line != null && line.equals("COG")) 
								{
									CognitiveScheme.Cogniton curCult= cs.addCulturon(grp, rol, br.readLine());
									curCult.customColor = new Color(Integer.parseInt(br.readLine()));
									if ((line = br.readLine()) != null && !line.equals("INFLUENCE")) {
										curCult.startingValue = Double.parseDouble(line);
										line = br.readLine();
									}
									if (line != null && !line.equals("INFLUENCE")) 
									{
										curCult.posX = Integer.parseInt(line.split(":")[0]);
										curCult.posY = Integer.parseInt(line.split(":")[1]);
										line = br.readLine();
									}
									if (line != null && line.equals("INFLUENCE")) {
										while ((line = br.readLine()) != null && !line.equals("DEPENDENCY") 
												&& !line.equals("REINFORCEMENT") && !line.equals("COG")
												&& !line.equals("ENDCS") && !line.equals("GRP")
												&& !line.equals("ROL")){
											cs.addCulturonInfluenceLink(grp, rol, curCult.Name,
													Double.parseDouble(line.split("=")[1]), line.split("=")[0]);
										}
									}
									if (line != null && line.equals("DEPENDENCY")) {
										while ((line = br.readLine()) != null 
												&& !line.equals("REINFORCEMENT") && !line.equals("COG")
												&& !line.equals("ENDCS") && !line.equals("GRP")
												&& !line.equals("ROL")){
												cs.addCulturonDependencyLink(grp, rol, curCult.Name, line);}
									}
									if (line != null && line.equals("REINFORCEMENT"))
									{
										while ((line = br.readLine()) != null && !line.equals("COG")
												&& !line.equals("ENDCS") && !line.equals("GRP")
												&& !line.equals("ROL"))
											cs.addCulturonReinforcementLink(grp, rol, curCult.Name,
													Double.parseDouble(line.split("=")[1]), line.split("=")[0]);
									}
								}
							}
						}
				       CognitonExtension.myConsole.println("INIT : cognitive scheme < "+cs.Name+" > loaded");
						cognitiveSchemes.put(cs.Name.toLowerCase(), cs);
						mainPanel.newCogSchemeMenuItem(cs);
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void saveAllCognitiveScheme() {
		for(CognitiveScheme cs : cognitiveSchemes.values())
			saveCognitiveScheme(cs);
	}

	public static void saveCognitiveScheme(CognitiveScheme cs) {

		if (cs != null) {
			FileWriter output = null;
			File settingsDirectory = new File(cogSchemeFolder);
			settingsDirectory.mkdirs();
			File cogSchemeFile = new File(cogSchemeFolder + System.getProperty("file.separator")
					+ cs.Name + cogSchemeFileTag);
			try {
				cogSchemeFile.createNewFile();
				output = new FileWriter(cogSchemeFile);
				output.write("STARTCS\n");
				output.write(cs.Name + "\n");
				output.write(cs.myDecisionMaker + " " + cs.DecisionMakerBias+ "\n");
				if(cs.planSavedPosition.size() > 0)
				{
					output.write("PLANPOS\n");
					for(String p : cs.planSavedPosition.keySet())
						output.write(p + "=" + (int)cs.planSavedPosition.get(p).getX() + ":" + (int)cs.planSavedPosition.get(p).getY() + "\n");
				}
				output.write("BREEDS\n");
				for (String br : cs.linkedBreeds)
					output.write(br + "\n");	
				for (CognitiveScheme.Cogniton c : cs.cognitons) {
					output.write("COG\n");
					output.write(c.Name + "\n");
					output.write(Integer.toString(c.customColor.getRGB()) + "\n");
					output.write(Boolean.toString(c.starting) + "\n");
					output.write(Double.toString(c.startingValue) + "\n");
					if(c.posX != null)
						output.write(Integer.toString(c.posX)+":"+Integer.toString(c.posY)+ "\n");
					output.write("INFLUENCE\n");
					for (String inf : c.influencelinks.keySet())
						output.write(inf + "=" + c.influencelinks.get(inf).toString() + "\n");
					output.write("DEPENDENCY\n");
					for (String dep : c.dependencyLinks)
						output.write(dep + "\n");
					output.write("REINFORCEMENT\n");
					for (String rf : c.feedBackLinks.keySet())
						output.write(rf + "=" + c.feedBackLinks.get(rf).toString() + "\n");
				}
				for (int g = 0; g < cs.groupNames.size(); g++) {
					output.write(cs.groupDescriptions.get(g).toFileString());
				}
				output.write("ENDCS\n");
				output.flush();
				output.close();
			} catch (Exception e) {}
		}
	}

	private void makeEditorPanel() {
		frame = new JFrame("Cognitons editor");
		mainPanel = new MainPanel(frame);
		frame.add(mainPanel);
		frame.setResizable(true);
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setVisible(false);
	}

	public static void deleteCognitiveScheme(CognitiveScheme cs) {
		File cogSchemeFile = new File(
				cogSchemeFolder + System.getProperty("file.separator") + cs.Name + cogSchemeFileTag);
		if (cogSchemeFile.exists())
			cogSchemeFile.delete();
		cognitiveSchemes.remove(cs.Name.toLowerCase());
	}

	public static void updateContext(Context context) {
		modelContext = context;
		loadCognitiveSchemes(modelContext.workspace().getModelPath());
	}
	
	public static synchronized void setBreedVariableOffset(int i, String br) {
		breedVarOffset.put(br.toLowerCase(), i);
	}

	public static int getBreedVariableOffset(String br) {
		return breedVarOffset.get(br.toLowerCase());
	}
}
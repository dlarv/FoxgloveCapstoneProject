package gui;
/*UMGC CAPSTONE PROJECT
 * Title:Game of Life in Java By Team Fox Glove:
 *         Anthony Farias
           Mitchell Howard
           Patrick Kamdem
           Hyrum Madson
           Bensaiten Sanchez Flores 
 * 
 *CMSC 495 7380
 *Professor Sanford
 *February 12, 2024 
 *
 * Displays all rules that are currently active.
 * Allows the user to move, add, and remove rules.
 */

import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import data.RuleSet;
import rules.Rule;
import simulation.Cell;

public class RulesListGUI extends JPanel {
	private JScrollPane scrollPane;
	private JPanel rulesList;
	private ArrayList<RuleGUI> ruleElements;

	// Holds list of radio buttons connected to RuleGUI objects.
	// This way, only one rule can be selected for editing at a time.
	private ButtonGroup selectButtons;

	public RulesListGUI(JFrame frame, Rule[] rules) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		rulesList = new JPanel();
		rulesList.setLayout(new BoxLayout(rulesList, BoxLayout.PAGE_AXIS));

		// Add list of rules to scroll pane.
		scrollPane = new JScrollPane(rulesList);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);

		buildRulesList(rules);

		// Add editing buttons at bottom.
		JPanel buttonsPanel = new JPanel();
		JButton moveUpButton = new JButton("Up");
		JButton moveDownButton = new JButton("Down");
		JButton newButton = new JButton("New");
		JButton removeButton = new JButton("Remove");
		add(buttonsPanel);
		
		// Add editing buttons to their panel.
		buttonsPanel.add(moveUpButton);
		buttonsPanel.add(moveDownButton);
		buttonsPanel.add(newButton);
		buttonsPanel.add(removeButton);
	
		// Add action listeners to buttons.
		moveUpButton.addActionListener(new MoveUpRule());
		moveDownButton.addActionListener(new MoveDownRule());
		newButton.addActionListener(new NewRule(frame));
		removeButton.addActionListener(new RemoveRule());
	}
	
	public RulesListGUI(JFrame frame) {
		new RulesListGUI(frame, RuleSet.asArray());
	}
	/* Creates GUI element 
	 * Assumes rulesList is already initialized.
	 */
	private void buildRulesList(Rule[] rules) {
		ruleElements = new ArrayList<>();

		selectButtons = new ButtonGroup();

		RuleGUI element;
		for(Rule rule: rules) {
			element = new RuleGUI(rule);
			selectButtons.add(element.getSelectButton());
			rulesList.add(element);
			ruleElements.add(element);
		}

		scrollPane.setViewportView(rulesList);
		repaint();
	}

	/*
	 * Automatically get list of rules from Cell class and recreate GUI.
	 */
	public void refresh() {
		rulesList.removeAll();
		buildRulesList(RuleSet.asArray());
	}
	
	private int getActiveButtonIndex() {
		for (int i = 0; i < ruleElements.size(); i++) {
			RuleGUI gui = ruleElements.get(i);
			if(gui.isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	// Helper method to delete everything related to a rule.
	private void removeRule(int index) {
		RuleSet.remove(index);
		rulesList.remove(index);
		ruleElements.remove(index);
	}
	
	// Helper method to change the position of a rule.
	private void moveRule(int oldIndex, int newIndex) {
		RuleSet.moveRule(oldIndex, newIndex);
		
		RuleGUI gui = ruleElements.remove(oldIndex);
		ruleElements.add(newIndex, gui);

		rulesList.remove(oldIndex);
		rulesList.add(gui, newIndex);
	}

	/* Action listeners */ 

	// Open new rule dialog window.
	// Append rule to list.
	public class NewRule implements ActionListener {
		JFrame dialogOwner;

		public NewRule(JFrame f) {
			dialogOwner = f;
		}

		public void actionPerformed(ActionEvent e) {
			// Open dialog window and extract Rule.
			// This is a modal window, so program will wait for it to close before continuing.
			RuleBuilderDialog dialog = new RuleBuilderDialog(dialogOwner);
			Rule newRule = dialog.getRule();
			
			// User canceled operation.
			if(newRule == null)
				return;

			// Add new rule to RuleSet.
			RuleSet.add(newRule);

			// Add new rule to gui.
			refresh();

			// Debug
			System.out.println("Added new rule");
		}
	}
	// Delete selected rule.
	public class RemoveRule implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = getActiveButtonIndex();
			
			// If no button is selected.
			if(index == -1) 
				return;

			// Item must be deleted from RuleSet, ruleElements
			removeRule(index);
			scrollPane.setViewportView(rulesList);
			System.out.println("Removed rule " + index);
		}
	}
	// Swap selected rule and the rule above it.
	public class MoveUpRule implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = getActiveButtonIndex();
			
			// If no button is selected or very first button is selected.
			if(index <= 0) 
				return;
			
			moveRule(index, index - 1);
			scrollPane.setViewportView(rulesList);
			System.out.println("Moved rule " + index + " up");
		}
	}
	// Swap selected rule and the rule below it.
	public class MoveDownRule implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int index = getActiveButtonIndex();
			
			// If no button is selected or last element is selected.
			if(index == -1 || index >= ruleElements.size() - 1) 
				return;
			
			moveRule(index, index + 1);
			scrollPane.setViewportView(rulesList);
			System.out.println("Moved rule " + index + " down");
		}
	}
}

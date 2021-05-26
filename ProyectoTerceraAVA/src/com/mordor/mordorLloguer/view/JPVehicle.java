package com.mordor.mordorLloguer.view;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;

public class JPVehicle extends JPanel {
	private JTextField txtRegistration;
	private JTextField txtModel;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public JPVehicle() {
		
		JPanel panel = new JPanel();
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(59, Short.MAX_VALUE))
		);
		panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnAdd = new JButton("Add");
		panel_2.add(btnAdd);
		
		JButton btnDelete = new JButton("Delete");
		panel_2.add(btnDelete);
		
		JButton btnEdit = new JButton("Edit");
		panel_2.add(btnEdit);
		
		JButton btnCancel = new JButton("Cancel");
		panel_2.add(btnCancel);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblRegistration = new JLabel("Registration");
		panel.add(lblRegistration);
		
		txtRegistration = new JTextField();
		panel.add(txtRegistration);
		txtRegistration.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Model");
		panel.add(lblNewLabel);
		
		txtModel = new JTextField();
		panel.add(txtModel);
		txtModel.setColumns(10);
		
		JLabel lblEngin = new JLabel("Engine");
		panel.add(lblEngin);
		
		JComboBox comboBoxEngine = new JComboBox();
		panel.add(comboBoxEngine);
		
		JLabel lblLicense = new JLabel("License");
		panel.add(lblLicense);
		
		JComboBox comboBoxLicense = new JComboBox();
		panel.add(comboBoxLicense);
		setLayout(groupLayout);

	}
}
